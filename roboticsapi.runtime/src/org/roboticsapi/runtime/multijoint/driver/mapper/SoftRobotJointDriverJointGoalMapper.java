/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleDivide;
import org.roboticsapi.runtime.core.primitives.DoubleEquals;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.OTG;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DerivedActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.JointVelDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotJointDriver;
import org.roboticsapi.runtime.multijoint.mapper.JointGoalActionResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotJointDriverJointGoalMapper extends AbstractSoftRobotJointDriverMapper<JointGoalActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, final SoftRobotJointDriver jointDriver,
			JointGoalActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		JointParameters jointParameters = parameters.get(JointParameters.class);

		final NetFragment deviceFragment = new NetFragment("SoftRobotJointGoal");
		DataflowOutPort override = ports.override;

		// Monitor for currently commanded position
		DataflowOutPort jointPosition = addJointMonitorFragment(deviceFragment, jointDriver, parameters);
		DataflowOutPort jointVelocity = addJointVelocityMonitorFragment(deviceFragment, jointDriver, parameters);

		double maxVel = jointParameters.getMaximumVelocity() * 0.98; // FIXME:
																		// why
																		// 0.98?
		double maxAcc = jointParameters.getMaximumAcceleration() * 0.98; // FIXME:
																			// why
																			// 0.98?

		// OTG to smooth commanded position to measured position
		OTG otg = deviceFragment.add(new OTG(maxVel, maxAcc));

		// Comparator for comparison of desired and current position
		DoubleEquals axisNearDest = deviceFragment.add(new DoubleEquals(0.001));

		deviceFragment.connect(jointPosition, otg.getInCurPos(), new JointDataflow(jointDriver));

		// velocity input for override scaling
		DoubleDivide div = deviceFragment.add(new DoubleDivide());

		deviceFragment.connect(jointVelocity, div.getInFirst(), new JointVelDataflow(jointDriver));

		deviceFragment.connect(override, deviceFragment.addInPort(new DoubleDataflow(), true, div.getInSecond()));
		otg.getInCurVel().connectTo(div.getOutValue());

		InPort destPosition1 = otg.getInDestPos();

		DataflowOutPort otgOut = deviceFragment.addOutPort(new JointVelDataflow(jointDriver), true, otg.getOutVel());
		// override scaling

		DoubleMultiply mult = deviceFragment.add(new DoubleMultiply());

		deviceFragment.connect(otgOut, deviceFragment.addInPort(new DoubleDataflow(), true, mult.getInFirst()));
		deviceFragment.connect(override, deviceFragment.addInPort(new DoubleDataflow(), true, mult.getInSecond()));

		DataflowOutPort otgOvOut = deviceFragment.addOutPort(new JointVelDataflow(jointDriver), true,
				mult.getOutValue());

		DataflowOutPort otgOvPosOut = deviceFragment.addConverterLink(otgOvOut, new JointDataflow(jointDriver));

		final ActuatorDriverMapperResult jointResult = runtime.getMapperRegistry().mapActuatorDriver(runtime,
				jointDriver, new JointPositionActionResult(otgOvPosOut), parameters, ports.cancel, override);

		deviceFragment.add(jointResult.getNetFragment());

		InPort destPosition2 = axisNearDest.getInFirst();
		deviceFragment.connect(jointPosition, axisNearDest.getInSecond(), new DoubleDataflow());

		deviceFragment.connect(actionResult.getOutPort(),
				deviceFragment.addInPort(new JointDataflow(jointDriver), true, destPosition1));
		deviceFragment.connect(actionResult.getOutPort(),
				deviceFragment.addInPort(new JointDataflow(jointDriver), true, destPosition2));

		final DataflowOutPort completed = deviceFragment.addOutPort(new StateDataflow(), false,
				axisNearDest.getOutValue());

		return new DerivedActuatorDriverMapperResult(jointResult, deviceFragment, completed);
	}

}
