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
import org.roboticsapi.multijoint.action.PTP;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.fragments.PTPFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class PTPMapper implements ActionMapper<SoftRobotRuntime, PTP> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, PTP action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		final double[] from = action.getFrom(), to = action.getTo();

		// check validity of parameters and positions
		final JointDeviceParameters p = parameters.get(JointDeviceParameters.class);
		if (p == null) {
			throw new MappingException("No joint device parameters given");
		}

		if (from == null || from.length != p.getJointCount()) {
			throw new MappingException("Invalid start position (numAxes)");
		}
		if (to == null || to.length != p.getJointCount()) {
			throw new MappingException("Invalid destination position (numAxes)");
		}

		// calculate acceleration and constant time
		double maxAccelTime = 0, maxConstantTime = 0;
		for (int i = 0; i < p.getJointCount(); i++) {
			final JointParameters j = p.getJointParameters(i);
			if (Double.isNaN(to[i])) {
				throw new MappingException("Invalid end position NaN (J" + i + ")");
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

		PTPFragment ret = new PTPFragment(ports, p, parameters.get(OverrideParameter.class), from, to,
				maxAccelTime / action.getSpeedFactor(), maxConstantTime / action.getSpeedFactor());

		ActionResult result = new JointPositionActionResult(ret.getResultPort());

		PlannedActionMapperResult mapperResult = new PlannedActionMapperResult(action, ret, result,
				ret.getCompletedPort(), ret.getProgressPort());

		mapperResult.addActionExceptionPort(ActionCancelledException.class, ret.getCancelCompleted());

		return mapperResult;
	}
}
