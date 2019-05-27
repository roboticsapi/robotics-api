/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.action.JointErrorCorrection;
import org.roboticsapi.multijoint.action.JointMotionPlan;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActionMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.fragments.JointCorrectionFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointErrorCorrectionMapper implements ActionMapper<SoftRobotRuntime, JointErrorCorrection> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, JointErrorCorrection action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		final ActionMapperResult innerAction = runtime.getMapperRegistry().mapAction(runtime, action.getWrappedMotion(),
				parameters, ports.cancelPort, ports.overridePort, ports.actionPlans);
		double duration = 1;

		if (!ports.actionPlans.containsKey(action)) {
			try {
				action.calculatePlan(ports.actionPlans, parameters);
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
		}
		JointMotionPlan plan = (JointMotionPlan) ports.actionPlans.get(action);

		if (plan != null) {
			duration = plan.getTotalTime() * 0.45;
		}

		NetFragment fragment = new NetFragment("JointErrorCorrection");
		DoubleSensorMapperResult[] realPos = new DoubleSensorMapperResult[action.getJoints().size()];
		SensorMappingContext context = new SensorMappingContext();
		for (int i = 0; i < action.getJoints().size(); i++) {
			DoubleSensor sensor = action.getJoints().get(i).getDriver().getCommandedPositionSensor();
			realPos[i] = (DoubleSensorMapperResult) runtime.getMapperRegistry().mapSensor(runtime, sensor, fragment,
					context);
		}

		final Clock clock = fragment.add(new Clock(1.0));

		DataflowOutPort overridePort = ports.overridePort;

		fragment.connect(overridePort, fragment.addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
		DataflowOutPort time = fragment.addOutPort(new DoubleDataflow(), false, clock.getOutValue());

		JointCorrectionFragment ret = new JointCorrectionFragment(action.getStart(), realPos, duration, 0, time,
				innerAction.getNetFragment(), innerAction.getActionResult().getOutPort());
		fragment.add(ret);

		ActionResult result = new JointPositionActionResult(ret.getResultPort());

		return new WrappedActionMapperResult(action, action.getWrappedMotion(), fragment, result, innerAction);
	}
}
