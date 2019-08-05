/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import java.util.List;
import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.AbstractRuntime;

public class TestRuntime extends AbstractRuntime {

	private boolean present = false;

	public TestRuntime() {
		setName("TestRuntime");
	}

	public void setActuatorPresency(boolean present) {
		this.present = present;
		checkConnection();

	}

	@Override
	protected void afterInitialization() throws RoboticsException {
		super.afterInitialization();
		checkConnection();
	}

	@Override
	protected boolean checkPresent() {
		return present;
	}

	public void doGoOperational() {
		goOperational();
	}

	public void doGoSafeoperational() {
		goSafeoperational();
	}

	public void doGoOffline() {
		goOffline();
	}

	@Override
	public CommandHandle loadCommand(Command command) throws RoboticsException {
		return null;
	}

	@Override
	public double getOverride() {
		return 0;
	}

	@Override
	public void setOverride(double newOverride) {

	}

	@Override
	public List<CommandHandle> getCommandHandles() {
		return null;
	}

	@Override
	public Command createWaitCommand(String name) {
		return new WaitCommand(name, this) {
		};
	}

	@Override
	public Command createWaitCommand(String name, double duration) {
		return new WaitCommand(name, this, duration) {
		};
	}

	@Override
	public RuntimeCommand createRuntimeCommandInternal(Actuator actuator, Action action, DeviceParameterBag parameters)
			throws RoboticsException {
		return new RuntimeCommand(action, actuator, parameters) {
		};
	}

	@Override
	public TransactionCommand createTransactionCommand(String name) {
		return new TransactionCommand(name, this) {
		};
	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
	}

	@Override
	public void addExtension(String extensionId) {
	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		return null;
	}

}
