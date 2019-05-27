/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.multijoint.action.PTPFromMotion;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.fragments.PTPFromMotionFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class PTPFromMotionMapper implements ActionMapper<SoftRobotRuntime, PTPFromMotion> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, PTPFromMotion action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		final double[] from = action.getFrom(), to = action.getTo(), realStart = action.getRealStart(),
				realStartVel = action.getRealStartVel();

		// check validity of parameters and positions
		final JointDeviceParameters p = parameters.get(JointDeviceParameters.class);
		if (p == null) {
			throw new MappingException("No joint device parameters given");
		}

		int jointCount = p.getJointCount();
		if (from == null || from.length != jointCount) {
			throw new MappingException("Invalid start position (numAxes)");
		}
		if (to == null || to.length != jointCount) {
			throw new MappingException("Invalid destination position (numAxes)");
		}
		if (realStart == null || realStart.length != jointCount) {
			throw new MappingException("Invalid real start position (numAxes)");
		}
		if (realStartVel == null || realStartVel.length != jointCount) {
			throw new MappingException("Invalid real start velocity (numAxes)");
		}

		// calculate acceleration and constant time
		double maxAccelTime = 0, maxConstantTime = 0;
		for (int i = 0; i < jointCount; i++) {
			final JointParameters j = p.getJointParameters(i);
			if (from[i] > j.getMaximumPosition() || from[i] < j.getMinimumPosition()) {
				throw new MappingException("Invalid start position (J" + i + ")");
			}
			if (to[i] > j.getMaximumPosition() || to[i] < j.getMinimumPosition()) {
				throw new MappingException("Invalid end position (J" + i + ")");
			}
			double constantTime = Math.abs((to[i] - from[i])) / j.getMaximumVelocity();
			double accelTime = j.getMaximumVelocity() / j.getMaximumAcceleration();
			if (constantTime >= accelTime) {
				constantTime -= accelTime;
			} else {
				constantTime = 0;
				accelTime = Math.sqrt(Math.abs(to[i] - from[i]) / j.getMaximumAcceleration());
			}
			if (constantTime > maxConstantTime) {
				maxConstantTime = constantTime;
			}
			if (accelTime > maxAccelTime) {
				maxAccelTime = accelTime;
			}
		}

		// calculate time for bezier fragment
		double realAccelTime = maxAccelTime;
		for (int i = 0; i < jointCount; i++) {
			double consVel = (to[i] - from[i]) / (maxAccelTime + maxConstantTime);
			double accelAcc = consVel / maxAccelTime;
			double accelEnd = from[i] + accelAcc * maxAccelTime * maxAccelTime / 2;

			double realCons = Math.abs(accelEnd - realStart[i]) / p.getJointParameters(i).getMaximumVelocity() * 1.5;
			if (realAccelTime < realCons) {
				realAccelTime = realCons;
			}
		}

		PTPFromMotionFragment ret = new PTPFromMotionFragment(ports, p, parameters.get(OverrideParameter.class), from,
				to, realStart, realStartVel, maxAccelTime / action.getSpeedFactor(),
				maxConstantTime / action.getSpeedFactor(), realAccelTime / action.getSpeedFactor());

		// for (double d = maxAccelTime - realAccelTime; d < ret.getTotalTime();
		// d += 0.01) {
		// System.out.println(Arrays.toString(ret.getJointPositionsAt(d)));
		// }
		// System.out.println("");

		ActionResult result = new JointPositionActionResult(ret.getResultPort());

		PlannedActionMapperResult mapperResult = new PlannedActionMapperResult(action, ret, result,
				ret.getCompletedPort(), ret.getProgressPort());

		mapperResult.addActionExceptionPort(ActionCancelledException.class, ret.getCancelCompleted());

		return mapperResult;
	}
}
