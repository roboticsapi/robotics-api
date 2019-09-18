/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.composed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResultContainer;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.activity.composed.StateChartActivity.Transition;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;

public class StateChartActivityHandle extends ActivityHandle {
	Executor threadPool = new ThreadPoolExecutor(0, 1, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	private StateChartActivity stateChart;

	public StateChartActivityHandle(StateChartActivity activity) throws RoboticsException {
		super(activity);
		stateChart = activity;
	}

	@Override
	public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack) throws RoboticsException {
		return prepareTransition(result, null, new Transition(null, null, stateChart.getInitialState(), true),
				errorStack);
	}

	private Map<ActivityHandle, Map<Activity, ActivityHandle>> handles = new HashMap<>();

	private ActivitySchedule prepareTransition(ActivityResult triggeringResult, ActivityHandle from,
			Transition transition, StackTraceElement[] errorStack) throws RoboticsException {
		if (handles.get(from) == null)
			handles.put(from, new HashMap<>());
		try {
			if (handles.get(from).get(transition.to) == null)
				handles.get(from).put(transition.to, transition.to.createHandle());
		} catch (RoboticsException e) {
			RAPILogger.logException(this, e);
			return null;
		}
		ActivityHandle handleForTargetState = handles.get(from).get(transition.to);
		ActivitySchedule scheduleForTargetState = handleForTargetState.prepare(triggeringResult, errorStack);
		ActivityResultContainer scheduleResults = new ActivityResultContainer();
		ActivitySchedule ret = scheduleForTargetState.withResults(this, scheduleResults);

		scheduleForTargetState.getResults().provide(scheduleResult -> {
			if (scheduleResult == null || scheduleResult.getStatus() == ActivityResult.Status.IMPOSSIBLE)
				return;
			boolean handled = false;
			for (Transition scheduleTransition : stateChart.getTransitions()) {
				if (scheduleTransition.from != transition.to || !scheduleTransition.condition.test(scheduleResult))
					continue;
				handled = true;
				if (scheduleTransition.to == null) {
					scheduleResults.addResult(scheduleResult);
				} else if (scheduleTransition.guaranteed) {
					ActivitySchedule nextSchedule = prepareTransition(scheduleResult, handleForTargetState,
							scheduleTransition, errorStack);
					ret.addDependency(nextSchedule);
					nextSchedule.getResults().provide(nextResult -> {
						if (nextResult != null)
							scheduleResults.addResult(nextResult);
					});
				} else {
					handleForTargetState.addStatusListener(targetStateStatus -> {
						if (targetStateStatus == ActivityHandle.Status.RUNNING
								&& scheduleForTargetState.getStatus() == ActivitySchedule.Status.TAKEN) {
							threadPool.execute(() -> {
								try {
									ActivitySchedule nextSchedule = prepareTransition(scheduleResult,
											handleForTargetState, scheduleTransition, errorStack);
									ret.addDependency(nextSchedule);
									nextSchedule.getResults().provide(nextResult -> {
										if (nextResult != null)
											scheduleResults.addResult(nextResult);
									});
								} catch (RoboticsException e) {
									this.fail(e);
								}
							});
						}
					});
				}
			}
			if (!handled)
				scheduleResults.addResult(scheduleResult);
		}, this::fail);
		if (getException() != null)
			throw getException();
		return ret;
	}

}
