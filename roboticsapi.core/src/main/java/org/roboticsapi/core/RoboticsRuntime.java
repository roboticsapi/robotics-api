/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.CommandSchedule.CommandScheduleStatusListener;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * Interface for a robotics runtime environment.
 */
public abstract class RoboticsRuntime extends AbstractOnlineObject {

	/**
	 * Retrieves the global override
	 *
	 * @return global override within [0,1]
	 */
	public abstract double getOverride();

	/**
	 * Sets the global override
	 *
	 * @param newOverride new global override within [0,1]
	 */
	@ConfigurationProperty(Optional = true)
	public abstract void setOverride(double newOverride);

	public abstract void addExtension(String extensionId);

	public abstract Set<String> getRegisteredExtensions() throws RoboticsException;

	public abstract Set<String> getAvailableExtensions() throws RoboticsException;

	public abstract boolean isAvailable(String extensionId) throws RoboticsException;

	/**
	 * Creates a Command that waits until it is cancelled.
	 *
	 * @return the Command
	 */
	public WaitCommand createWaitCommand() throws RoboticsException {
		return createWaitCommand("Wait");
	}

	/**
	 * Creates a Command with the given name that waits until it is cancelled.
	 *
	 * @param name the Command's name
	 * @return the Command
	 */
	public abstract WaitCommand createWaitCommand(String name) throws RoboticsException;

	/**
	 * Creates a Command that waits for the given time or until it is cancelled.
	 *
	 * @param duration the duration in [s]
	 * @return the Command
	 */
	public WaitCommand createWaitCommand(double duration) throws RoboticsException {
		return createWaitCommand("Wait " + duration + "s", duration);
	}

	/**
	 * Creates a Command with the given name that waits for the given time or until
	 * it is cancelled.
	 *
	 * @param name     the Command's name
	 * @param duration the duration in [s]
	 * @return the Command
	 */
	public abstract WaitCommand createWaitCommand(String name, double duration) throws RoboticsException;

	/**
	 * Creates a Command with the given name that waits for the given time or until
	 * it is cancelled.
	 *
	 * @param name    the Command's name
	 * @param waitFor a State to wait for
	 * @return the Command
	 */
	public abstract WaitCommand createWaitCommand(String name, RealtimeBoolean waitFor) throws RoboticsException;

	/**
	 * Creates a Command that tells the given Actuator to execute the given Action.
	 *
	 * The final DeviceParameters for Command parameterization are determined in the
	 * following way: 1. The Actuator's default parameters are retrieved 2. The
	 * given additional parameters are merged in, possibly replacing existing
	 * parameters
	 *
	 * @param driver               the Actuator
	 * @param action               the Action
	 * @param additionalParameters additional parameters configuring the Command
	 * @return the created RuntimeCommand
	 * @throws RoboticsException thrown if Command creation failed
	 */
	public RuntimeCommand createRuntimeCommand(ActuatorDriver driver, Action action,
			DeviceParameters... additionalParameters) throws RoboticsException {

		return createRuntimeCommand(driver, action,
				driver.getDevice().getDefaultParameters().withParameters(additionalParameters));
	}

	/**
	 * Creates a Command that tells the given Actuator to execute the given Action.
	 *
	 * The given DeviceParameters are used for parameterization of the Command
	 * as-is.
	 *
	 * @param driver          the Actuator
	 * @param action          the Action
	 * @param finalParameters the final parameters configuring the Command
	 * @return the created RuntimeCommand
	 * @throws RoboticsException thrown if Command creation failed
	 */
	public RuntimeCommand createRuntimeCommand(ActuatorDriver driver, Action action, DeviceParameterBag finalParameters)
			throws RoboticsException {
		if (!driver.isInitialized()) {
			throw new RoboticsException("The device '" + driver.getName() + "'(" + driver.getClass().getSimpleName()
					+ ") is not initialized.");
		}

		driver.getDevice().checkParameterBounds(finalParameters);

		return createRuntimeCommandInternal(driver, action, finalParameters);
	}

	protected abstract RuntimeCommand createRuntimeCommandInternal(ActuatorDriver driver, Action action,
			DeviceParameterBag parameters) throws RoboticsException;

	public abstract CommandSchedule scheduleOperations(List<CommandResult> condition,
			List<CommandHandleOperation> operations) throws CommandException;

	public abstract void scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations,
			CommandScheduleStatusListener listener) throws CommandException;

	public CommandSchedule executeOperations(List<CommandHandleOperation> operations) throws CommandException {
		return scheduleOperations(new ArrayList<CommandResult>(), operations);
	}

	public void executeOperations(List<CommandHandleOperation> operations, CommandScheduleStatusListener listener)
			throws CommandException {
		scheduleOperations(new ArrayList<CommandResult>(), operations, listener);
	}

	/**
	 * Adds a CommandHook to this RoboticsRuntime that is notified about Command
	 * related events.
	 *
	 * @param hook the hook to add
	 */
	public abstract void addCommandHook(CommandHook hook);

	/**
	 * Removes a CommandHook from this RoboticsRuntime.
	 *
	 * @param hook the hook to remove
	 */
	public abstract void removeCommandHook(CommandHook hook);

	/**
	 * Gets all CommandHooks that are registered with this RoboticsRuntime.
	 *
	 * @return the CommandHooks
	 */
	public abstract List<CommandHook> getCommandHooks();

	/**
	 * Interface for a hook executed during loading a command
	 */
	public interface CommandHook {
		/**
		 * Called before a command is sealed.
		 *
		 * @param command (sub-)command that will be sealed
		 */
		void commandSealHook(Command command);

		/**
		 * Called before a command is loaded.
		 *
		 * @param command command that will be loaded
		 */
		void commandLoadHook(Command command);

		/**
		 * Called after the command has been loaded.
		 *
		 * @param handle command handle of the loaded command
		 */
		void commandHandleHook(CommandHandle handle);
	}

	public class CommandHookAdapter implements CommandHook {

		@Override
		public void commandSealHook(Command command) {
		}

		@Override
		public void commandLoadHook(Command command) {
		}

		@Override
		public void commandHandleHook(CommandHandle handle) {
		}

	}

	/**
	 * Adds a CommandFilter to this RoboticsRuntime that is executed prior to
	 * execution of a Command.
	 *
	 * @param filter the CommandFilter to add
	 */
	public abstract void addCommandFilter(CommandFilter filter);

	/**
	 * Removes a previously added CommandFilter.
	 *
	 * @param filter the CommandFilter to remove
	 */
	public abstract void removeCommandFilter(CommandFilter filter);

	/**
	 * Adds a given List of SensorListeners for Sensors. All supplied Sensors are
	 * expected to be affiliated to this RoboticsRuntime.
	 *
	 * @param listeners list of SensorListenerRegistration, each specifying a Sensor
	 *                  and a SensorListener to be added
	 * @throws RoboticsException thrown if SensorListener registration failed
	 */
	public abstract void addRelatimeValueListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException;

	/**
	 * Retrieves the current value of a realtime value of this RoboticsRuntime.
	 *
	 * @param value realtime value to get the value for
	 * @throws RoboticsException thrown if the sensor value cannot be determined
	 */
	public abstract <T> T getRealtimeValue(RealtimeValue<T> value) throws RealtimeValueReadException;

	/**
	 * Removes a list of (previously added) SensorListeners for Sensors.
	 *
	 * Note that only the Sensor and SensorListener objects need to be the same as
	 * added previously. The SensorListenerRegistration objects are only treated as
	 * containers, not respecting their identity.
	 *
	 * @param listeners list of SensorListenerRegistration, each specifying a Sensor
	 *                  and a SensorListener to be removed
	 * @throws RoboticsException thrown if SensorListener removal failed
	 */
	public abstract void removeRealtimeValueListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException;

	/**
	 * Checks if the current thread is an event handler thread and may not be
	 * blocked
	 *
	 * @throws RoboticsException if the current thread is a reader thread
	 */
	public abstract void checkBlockEventHandlerThread() throws RoboticsException;

}
