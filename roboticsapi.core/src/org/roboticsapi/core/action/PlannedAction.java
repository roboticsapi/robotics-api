/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * Abstract class for motions
 */
public abstract class PlannedAction<T extends Plan> extends Action implements ProcessAction {

	public PlannedAction(double watchdogTimeout) {
		super(watchdogTimeout);
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
	public ActionState getMotionTimeProgress(final float progress) {
		return atTimeProgress(progress).setAction(this);
	}

	/**
	 * Action event that a motion has arrived at a certain progress of its execution
	 * time
	 * 
	 * @param progress amount of the motion to be completed (in time space, 0...1)
	 * @return action event
	 */
	public static ActionState atTimeProgress(final float progress) {
		return new TimeProgressState(progress);
	}

	public static class TimeProgressState extends ActionState {
		private final double progress;

		public TimeProgressState(final double progress) {
			this.progress = progress;
		}

		public double getProgress() {
			return progress;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && progress == ((TimeProgressState) obj).progress;
		}

		@Override
		public int hashCode() {
			return HashCodeUtil.hash(super.hashCode(), progress);
		}
	}

}
