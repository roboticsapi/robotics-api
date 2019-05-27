/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.cartesianmotion.action.HoldCartesianVelocity;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.VelocityFromComponentsSensor;

public class VelocityControlMode implements ControlMode {

	private final double vel;

	public VelocityControlMode(double vel) {
		this.vel = vel;
	}

	@Override
	public Action createAction(Frame baseFrame, Frame movingFrame, Frame taskFrame, Dimension d)
			throws RoboticsException {

		return new HoldCartesianVelocity(new VelocityFromComponentsSensor(
				DirectionSensor.fromConstant(new Direction(taskFrame.getOrientation(), createTransVector(d))),
				DirectionSensor.fromConstant(new Direction(taskFrame.getOrientation(), createRotVector(d))),
				movingFrame, baseFrame, movingFrame.getPoint()));
	}

	private Vector createRotVector(Dimension d) {
		return new Vector(d == Dimension.A ? vel : 0, d == Dimension.B ? vel : 0, d == Dimension.C ? vel : 0);
	}

	private Vector createTransVector(Dimension d) {
		return new Vector(d == Dimension.X ? vel : 0, d == Dimension.Y ? vel : 0, d == Dimension.Z ? vel : 0);
	}
}
