/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;
import java.util.Set;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * Interface for a robotics runtime environment.
 */
public abstract class RoboticsRuntime extends AbstractOnlineObject {

	protected double defaultOverride = 1;

	/**
	 * Instantiates a new RoboticsRuntime.
	 */
	public RoboticsRuntime() {
	}

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
	public abstract void setOverride(double newOverride);

	/**
	 * Sets the default global override
	 * 
	 * @param override new default global override within [0,1]
	 */
	@ConfigurationProperty(Optional = true)
	public void setDefaultOverride(double override) {
		immutableWhenInitialized();
		this.defaultOverride = override;
	}

	public abstract void addExtension(String extensionId);

	public abstract Set<String> getRegisteredExtensions() throws RoboticsException;

	/**
	 * Loads the given Command into the Robot Control Core.
	 * 
	 * @param command Command to load
	 * @return CommandHandle for controlling execution
	 * @throws RoboticsException if an error occurs
	 */
	public abstract CommandHandle load(Command command) throws RoboticsException;

	/**
	 * Creates a Command that waits until it is cancelled.
	 * 
	 * @return the Command
	 */
	public Command createWaitCommand() {
		return createWaitCommand("Wait");
	}

	/**
	 * Creates a Command with the given name that waits until it is cancelled.
	 * 
	 * @param name the Command's name
	 * @return the Command
	 */
	public abstract Command createWaitCommand(String name);

	/**
	 * Creates a Command that waits for the given time or until it is cancelled.
	 * 
	 * @param duration the duration in [s]
	 * @return the Command
	 */
	public Command createWaitCommand(double duration) {
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
	public abstract Command createWaitCommand(String name, double duration);

	/**
	 * Creates a Command that tells the given Actuator to execute the given Action.
	 * 
	 * The final DeviceParameters for Command parameterization are determined in the
	 * following way: 1. The Actuator's default parameters are retrieved 2. The
	 * given additional parameters are merged in, possibly replacing existing
	 * parameters
	 * 
	 * @param actuator             the Actuator
	 * @param action               the Action
	 * @param additionalParameters additional parameters configuring the Command
	 * @return the created RuntimeCommand
	 * @throws RoboticsException thrown if Command creation failed
	 */
	public RuntimeCommand createRuntimeCommand(Actuator actuator, Action action,
			DeviceParameters... additionalParameters) throws RoboticsException {

		return createRuntimeCommand(actuator, action,
				actuator.getDefaultParameters().withParameters(additionalParameters));
	}

	/**
	 * Creates a Command that tells the given Actuator to execute the given Action.
	 * 
	 * The given DeviceParameters are used for parameterization of the Command
	 * as-is.
	 * 
	 * @param actuator        the Actuator
	 * @param action          the Action
	 * @param finalParameters the final parameters configuring the Command
	 * @return the created RuntimeCommand
	 * @throws RoboticsException thrown if Command creation failed
	 */
	public RuntimeCommand createRuntimeCommand(Actuator actuator, Action action, DeviceParameterBag finalParameters)
			throws RoboticsException {
		if (!actuator.isInitialized()) {
			throw new RoboticsException("The device '" + actuator.getName() + "'(" + actuator.getClass().getSimpleName()
					+ ") is not initialized.");
		}
		if (!actuator.isPresent()) {
			throw new RoboticsException("The device '" + actuator.getName() + "'(" + actuator.getClass().getSimpleName()
					+ ") is not present (" + actuator.getState() + ").");
		}

		actuator.checkParameterBounds(finalParameters);

		return createRuntimeCommandInternal(actuator, action, finalParameters);
	}

	protected abstract RuntimeCommand createRuntimeCommandInternal(Actuator actuator, Action action,
			DeviceParameterBag parameters) throws RoboticsException;

	/**
	 * Creates an empty TransactionCommand that can contain other Commands.
	 * 
	 * @return the created TransactionCommand
	 */
	public TransactionCommand createTransactionCommand() {
		return createTransactionCommand("Transaction");
	}

	/**
	 * Creates an empty TransactionCommand with the given name that can contain
	 * other Commands.
	 * 
	 * @param name the TransactionCommand's name
	 * @return the created TransactionCommand
	 */
	public abstract TransactionCommand createTransactionCommand(String name);

	/**
	 * Creates a TransactionCommand with the given name that contains the given
	 * Commands.
	 * 
	 * @param name     the TransactionCommand's name
	 * @param commands the Commands contained in the TransactionCommand
	 * @return the created TransactionCommand
	 * @throws RoboticsException thrown if Command creation failed
	 */
	public TransactionCommand createTransactionCommand(String name, Command... commands) throws RoboticsException {
		TransactionCommand cmd = createTransactionCommand(name);

		for (Command c : commands) {
			cmd.addCommand(c);
		}

		return cmd;
	}

	/**
	 * Creates a TransactionCommand that contains the given Commands.
	 * 
	 * @param commands the Commands contained in the TransactionCommand
	 * @return the created TransactionCommand
	 * @throws RoboticsException thrown if Command creation failed
	 */
	public TransactionCommand createTransactionCommand(Command... commands) throws RoboticsException {
		TransactionCommand cmd = createTransactionCommand();

		for (Command c : commands) {
			cmd.addCommand(c);
		}

		return cmd;
	}

	/**
	 * Retrieves the list of handles to all current (ready or running) commands
	 * 
	 * @return list of command handles
	 */
	public abstract List<CommandHandle> getCommandHandles();

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
	public abstract void addSensorListeners(List<SensorListenerRegistration<?>> listeners) throws RoboticsException;

	/**
	 * Retrieves the current value of a Sensor of this RoboticsRuntime.
	 * 
	 * @param sensor sensor to get the value for
	 * @throws RoboticsException thrown if the sensor value cannot be determined
	 */
	public abstract <T> T getSensorValue(Sensor<T> sensor) throws RoboticsException;

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
	public abstract void removeSensorListeners(List<SensorListenerRegistration<?>> listeners) throws RoboticsException;

	/**
	 * Checks if the current thread is an event handler thread and may not be
	 * blocked
	 * 
	 * @throws RoboticsException if the current thread is a reader thread
	 */
	public abstract void checkBlockEventHandlerThread() throws RoboticsException;

}
