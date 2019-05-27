/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.core.state.ScopedState;

public abstract class RuntimeCommand extends Command {

	/** Action to execute */
	private final Action action;

	/** Device parameters */
	private DeviceParameterBag parameters;

	/** Device to execute action on */
	private final Actuator actuator;

	/**
	 * Creates a command from action, device and specific device paramters
	 * 
	 * @param action     action to execute
	 * @param actuator   actuator to execute action on
	 * @param parameters specific device parameters for the execution of this action
	 * @throws RoboticsException
	 */
	protected RuntimeCommand(final Action action, final Actuator actuator, final DeviceParameterBag parameters)
			throws RoboticsException {
		super(determineName(actuator, action));
		addStateValidator(new StateValidator() {
			@Override
			public boolean validateState(State state) throws RoboticsException {
				// allow action states for this action
				if (state instanceof ActionState && (((ActionState) state).getAction() == null
						|| (getAction().supportsState((ActionState) state)))) {
					return true;
				}

				// allow actuator states for the actuator
				if (state instanceof ActuatorState && (((ActuatorState) state).getDevice() == null
						|| ((ActuatorState) state).getDevice() == getDevice())) {
					return true;
				}

				return false;
			}
		});

		setRuntime(actuator.getDriver().getRuntime());

		this.action = action;
		this.actuator = actuator;

		this.parameters = parameters;

		if (action instanceof PlannedAction<?>) {
			((PlannedAction<?>) action).calculatePlan(plans, parameters);
		}
	}

	/**
	 * Determines name for the Command based on the given action and actuator
	 * 
	 * @param actuator actuator to execute action on; throws
	 *                 {@link IllegalArgumentException} if null
	 * @param action   action to execute; throws {@link IllegalArgumentException} if
	 *                 null
	 * @return the name combined from actuator name and action name
	 * 
	 */
	private static String determineName(final Actuator actuator, final Action action) {
		if (actuator == null) {
			throw new IllegalArgumentException("Parameter actuator must not be null.");
		}

		if (action == null) {
			throw new IllegalArgumentException("Parameter action must not be null.");
		}

		return actuator.getName() + "." + action.toString();
	}

	/**
	 * Retrieves the action of the command
	 * 
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Retrieves the actuator of the command
	 * 
	 * @return the actuator
	 */
	public Actuator getDevice() {
		return actuator;
	}

	/**
	 * Retrieves the device parameters of the command
	 * 
	 * @return the device parameters
	 */
	public DeviceParameterBag getDeviceParameters() {
		return parameters;
	}

	@Override
	public boolean needsWatchdog() {
		return super.needsWatchdog() || action.getNeedsWatchdog();
	}

	/**
	 * Compares the command's watchdogTimeout with its action's watchdogTimeout. If
	 * one of the values is not 0, the minimum of both values is returned. Otherwise
	 * the maximum is returned.
	 * 
	 * @return the minimum of the watchdogTimeouts ore 0 for infinite timeout
	 */
	@Override
	public double getWatchdogTimeout() {
		double commandTimeout = super.getWatchdogTimeout();
		double actionTimeout = action.getWatchdogTimeout();

		double min = Math.min(commandTimeout, actionTimeout);

		if (min != 0) {
			return min;
		} else {
			return Math.max(commandTimeout, actionTimeout);
		}
	}

	@Override
	protected void overrideWatchdogTimeoutInternal(double watchdogTimeout) {
		action.setWatchdogTimeout(watchdogTimeout);
	}

	@Override
	protected void relaxWatchdogTimeoutInternal(double watchdogTimeout) {
		action.relaxWatchdogTimeout(watchdogTimeout);

	}

	@Override
	protected Set<CommandRealtimeException> collectInnerExceptions() {
		final Set<CommandRealtimeException> errors = new HashSet<CommandRealtimeException>();
		List<ActionRealtimeException> actionExceptions = action.defineActionExceptions();
		if (actionExceptions != null) {
			errors.addAll(actionExceptions);
		}
		List<ActuatorDriverRealtimeException> deviceExceptions = actuator.getDriver().defineActuatorDriverExceptions();
		if (deviceExceptions != null) {
			errors.addAll(deviceExceptions);
		}
		return errors;
	}

	@Override
	public String toString() {
		return "RuntimeCommand<" + action.toString() + "," + actuator.toString() + ">";
	}

	Map<PlannedAction<?>, Plan> plans = new HashMap<PlannedAction<?>, Plan>();

	@SuppressWarnings("unchecked")
	public <T extends Plan> T getPlan(PlannedAction<T> action) {
		return (T) plans.get(action);
	}

	@Override
	protected void handleCompletion() throws RoboticsException {
		addDoneStateCondition(
				new ScopedState(this, getAction().getCompletedState().and(getDevice().getCompletedState())));

	}

	public Map<PlannedAction<?>, Plan> getPlans() {
		return plans;
	}
}
