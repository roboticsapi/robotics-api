/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.action.CartesianErrorCorrection;
import org.roboticsapi.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.CartesianCorrectionFragment;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.world.sensor.TransformationSensor;

public class CartesianErrorCorrectionMapper implements ActionMapper<SoftRobotRuntime, CartesianErrorCorrection> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, CartesianErrorCorrection action,
			DeviceParameterBag parameters, ActionMappingContext ports) throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("CartesianErrorCorrection");

		if (!ports.actionPlans.containsKey(action)) {
			try {
				action.calculatePlan(ports.actionPlans, parameters);
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
		}
		CartesianMotionPlan plan = (CartesianMotionPlan) ports.actionPlans.get(action);

		final ActionMapperResult innerAction = runtime.getMapperRegistry().mapAction(runtime, action.getWrappedMotion(),
				parameters, ports.cancelPort, ports.overridePort, ports.actionPlans);
		double duration = 1;
		if (plan != null) {
			duration = plan.getTotalTime() * 0.45;
		}
		fragment.add(innerAction.getNetFragment());

		TransformationSensor realPos = action.getBase().getRelationSensor(action.getFrame()).getTransformationSensor();
		TransformationSensorMapperResult realSensor = (TransformationSensorMapperResult) runtime.getMapperRegistry()
				.mapSensor(runtime, realPos, fragment, null);

		final Clock clock = fragment.add(new Clock(1.0));

		DataflowOutPort overridePort = ports.overridePort;

		fragment.connect(overridePort, fragment.addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
		DataflowOutPort time = fragment.addOutPort(new DoubleDataflow(), false, clock.getOutValue());

		CartesianCorrectionFragment ret = fragment.add(new CartesianCorrectionFragment(action.getStart(),
				realSensor.getSensorPort(), duration, 0, time, innerAction.getActionResult().getOutPort()));

		ActionResult result = new CartesianPositionActionResult(ret.getResultPort());

		return new WrappedActionMapperResult(action, action.getWrappedMotion(), fragment, result, innerAction);
	}
}
