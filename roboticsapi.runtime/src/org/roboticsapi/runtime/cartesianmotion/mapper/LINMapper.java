/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.action.LIN;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.LINFragment;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

public class LINMapper implements ActionMapper<SoftRobotRuntime, LIN> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, LIN action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		final double[] from = new double[3], to = new double[3];

		final Frame base = action.getFrom();
		Transformation f = null, t = null;
		try {
			f = base.getTransformationTo(action.getFrom());
			t = base.getTransformationTo(action.getTo(), false);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}

		if (t == null) {
			throw new MappingException("Unreachable destination point (not statically connected to start point).");
		}

		from[0] = f.getTranslation().getX();
		from[1] = f.getTranslation().getY();
		from[2] = f.getTranslation().getZ();
		to[0] = t.getTranslation().getX();
		to[1] = t.getTranslation().getY();
		to[2] = t.getTranslation().getZ();

		final double angle = t.getRotation().getAngle();

		// check validity of parameters and positions
		final CartesianParameters p = parameters.get(CartesianParameters.class);

		if (p == null) {
			throw new MappingException("No cartesian parameters given");
		}

		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new MappingException("No motion center parameters given");
		}

		// calculate acceleration and constant time
		double maxAccelTime = 0, maxConstantTime = 0;
		for (int i = 0; i < 3; i++) {
			double constantTime = Math.abs((to[i] - from[i])) / p.getMaximumPositionVelocity();
			double accelTime = p.getMaximumPositionVelocity() / p.getMaximumPositionAcceleration();
			if (constantTime >= accelTime) {
				constantTime -= accelTime;
			} else {
				constantTime = 0;
				accelTime = Math.sqrt(Math.abs(to[i] - from[i]) / p.getMaximumPositionAcceleration());
			}
			if (constantTime > maxConstantTime) {
				maxConstantTime = constantTime;
			}
			if (accelTime > maxAccelTime) {
				maxAccelTime = accelTime;
			}
		}

		double constantTime = Math.abs(angle) / p.getMaximumRotationVelocity();
		double accelTime = p.getMaximumRotationVelocity() / p.getMaximumRotationAcceleration();
		if (constantTime >= accelTime) {
			constantTime -= accelTime;
		} else {
			constantTime = 0;
			accelTime = Math.sqrt(Math.abs(angle) / p.getMaximumRotationAcceleration());
		}
		if (constantTime > maxConstantTime) {
			maxConstantTime = constantTime;
		}
		if (accelTime > maxAccelTime) {
			maxAccelTime = accelTime;
		}

		LINFragment ret = new LINFragment(ports, p, base, f, t, mp.getMotionCenter(),
				parameters.get(OverrideParameter.class), maxAccelTime / action.getSpeedFactor(),
				maxConstantTime / action.getSpeedFactor());

		ActionResult result = new CartesianPositionActionResult(ret.getResultPort());

		PlannedActionMapperResult mapperResult = new PlannedActionMapperResult(action, ret, result,
				ret.getCompletedPort(), ret.getProgressPort());

		mapperResult.addActionExceptionPort(ActionCancelledException.class, ret.getCancelCompleted());
		return mapperResult;
	}

}
