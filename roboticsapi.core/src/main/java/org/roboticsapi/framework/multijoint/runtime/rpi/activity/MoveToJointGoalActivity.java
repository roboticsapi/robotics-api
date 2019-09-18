/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import java.util.Arrays;
import java.util.Set;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.runtime.SingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.multijoint.action.MoveToJointGoal;
import org.roboticsapi.framework.multijoint.property.CommandedPositionProperty;
import org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty;
import org.roboticsapi.framework.multijoint.property.GoalProperty;

public class MoveToJointGoalActivity extends SingleDeviceActivity {
	// TODO: add maintaining
	private final RealtimeDouble[] goal;
	private final DeviceParameterBag parameters;

	public MoveToJointGoalActivity(ActuatorDriver driver, RealtimeDouble[] goal, DeviceParameterBag parameters) {
		super(driver.getDevice() + ".moveToJointGoal(" + Arrays.toString(goal) + ")", driver);
		this.goal = goal;
		this.parameters = parameters;
	}

	@Override
	protected Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException {
		double[] staticGoal = new double[goal.length];
		for (int i = 0; i < goal.length; i++) {
			if (!goal[i].isConstant()) {
				staticGoal = null;
				break;
			}
			staticGoal[i] = goal[i].getCurrentValue();
		}
		ActivityResult completionResult;
		ActivityResult cancelResult;
		if (staticGoal != null) {
			completionResult = completionResult(((RuntimeCommand) command).getCompletionResult(),
					new GoalProperty(staticGoal), new CommandedPositionProperty(staticGoal),
					new CommandedVelocityProperty(new double[goal.length]));
			cancelResult = cancelResult(((RuntimeCommand) command).getCancelResult(), new GoalProperty(staticGoal));
		} else {
			completionResult = completionResult(((RuntimeCommand) command).getCompletionResult());
			cancelResult = cancelResult(((RuntimeCommand) command).getCancelResult());
		}
		return activityResultSet(completionResult, cancelResult);
	}

	@Override
	protected Command getCommandForResult(ActivityResult result) throws RoboticsException {
		return createRuntimeCommand(new MoveToJointGoal(goal), parameters);
	}

}
