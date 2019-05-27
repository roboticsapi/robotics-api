/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.action.FollowJointGoal;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class FollowJointGoalMapper implements ActionMapper<SoftRobotRuntime, FollowJointGoal> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, FollowJointGoal action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		final int numAxis = action.getSensors().length;

		NetFragment composedNetFragment = new NetFragment("FollowPositionOTG_comp");

		OutPort[] destJoints = new OutPort[numAxis];
		SensorMappingContext context = new SensorMappingContext();
		for (int i = 0; i < numAxis; i++) {
			DoubleSensor sensor = action.getSensors()[i];
			SensorMapperResult<?> sensorFragment = runtime.getMapperRegistry().mapSensor(runtime, sensor,
					composedNetFragment, context);

			// connect sensor values with array for net outports
			destJoints[i] = sensorFragment.getSensorPort().getPorts().get(0);
		}

		NetFragment actionFragment = new NetFragment("FollowPositionOTG_action");

		// add ActionResult outport with 7 values from sensors
		DataflowOutPort resultPort = actionFragment.addOutPort(new JointsDataflow(numAxis, null), true, destJoints);

		composedNetFragment.add(actionFragment);

		return new CancellationActionMapperResult(action, composedNetFragment, new JointGoalActionResult(resultPort),
				ports.cancelPort);
	}

	public static final class CancellationActionMapperResult extends BaseActionMapperResult {

		private CancellationActionMapperResult(Action action, NetFragment fragment, ActionResult result, DataflowOutPort cancel) {
			super(action, fragment, result, cancel); // completed when cancelled

			addExceptionPort(ActionCancelledException.class, cancel);
		}

	}

}
