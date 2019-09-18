/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

public class PersistContext<T> {
	private final Command command;
	private PersistedRealtimeValueFactory<T> factory;

	public interface PersistedRealtimeValueFactory<T> {
		RealtimeValue<T> createRealtimeValue(Command command);
	}

	public PersistContext(Command command) {
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public void setFactory(PersistedRealtimeValueFactory<T> factory) {
		this.factory = factory;
	}

	public void unpersist() throws CommandException {
		if (command.getCommandHandle() == null) {
			throw new IllegalStateException("The persisted value is not available any more.");
		}
		command.getCommandHandle().addStatusListener(new CommandStatusListener() {
			@Override
			public void statusChanged(CommandStatus newStatus) {
				if (newStatus == CommandStatus.TERMINATED) {
					try {
						command.getCommandHandle().unload();
					} catch (CommandException e) {
					}
				}
			}
		});
		command.getCommandHandle().cancel();
	}

	public RealtimeValue<T> getValue() {
		if (factory == null) {
			throw new IllegalStateException("The persisted value is not available yet.");
		}
		return factory.createRealtimeValue(command);
	}
}
