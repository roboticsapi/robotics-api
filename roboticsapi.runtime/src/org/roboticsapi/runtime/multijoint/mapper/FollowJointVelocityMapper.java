/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.action.FollowJointVelocity;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.CycleTime;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.DoubleValue;
import org.roboticsapi.runtime.core.primitives.OTG2;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class FollowJointVelocityMapper implements ActionMapper<SoftRobotRuntime, FollowJointVelocity> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, FollowJointVelocity action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		JointDeviceParameters jointParameters = parameters.get(JointDeviceParameters.class);

		if (jointParameters.getJointCount() != action.getJointCount()) {
			throw new MappingException("Illegal number of joints found !");
		}

		// current joint position of device
		InPort[] currentJointPosIns = new InPort[action.getJointCount()];

		// desired joint position of device
		OutPort[] desiredJointPosOuts = new OutPort[action.getJointCount()];

		NetFragment fragment = new NetFragment("SoftRobot Follow Joint Velocity Action");

		// making jogging cycle-time invariant
		CycleTime tick = new CycleTime();
		fragment.add(tick);

		DataflowOutPort overridePort = ports.overridePort;

		SensorMappingContext context = new SensorMappingContext();
		// processing jogging input for each axis
		for (int i = 0; i < action.getJointCount() - 1; i++) {
			JointParameters jparam = jointParameters.getJointParameters(i);

			// limit velocity
			DoubleSensor velocity = action.getSensor(i);
			Double limit = action.getLimit(i);

			if (limit != null) {
				velocity = velocity.limit(-limit, limit);
			}

			SensorMapperResult<Double> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime, velocity, fragment,
					context);

			// input velocity is scaled/multiplied by override
			DoubleMultiply scaledVelocity = fragment.add(new DoubleMultiply());

			fragment.connect(mappedSensor.getSensorPort(), scaledVelocity.getInFirst(), new DoubleDataflow());
			fragment.connect(overridePort, scaledVelocity.getInSecond(), new DoubleDataflow());

			// max acceleration is scaled/multiplied by override^2
			DoubleValue maxAcc = fragment.add(new DoubleValue(jparam.getMaximumAcceleration()));
			DoubleMultiply scaledMaxAcc = fragment.add(new DoubleMultiply());

			DoubleMultiply override2 = fragment.add(new DoubleMultiply());
			fragment.connect(overridePort, override2.getInFirst(), new DoubleDataflow());
			fragment.connect(overridePort, override2.getInSecond(), new DoubleDataflow());

			fragment.connect(maxAcc.getOutValue(), scaledMaxAcc.getInFirst());
			fragment.connect(override2.getOutValue(), scaledMaxAcc.getInSecond());

			// max jerk is scaled/multiplied by override^3
			DoubleValue maxJerk = fragment.add(new DoubleValue(jparam.getMaximumJerk()));
			DoubleMultiply scaledMaxJerk = fragment.add(new DoubleMultiply());

			DoubleMultiply override3 = fragment.add(new DoubleMultiply());
			fragment.connect(override2.getOutValue(), override3.getInFirst());
			fragment.connect(overridePort, override3.getInSecond(), new DoubleDataflow());

			fragment.connect(maxJerk.getOutValue(), scaledMaxJerk.getInFirst());
			fragment.connect(override2.getOutValue(), scaledMaxJerk.getInSecond());

			// OTG is used for smooth velocity profile
			// Note that velocity maps to position, acceleration maps to
			// velocity and jerk maps to acceleration.
			OTG2 otg = fragment.add(new OTG2());

			otg.getInDesPos().connectTo(scaledVelocity.getOutValue());
			otg.getInMaxVel().connectTo(scaledMaxAcc.getOutValue());
			otg.getInMaxAcc().connectTo(scaledMaxJerk.getOutValue());

			// scaled velocity input is multiplied by cycle time of net
			DoubleMultiply newpos = fragment.add(new DoubleMultiply());

			newpos.getInFirst().connectTo(tick.getOutValue());
			newpos.getInSecond().connectTo(otg.getOutPos());

			// add jogging velocity/position offset to current joint position
			DoubleAdd add = fragment.add(new DoubleAdd());
			currentJointPosIns[i] = add.getInFirst();
			add.getInSecond().connectTo(newpos.getOutValue());

			// target joint position is result of above addition
			desiredJointPosOuts[i] = add.getOutValue();
		}

		DataflowInPort currentPosIn = fragment.addInPort(new JointsDataflow(action.getJointCount(), null), true,
				currentJointPosIns);
		fragment.connect(null, currentPosIn);

		// action result is a joint dataflow with the target values calculated
		// above
		DataflowOutPort resultPort = fragment.addOutPort(new JointsDataflow(action.getJointCount(), null), true,
				desiredJointPosOuts);

		return new PlannedActionMapperResult(action, fragment, new JointPositionActionResult(resultPort),
				ports.cancelPort, null);
	}
}
