/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.cartesianmotion.action.HybridMoveAction;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.State;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public class HybridMove extends SingleDeviceRtActivity<CartesianMotionDevice> {

	private final Map<Dimension, ControlMode> modes = new HashMap<Dimension, ControlMode>();
	private final DeviceParameterBag parameters;
	private final Frame motionCenter;
	private final List<State> stopConditions = new ArrayList<State>();

	public HybridMove(CartesianMotionDevice device, DeviceParameterBag parameters) {
		super("HybridMove", device);
		this.parameters = parameters;

		MotionCenterParameter mcp = parameters.get(MotionCenterParameter.class);

		if (mcp == null) {
			throw new IllegalArgumentException("No MotionCenterParameter was supplied!");
		}

		motionCenter = mcp.getMotionCenter();

		initDefaultModes();
	}

	private void initDefaultModes() {
		addControlMode(Dimension.X, StandardModes.position(0));
		addControlMode(Dimension.Y, StandardModes.position(0));
		addControlMode(Dimension.Z, StandardModes.position(0));
		addControlMode(Dimension.A, StandardModes.position(0));
		addControlMode(Dimension.B, StandardModes.position(0));
		addControlMode(Dimension.C, StandardModes.position(0));

	}

	public void addControlMode(Dimension d, ControlMode mode) {
		modes.put(d, mode);
	}

	public void addStopConditions(State stopState, State... furtherStopStates) {
		stopConditions.add(stopState);
		for (State s : furtherStopStates) {
			stopConditions.add(s);
		}
	}

	public void stop() throws CommandException {
		CommandHandle commandHandle = getCommand().getCommandHandle();
		if (commandHandle != null) {
			commandHandle.abort();
		}
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		prevActivity.endExecute();

		Frame taskFrame = motionCenter.snapshot(getDevice().getReferenceFrame());

		Action[] controlActions = new Action[6];

		for (Dimension d : modes.keySet()) {
			Action action = modes.get(d).createAction(getDevice().getReferenceFrame(), motionCenter, taskFrame, d);

			controlActions[d.ordinal()] = action;
		}

		HybridMoveAction hm = new HybridMoveAction(controlActions[0], controlActions[1], controlActions[2],
				controlActions[3], controlActions[4], controlActions[5], taskFrame);

		RuntimeCommand cmd = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), hm, parameters);

		for (State s : stopConditions) {
			cmd.addStateEnteredHandler(s, new CommandStopper());
		}

		setCommand(cmd, prevActivity);

		return false;
	}

}
