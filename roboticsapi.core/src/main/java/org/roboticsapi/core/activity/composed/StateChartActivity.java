/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.composed;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.exception.RoboticsException;

public class StateChartActivity extends AbstractActivity {

	public static class Transition {
		public final Activity from;
		public final Activity to;
		public final Predicate<ActivityResult> condition;
		public final boolean guaranteed;

		public Transition(Activity from, Predicate<ActivityResult> condition, Activity to, boolean guaranteed) {
			this.from = from;
			this.condition = condition;
			this.to = to;
			this.guaranteed = guaranteed;
		}
	}

	private List<Transition> transitions = new ArrayList<>();
	private List<Activity> states = new ArrayList<>();
	private Activity initialState;

	public StateChartActivity(String name, Activity initial) {
		super(name, initial);
		initialState = initial;
		states.add(initial);
	}

	public void addTransition(Transition transition) {
		transitions.add(transition);
		if (transition.to != null && !states.contains(transition.to))
			states.add(transition.to);
	}

	public void addTransition(Activity from, Predicate<ActivityResult> condition, Activity to) {
		addTransition(new Transition(from, condition, to, false));
	}

	public void addTakeoverTransition(Activity from, Activity to) {
		addTransition(from, r -> !r.isCompletedWhenActive(), to);
	}

	public void addCompletionTransition(Activity from, Activity to) {
		addTransition(from, r -> r.isCompletedWhenActive(), to);
	}

	public void addErrorTransition(Activity from, Activity to) {
		addTransition(from, r -> r.isFailedWhenActive() != null, to);
	}

	public <T extends RoboticsException> void addErrorTransition(Activity from, Class<T> error, Activity to) {
		addTransition(from,
				r -> r.isFailedWhenActive() != null && error.isAssignableFrom(r.isFailedWhenActive().getClass()), to);
	}

	public void addGuaranteedTransition(Activity from, Predicate<ActivityResult> condition, Activity to) {
		addTransition(new Transition(from, condition, to, true));
	}

	public void addGuaranteedTakeoverTransition(Activity from, Activity to) {
		addGuaranteedTransition(from, r -> !r.isCompletedWhenActive(), to);
	}

	public void addGuaranteedCompletionTransition(Activity from, Activity to) {
		addGuaranteedTransition(from, r -> r.isCompletedWhenActive(), to);
	}

	public void addGuaranteedErrorTransition(Activity from, Activity to) {
		addGuaranteedTransition(from, r -> r.isFailedWhenActive() != null, to);
	}

	public <T extends RoboticsException> void addGuaranteedErrorTransition(Activity from, Class<T> error, Activity to) {
		addGuaranteedTransition(from,
				r -> r.isFailedWhenActive() != null && error.isAssignableFrom(r.isFailedWhenActive().getClass()), to);
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public List<Transition> getTransitions(Activity from) {
		return transitions.stream().filter(t -> t.from == from).collect(Collectors.toList());
	}

	public List<Activity> getStates() {
		return states;
	}

	public Activity getInitialState() {
		return initialState;
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		return new StateChartActivityHandle(this);
	}

}
