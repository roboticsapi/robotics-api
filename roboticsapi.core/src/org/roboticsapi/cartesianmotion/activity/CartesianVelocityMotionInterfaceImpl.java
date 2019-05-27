/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.FromCommandActivity;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.action.HoldCartesianVelocity;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.VelocityFromComponentsSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class CartesianVelocityMotionInterfaceImpl<T extends CartesianMotionDevice> extends ActuatorInterfaceImpl<T>
		implements CartesianVelocityMotionInterface {

	public CartesianVelocityMotionInterfaceImpl(T device) {
		super(device);
	}

	@Override
	public RtActivity moveVelocity(VelocitySensor velocity, DeviceParameters... parameters) throws RoboticsException {
		HoldCartesianVelocity action = new HoldCartesianVelocity(velocity);
		return new FromCommandActivity(getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), action,
				getDefaultParameters().withParameters(parameters)), getDevice());
	}

	@Override
	public final RtActivity moveVelocity(DirectionSensor direction, DeviceParameters... parameters)
			throws RoboticsException {
		Frame base = getDevice().getReferenceFrame();
		Frame moving = getDevice().getMovingFrame();
		VelocitySensor velocity = new VelocityFromComponentsSensor(direction,
				DirectionSensor.fromConstant(new Direction(direction.getOrientation(), new Vector())), moving, base,
				moving.getPoint());
		return moveVelocity(velocity, parameters);
	}

	@Override
	public final RtActivity moveVelocity(DirectionSensor direction, DirectionSensor rotation,
			DeviceParameters... parameters) throws RoboticsException {
		Frame base = getDevice().getReferenceFrame();
		Frame moving = getDevice().getMovingFrame();
		VelocitySensor velocity = new VelocityFromComponentsSensor(direction, rotation, moving, base,
				moving.getPoint());
		return moveVelocity(velocity, parameters);
	}

	@Override
	public final RtActivity moveVelocity(Direction direction, DeviceParameters... parameters) throws RoboticsException {
		return moveVelocity(DirectionSensor.fromConstant(direction), parameters);
	}

	@Override
	public final RtActivity moveVelocity(Direction direction, Direction rotation, DeviceParameters... parameters)
			throws RoboticsException {
		return moveVelocity(DirectionSensor.fromConstant(direction), DirectionSensor.fromConstant(rotation),
				parameters);
	}

}
