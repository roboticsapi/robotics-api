/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.cartesianmotion.action.MoveToCartesianGoal;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public class MoveToCartesianGoalActivityImpl extends SingleDeviceRtActivity<CartesianMotionDevice> {

	protected DeviceParameterBag parameters;
	protected MoveToCartesianGoal action;
	protected MotionCenterParameter mp;
	private final Frame goal;

	public MoveToCartesianGoalActivityImpl(CartesianMotionDevice device, Frame goal, DeviceParameterBag parameters)
			throws RoboticsException {
		super(device);
		this.goal = goal;
		this.parameters = parameters;

		getMotionCenter();

	}

	public MoveToCartesianGoalActivityImpl(String name, CartesianMotionDevice device, Frame goal,
			DeviceParameterBag parameters) throws RoboticsException {
		super(name, device);
		this.goal = goal;
		this.parameters = parameters;

		getMotionCenter();
	}

	private void getMotionCenter() throws RoboticsException {
		mp = parameters.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("MotionCenterParameter not given");
		}
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		if (getGoal().getRelationsTo(mp.getMotionCenter()) == null) {
			throw new RoboticsException("Goal frame (" + getGoal().getName() + ") and motion center frame ("
					+ mp.getMotionCenter().getName() + ") must be connected somehow");
		}

		action = new MoveToCartesianGoal(getGoal());
		RuntimeCommand goalcommand = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), action,
				parameters);

		// try {
		// getDevice().getReferenceFrame().getTransformationTo(getGoal(),
		// false);
		setCommand(goalcommand, prevActivity);
		// } catch (TransformationException e) {
		//
		// RuntimeCommand holdcommand = getDevice()
		// .getDriver()
		// .getRuntime()
		// .createRuntimeCommand(getDevice(),
		// new HoldCartesianPosition(getGoal()), parameters);
		//
		// TransactionCommand command = getDevice().getDriver().getRuntime()
		// .createTransactionCommand(goalcommand, holdcommand);
		//
		// command.addStartCommand(goalcommand);
		// command.addStateFirstEnteredHandler(goalcommand.getDoneState(),
		// new CommandStarter(holdcommand));
		// command.addStateFirstEnteredHandler(command.getCancelState(),
		// new CommandCanceller(goalcommand));
		// command.addStateFirstEnteredHandler(command.getCancelState(),
		// new CommandCanceller(holdcommand));
		// command.addTakeoverAllowedCondition(holdcommand.getStartedState());
		//
		// setCommand(command, prevActivity, holdcommand.getStartedState());
		// }

		addProperty(getDevice(), new FrameGoalProperty(goal, mp.getMotionCenter()));

		return true;
	}

	public Frame getGoal() {
		return goal;
	}

}