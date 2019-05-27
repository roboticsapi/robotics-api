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
import org.roboticsapi.cartesianmotion.action.FollowCartesianGoal;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public class FollowCartesianGoalActivityImpl extends SingleDeviceRtActivity<CartesianMotionDevice> {

	private final DeviceParameterBag parameters;
	private Frame goal;
	private final Frame mc;

	public FollowCartesianGoalActivityImpl(CartesianMotionDevice device, Frame goal, DeviceParameterBag parameters)
			throws RoboticsException {
		super(device);
		this.goal = goal;
		this.parameters = parameters;

		MotionCenterParameter motionCenterParameter = parameters.get(MotionCenterParameter.class);
		if (motionCenterParameter == null) {
			throw new RoboticsException("MotionCenterParameter not given");
		}

		mc = motionCenterParameter.getMotionCenter();

		checkGoal();
	}

	protected void checkGoal() throws RoboticsException {
		if (goal == null) {
			return;
		}

		if (goal.getRelationsTo(mc) == null) {
			throw new RoboticsException("Goal Frame (" + goal.getName() + ") and motion center frame (" + mc.getName()
					+ ") must be connected somehow");
		}
	}

	protected void setGoal(Frame goal) throws RoboticsException {
		this.goal = goal;

		checkGoal();
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		RuntimeCommand goalcommand = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(),
				new FollowCartesianGoal(goal), getParameters());

		addProperty(getDevice(), new FrameGoalProperty(goal, mc));
		goalcommand.addTakeoverAllowedCondition(getDevice().getCompletedState());

		ignoreException(ActionCancelledException.class);

		setCommand(goalcommand, prevActivity);
		return true;
	}

	protected DeviceParameterBag getParameters() {
		return parameters;
	}

}
