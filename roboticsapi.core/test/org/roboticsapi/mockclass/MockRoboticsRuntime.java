/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListenerRegistration;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.RoboticsException;

public class MockRoboticsRuntime extends RoboticsRuntime {
	private double override = 0;

	private final List<CommandHandle> commandHandles = new ArrayList<CommandHandle>();
	private final Set<String> extensions = new HashSet<String>();

	public double getDefaultOverride() {
		return defaultOverride;
	}

	@Override
	public double getOverride() {
		return override;
	}

	@Override
	public void setOverride(double newOverride) {
		override = newOverride;
	}

	@Override
	public void addExtension(String extensionId) {
		synchronized (extensions) {
			extensions.add(extensionId);
		}
	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		synchronized (extensions) {
			return Collections.unmodifiableSet(extensions);
		}
	}

	@Override
	public CommandHandle load(Command command) throws RoboticsException {
		CommandHandle tempHandle = new MockCommandHandleRAPILoggerImpl(command);

		commandHandles.add(tempHandle);

		return tempHandle;
	}

	@Override
	public Command createWaitCommand(String name) {
		return new MockWaitCommand(name, this);
	}

	@Override
	public Command createWaitCommand(String name, double duration) {
		return new MockWaitCommand(name, this, duration);
	}

	@Override
	protected RuntimeCommand createRuntimeCommandInternal(Actuator actuator, Action action,
			DeviceParameterBag parameters) throws RoboticsException {
		return null;
	}

	@Override
	public TransactionCommand createTransactionCommand(String name) {
		return null;
	}

	@Override
	public List<CommandHandle> getCommandHandles() {
		return null;
	}

	@Override
	public void addCommandHook(CommandHook hook) {
	}

	@Override
	public void removeCommandHook(CommandHook hook) {
	}

	@Override
	public List<CommandHook> getCommandHooks() {
		return null;
	}

	@Override
	public void addCommandFilter(CommandFilter filter) {
	}

	@Override
	public void removeCommandFilter(CommandFilter filter) {
	}

	@Override
	public void addSensorListeners(List<SensorListenerRegistration<?>> listeners) throws RoboticsException {
	}

	@Override
	public <T> T getSensorValue(Sensor<T> sensor) throws RoboticsException {
		return null;
	}

	@Override
	public void removeSensorListeners(List<SensorListenerRegistration<?>> listeners) throws RoboticsException {
	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
	}
}
