/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandResult.Status;
import org.roboticsapi.core.CommandResultListener;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.UnhandledErrorsException;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetStatus;
import org.roboticsapi.facet.runtime.rpi.NetStatusListener;
import org.roboticsapi.facet.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public class BasicCommandHandle implements RpiCommandHandle {
	private final NetHandle net;
	private Command command;
	private final Map<CommandResult, NetResult> netResults;
	private CommandStatus lastStatus = CommandStatus.READY;
	private List<CommandStatusListener> statusListeners = new ArrayList<>();
	private List<CommandResultListener> resultListeners = new ArrayList<>();
	private List<CommandResult> finalResults = null;

	public BasicCommandHandle(Command command, NetHandle net, Map<CommandResult, NetcommValue> resultMap)
			throws CommandException {
		this.command = command;
		this.net = net;
		this.netResults = new HashMap<CommandResult, NetResult>();
		for (final Map.Entry<CommandResult, NetcommValue> result : resultMap.entrySet()) {
			NetResult netresult = net.createResult(result.getValue());
			netResults.put(result.getKey(), netresult);
			result.getValue().addNetcommListener(new NetcommListenerAdapter() {
				@Override
				public void valueChanged(NetcommValue value) {
					for (CommandResultListener listener : resultListeners)
						listener.statusChanged(result.getKey(), netresult.isActive() ? Status.ACTIVE : Status.POSSIBLE);
				}
			});
		}

		try {
			net.addStatusListener(new NetStatusListener() {
				@Override
				public void statusChanged(NetStatus newStatus) {
					updateStatus();
				}
			});

		} catch (RpiException e) {
			throw new CommandException("Could not add status listener.", e);
		}
	}

	protected RpiCommandHandle checkHandle(CommandHandle handle) {
		if (handle instanceof RpiCommandHandle)
			return (RpiCommandHandle) handle;
		else
			return null;
	}

	private synchronized void updateStatus() {
		CommandStatus status = CommandStatus.READY;
		try {
			switch (net.getStatus()) {
			case ERROR:
			case INVALID:
			case REJECTED:
				status = CommandStatus.ERROR;
				break;
			case LOADING:
			case READY:
				status = CommandStatus.READY;
				break;
			case RUNNING:
			case CANCELLING:
				status = CommandStatus.RUNNING;
				break;
			case SCHEDULED:
				status = CommandStatus.SCHEDULED;
				break;
			case TERMINATED:
			case UNLOADED:
				status = CommandStatus.TERMINATED;
				break;
			default:
				break;
			}
		} catch (RpiException e) {
			status = CommandStatus.ERROR;
			throwException(new CommandException(e.getMessage(), e));
		}
		if (status != lastStatus) {

			if (finalResults == null && (status == CommandStatus.TERMINATED || status == CommandStatus.ERROR)) {
				finalResults = new ArrayList<CommandResult>();
				for (CommandResult result : netResults.keySet()) {
					Boolean active = netResults.get(result).isActive();
					if (active != null && active) {
						finalResults.add(result);
						if (result.getException() != null)
							throwException(result.getException());
					} else {
						for (CommandResultListener listener : resultListeners) {
							listener.statusChanged(result, Status.IMPOSSIBLE);
						}
					}
				}
			}

			lastStatus = status;
			for (CommandStatusListener listener : statusListeners)
				listener.statusChanged(lastStatus);
		}
	}

	private <T> List<T> asList(T handle) {
		ArrayList<T> ret = new ArrayList<T>();
		ret.add(handle);
		return ret;
	}

	@Override
	public boolean start() throws CommandException {
		command.getRuntime().executeOperations(Arrays.asList(getStartOperation()), null);
		return true;
	}

	@Override
	public boolean scheduleAfter(CommandResult executeAfter) throws CommandException {
		command.getRuntime().scheduleOperations(Arrays.asList(executeAfter),
				Arrays.asList(getStartOperation(), executeAfter.getCommand().getCommandHandle().getAbortOperation()),
				null);
		return true;
	}

	@Override
	public boolean scheduleWhen(CommandResult executeWhen) throws CommandException {
		command.getRuntime().scheduleOperations(Arrays.asList(executeWhen), Arrays.asList(getStartOperation()), null);
		return true;
	}

	@Override
	public boolean abort() throws CommandException {
		command.getRuntime().executeOperations(Arrays.asList(getAbortOperation()), null);
		return true;
	}

	@Override
	public boolean cancel() throws CommandException {
		command.getRuntime().executeOperations(Arrays.asList(getCancelOperation()), null);
		return true;
	}

	private List<Thread> startedThreads = new ArrayList<Thread>();
	private List<CommandException> thrownExceptions = new ArrayList<CommandException>();

	@Override
	public void waitComplete() throws CommandException {
		try {
			net.waitComplete();
			updateStatus();
		} catch (RpiException e) {
			throw new CommandException("Error waiting for net", e);
		}
		try {
			for (Thread t : startedThreads) {
				t.join();
			}
		} catch (InterruptedException e) {
			throw new CommandException("Interrupted", e);
		}
		if (!thrownExceptions.isEmpty()) {
			List<CommandException> thrown = new ArrayList<CommandException>();
			for (CommandException e : thrownExceptions) {
				if (e instanceof UnhandledErrorsException) {
					thrown.addAll(((UnhandledErrorsException) e).getInnerExceptions());
				} else {
					thrown.add(e);
				}
			}
			throw command.getExceptionAggregator().aggregate(thrownExceptions);
		}
	}

	@Override
	public CommandStatus getStatus() throws CommandException {
		updateStatus();
		return lastStatus;
	}

	@Override
	public void addStatusListener(CommandStatusListener listener) throws CommandException {
		updateStatus();
		statusListeners.add(listener);
		listener.statusChanged(lastStatus);
	}

	@Override
	public void throwException(CommandException ex) {
		thrownExceptions.add(ex);
	}

	@Override
	public List<CommandException> getOccurredExceptions() {
		return thrownExceptions;
	}

	@Override
	public void startThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		startedThreads.add(thread);
	}

	@Override
	public List<CommandResult> getFinalCommandResults() {
		return finalResults;
	}

	@Override
	public List<CommandResult> getPossibleCommandResults() {
		updateStatus();
		if (finalResults != null)
			return finalResults;
		else
			return new ArrayList<CommandResult>(netResults.keySet());
	}

	@Override
	public NetHandle getNetHandle() {
		return net;
	}

	@Override
	public List<NetResult> getNetResults(CommandResult commandResult) {
		return asList(netResults.get(commandResult));
	}

	@Override
	public CommandHandleOperation getStartOperation() {
		return new RpiSchedulingOperation(this, asList(net), null, null);
	}

	@Override
	public CommandHandleOperation getCancelOperation() {
		return new RpiSchedulingOperation(this, null, asList(net), null);
	}

	@Override
	public CommandHandleOperation getAbortOperation() {
		return new RpiSchedulingOperation(this, null, null, asList(net));
	}

	@Override
	public Set<CommandResult> getActiveCommandResults() {
		Set<CommandResult> ret = new HashSet<CommandResult>();
		for (CommandResult result : getPossibleCommandResults()) {
			if (netResults.get(result).isActive())
				ret.add(result);
		}
		return ret;
	}

	@Override
	public void unload() throws CommandException {
		try {
			net.unload();
			command = null;
			// netResults.clear();
		} catch (RpiException e) {
			throw new CommandException("Error unloading command", e);
		}
	}

	@Override
	public void addCommandResultListener(CommandResultListener listener) {
		List<CommandResultListener> resultListeners = new ArrayList<>(this.resultListeners);
		resultListeners.add(listener);
		this.resultListeners = resultListeners;
		for (CommandResult result : getPossibleCommandResults()) {
			if (netResults.get(result).isActive())
				listener.statusChanged(result, Status.ACTIVE);
			else
				listener.statusChanged(result, Status.POSSIBLE);
		}
	}

	@Override
	public void removeCommandResultListener(CommandResultListener listener) {
		List<CommandResultListener> resultListeners = new ArrayList<>(this.resultListeners);
		resultListeners.add(listener);
		resultListeners.remove(listener);
		this.resultListeners = resultListeners;
	}
}
