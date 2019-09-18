/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.realtimevalue.realtimeboolean.LeafActionDynamicScopeRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public abstract class PlannedSingleDeviceActivity extends SingleDeviceActivity implements PlannedActivity {

	public PlannedSingleDeviceActivity(String name, ActuatorDriver driver) {
		super(name, driver);
	}

	@Override
	public RealtimeBoolean getMotionTimeProgress(double progress) {
		return new MotionTimeProgressState(progress);
	}

	protected static class MotionTimeProgressState extends LeafActionDynamicScopeRealtimeBoolean {
		public final double progress;

		public MotionTimeProgressState(double progress) {
			this.progress = progress;
		}

		@Override
		protected RealtimeBoolean getState(Action action, Command scope) {
			return ((PlannedAction<?>) action).getMotionTimeProgress(scope, (float) progress);
		}
	}
}
