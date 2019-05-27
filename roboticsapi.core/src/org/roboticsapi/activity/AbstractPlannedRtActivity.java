/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.State;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.state.AliasState;
import org.roboticsapi.core.state.OrState;

public abstract class AbstractPlannedRtActivity<D extends ActuatorDriver, A extends Actuator>
		extends SingleDeviceRtActivity<A> implements PlannedRtActivity {

	private final List<PlannedRtActivityProgressState<?, ?>> states = new ArrayList<PlannedRtActivityProgressState<?, ?>>();

	public AbstractPlannedRtActivity(String name, A device) {
		super(name, device);
	}

	@Override
	public PlannedRtActivityProgressState<Plan, PlannedAction<Plan>> getMotionTimeProgress(double progress) {
		MotionTimeProgressState state = new MotionTimeProgressState(progress);
		addProgressState(state);
		return state;
	}

	protected void addProgressState(PlannedRtActivityProgressState<?, ?> state) {
		states.add(state);
	}

	@SuppressWarnings("unchecked")
	protected <P extends Plan> void bindProgressStates(PlannedAction<P> action) {
		for (PlannedRtActivityProgressState<?, ?> s : states) {
			((PlannedRtActivityProgressState<P, PlannedAction<P>>) s).addActionState(action);
		}
	}

	protected void finishProgressStates() {
		for (PlannedRtActivityProgressState<?, ?> s : states) {
			s.finish();
		}
	}

	protected abstract class PlannedRtActivityProgressState<P extends Plan, T extends PlannedAction<P>>
			extends AliasState {
		private final List<State> actionStates = new ArrayList<State>();

		public void addActionState(T action) {
			State state = determineActionState(action);

			if (state != null) {
				actionStates.add(state);
			}
		}

		public void finish() {
			if (actionStates.size() == 0) {
				setOther(State.False());
			} else if (actionStates.size() == 1) {
				setOther(actionStates.get(0));
			} else {
				OrState or = new OrState();
				for (State s : actionStates) {
					or.addState(s);
				}
				setOther(or);
			}
		}

		protected abstract State determineActionState(T action);
	}

	protected class MotionTimeProgressState extends PlannedRtActivityProgressState<Plan, PlannedAction<Plan>> {
		public final double progress;

		public MotionTimeProgressState(double progress) {
			this.progress = progress;

		}

		@Override
		protected ActionState determineActionState(PlannedAction<Plan> action) {
			return action.getMotionTimeProgress((float) progress);
		}
	}

}
