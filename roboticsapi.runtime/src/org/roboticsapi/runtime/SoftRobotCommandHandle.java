/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.runtime.controlcore.SoftRobotNetHandle;
import org.roboticsapi.runtime.core.NetBasedCommandHandle;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.rpi.NetStatus;
import org.roboticsapi.runtime.rpi.NetStatusListener;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * Representation of a RPI net running on an SoftRobot RCC
 */
public class SoftRobotCommandHandle implements NetBasedCommandHandle {

	/** URI of the net */
	private CommandException occuredException = null;
	private final NetcommValue override;

	private final NetHandle netHandle;

	@Override
	public void throwException(final CommandException ex) {
		occuredException = ex;
	}

	/**
	 * Creates a new SoftRobot net
	 *
	 * @param uri URI of the net
	 */
	public SoftRobotCommandHandle(final NetHandle handle, final NetcommValue override) {
		this.netHandle = handle;
		this.override = override;
	}

	@Override
	public NetHandle getNetHandle() {
		return netHandle;
	}

	@Override
	public boolean abort() throws CommandException {
		try {
			return netHandle.abort();
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	private CommandStatus netToCommand(final NetStatus status) {
		switch (status) {
		case CANCELLING:
			return CommandStatus.RUNNING;
		case ERROR:
			return CommandStatus.ERROR;
		case LOADING:
			return CommandStatus.READY;
		case READY:
			return CommandStatus.READY;
		case REJECTED:
			return CommandStatus.ERROR;
		case RUNNING:
			return CommandStatus.RUNNING;
		case TERMINATED:
			return CommandStatus.TERMINATED;
		case SCHEDULED:
			// TODO: decide if we want this
			return CommandStatus.RUNNING;
		case INVALID:
			break;
		case UNLOADED:
			break;
		default:
			break;
		}
		return CommandStatus.ERROR;
	}

	@Override
	public void addStatusListener(final CommandStatusListener listener) throws CommandException {
		try {
			netHandle.addStatusListener(new NetStatusListener() {
				@Override
				public void statusChanged(final NetStatus newStatus) {
					if (newStatus == NetStatus.REJECTED) {
						throwException(new CommandException("Command has been rejected."));
					}
					if (newStatus == NetStatus.ERROR && occuredException == null) {
						occuredException = new CommandException("Command execution failed for unknown reason.");
					}
					if (newStatus != null) {
						listener.statusChanged(netToCommand(newStatus));
					}
					if (newStatus == null) {
						RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL, "Command status was null");
					}
				}
			});
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	@Override
	public boolean cancel() throws CommandException {
		try {
			return netHandle.cancel();
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	protected void unload() throws CommandException {
		try {
			netHandle.unload();
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	@Override
	public CommandStatus getStatus() throws CommandException {
		try {
			if (occuredException != null) {
				throw occuredException;
			}
			return netToCommand(netHandle.getStatus());
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	@Override
	public boolean start() throws CommandException {
		try {
			return netHandle.start();
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	@Override
	public void waitComplete() throws CommandException {
		try {
			// TODO: decide when to assume ERROR to be final
			netHandle.waitComplete();
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}

		if (occuredException != null) {
			throw occuredException;
		}
	}

	public void setOverride(final double override) throws CommandException {
		if (this.override == null) {
			return;
		}
		this.override.setString(Double.toString(override));
	}

	@Override
	public CommandException getOccurredException() {
		return occuredException;
	}

	@Override
	public boolean scheduleAfter(CommandHandle executeAfter) throws CommandException {
		try {
			if (executeAfter == null || !(executeAfter instanceof SoftRobotCommandHandle)) {
				throw new CommandException("Invalid command handle to schedule after");
			}
			return netHandle.scheduleAfter(((SoftRobotCommandHandle) executeAfter).netHandle);
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}

	@Override
	public void startThread(Runnable thread) {
		((SoftRobotNetHandle) netHandle).startThread(thread);
	}
}
