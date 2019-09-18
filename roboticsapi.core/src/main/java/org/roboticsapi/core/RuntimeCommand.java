/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
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
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

/**
 * A runtime command (telling a device to execute an action)
 */
public abstract class RuntimeCommand extends Command {

	private final Set<CommandRealtimeException> innerErrors = new HashSet<CommandRealtimeException>();

	/** Action to execute */
	private final Action action;

	/** Device parameters */
	private final DeviceParameterBag parameters;

	/** Device to execute action on */
	private final ActuatorDriver device;

	private CommandResult completionResult, cancelResult;

	/**
	 * Creates a command from action, device and specific device paramters
	 *
	 * @param action     action to execute
	 * @param device     device to execute action on
	 * @param parameters specific device parameters for the execution of this action
	 * @throws RoboticsException
	 */
	protected RuntimeCommand(final Action action, final ActuatorDriver device, final DeviceParameterBag parameters)
			throws RoboticsException {
		super(determineName(device, action), device.getRuntime());

		this.action = action;
		this.device = device;
		this.parameters = parameters;

		if (action instanceof PlannedAction<?>) {
			((PlannedAction<?>) action).calculatePlan(plans, parameters);
		}

		List<ActionRealtimeException> actionExceptions = action.defineActionExceptions(this);
		if (actionExceptions != null) {
			innerErrors.addAll(actionExceptions);
		}
		List<ActuatorDriverRealtimeException> deviceExceptions = device.defineActuatorDriverExceptions();
		if (deviceExceptions != null) {
			innerErrors.addAll(deviceExceptions);
		}

		RealtimeBoolean deviceAndActionCompleted = getAction().getCompletedState(this)
				.and(getDevice().getDevice().getCompletedState(this));
		if (getAction().getCancelSupported()) {
			cancelResult = addCompletionResult("Cancelled", getCancelState().and(deviceAndActionCompleted), true);
			if (getAction().getCompletionSupported()) {
				completionResult = addCompletionResult("Completed",
						getCancelState().not().and(deviceAndActionCompleted), false);
			}
		} else {
			completionResult = addCompletionResult("Completed", deviceAndActionCompleted, false);
		}
	}

	public CommandResult getCancelResult() {
		return cancelResult;
	}

	public CommandResult getCompletionResult() {
		return completionResult;
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
	private static String determineName(final ActuatorDriver actuator, final Action action) {
		if (actuator == null) {
			throw new IllegalArgumentException("Parameter actuator must not be null.");
		}

		if (action == null) {
			throw new IllegalArgumentException("Parameter action must not be null.");
		}

		return actuator.getDevice().getName() + "." + action.toString();
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
	 * Retrieves the device of the command
	 *
	 * @return the device
	 */
	public ActuatorDriver getDevice() {
		return device;
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
		return innerErrors;
	}

	@Override
	public CommandHandle load() throws RoboticsException {
		// return handle if already loaded
		if (getCommandHandle() != null) {
			return getCommandHandle();
		}

		if (!getRuntime().isPresent()) {
			throw new RoboticsException(
					"The runtime '" + getRuntime() + "' is not present (" + getRuntime().getState() + ")");
		}

		if (!device.isPresent()) {
			throw new RoboticsException("The device '" + device.getName() + "'(" + device.getClass().getSimpleName()
					+ ") is not present (" + device.getState() + ").");
		}

		return super.load();
	}

	@Override
	public String toString() {
		return "RuntimeCommand<" + action.toString() + "," + device.toString() + ">";
	}

	Map<PlannedAction<?>, Plan> plans = new HashMap<PlannedAction<?>, Plan>();

	@SuppressWarnings("unchecked")
	public <T extends Plan> T getPlan(PlannedAction<T> action) {
		return (T) plans.get(action);
	}

	public Map<PlannedAction<?>, Plan> getPlans() {
		return plans;
	}
}
