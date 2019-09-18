/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * Abstract class for motions
 */
public abstract class PlannedAction<T extends Plan> extends Action implements ProcessAction {

	public PlannedAction(double watchdogTimeout, boolean supportsCompletion, boolean supportsCancel) {
		super(watchdogTimeout, supportsCompletion, supportsCancel);
	}

	/**
	 * Calculates a plan for this action
	 * 
	 * @param plans      map to place the generated plan into
	 * @param parameters device parameters to use for the plan
	 */
	public abstract void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException;

	/**
	 * Action event that a motion has arrived at a certain percentage of its
	 * execution time
	 * 
	 * @param progress amount of the motion to be completed (in time space)
	 * @return action event
	 */
	public ActionRealtimeBoolean getMotionTimeProgress(Command command, final float progress) {
		return new TimeProgressRealtimeBoolean(command, this, progress);
	}

	public static class TimeProgressRealtimeBoolean extends ActionRealtimeBoolean {
		private final double progress;

		public TimeProgressRealtimeBoolean(Command scope, Action action, final double progress) {
			super(scope, action);
			this.progress = progress;
		}

		public double getProgress() {
			return progress;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && progress == ((TimeProgressRealtimeBoolean) obj).progress;
		}

		@Override
		public int hashCode() {
			return HashCodeUtil.hash(super.hashCode(), progress);
		}
	}

}
