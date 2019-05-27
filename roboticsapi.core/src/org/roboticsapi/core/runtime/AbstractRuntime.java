/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.SensorListenerRegistration;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * Abstract RoboticsRuntime implementation with basic handling of Device and
 * Sensor Configurations.
 */
public abstract class AbstractRuntime extends RoboticsRuntime {
	private final List<CommandHook> commandHooks = new Vector<CommandHook>();
	private final List<CommandFilter> commandFilters = new Vector<CommandFilter>();
	private CommandHandle sensorListenerHandle;
	private final Map<Sensor<?>, SensorListenerList<?>> sensorListeners = new HashMap<Sensor<?>, SensorListenerList<?>>();

	public AbstractRuntime() {
		addOperationStateListener(new PresentListener() {
			@Override
			public void isPresent(OnlineObject ro) {
				try {
					updateObserverCommand();
				} catch (RoboticsException e) {
				}
			}

			@Override
			public void isNotPresent(OnlineObject ro) {
			}
		});
	}

	@Override
	public List<CommandHook> getCommandHooks() {
		return Collections.unmodifiableList(commandHooks);
	}

	@Override
	public void addCommandHook(CommandHook hook) {
		commandHooks.add(hook);
	}

	@Override
	public void removeCommandHook(CommandHook hook) {
		commandHooks.remove(hook);
	}

	@Override
	public void addCommandFilter(CommandFilter filter) {
		commandFilters.add(filter);
	}

	@Override
	public void removeCommandFilter(CommandFilter filter) {
		commandFilters.remove(filter);
	}

	@Override
	public final CommandHandle load(Command command) throws RoboticsException {
		filterCommand(command);

		for (CommandHook hook : commandHooks) {
			hook.commandLoadHook(command);
		}

		// TODO May CommandHandle handle be null?
		// (loadCommand(command) could return null)
		CommandHandle handle = loadCommand(command);

		for (CommandHook hook : commandHooks) {
			hook.commandHandleHook(handle);
		}

		return handle;
	}

	public abstract CommandHandle loadCommand(Command command) throws RoboticsException;

	private void filterCommand(Command command) {
		for (CommandFilter filter : commandFilters) {
			if (filter.filter(command)) {
				filter.process(command);
			}
		}

		if (command instanceof TransactionCommand) {
			for (Command inner : ((TransactionCommand) command).getCommandsInTransaction()) {
				filterCommand(inner);
			}
		}
	}

	@Override
	public <T> T getSensorValue(Sensor<T> sensor) throws RoboticsException {
		checkBlockEventHandlerThread();

		if (sensorListeners.containsKey(sensor)) {
			@SuppressWarnings("unchecked")
			SensorListenerList<T> listenerList = (SensorListenerList<T>) sensorListeners.get(sensor);
			if (listenerList.lastValue != null) {
				return listenerList.lastValue;
			}
		}

		final List<T> results = new ArrayList<T>();
		Command sensorCmd = createWaitCommand("Read " + sensor.toString());
		sensorCmd.addDoneStateCondition(State.True());
		sensorCmd.addObserver(sensor, new SensorListener<T>() {
			@Override
			public void onValueChanged(T newValue) {
				results.add(newValue);
			}
		});
		sensorCmd.execute();

		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public synchronized void addSensorListeners(List<SensorListenerRegistration<?>> listeners)
			throws RoboticsException {
		boolean needsCommandUpdate = false;

		for (SensorListenerRegistration<?> reg : listeners) {

			if (reg.getSensor().getRuntime() != this) {
				throw new RoboticsException(
						"Can only add sensor listeners with the Sensor having the right RoboticsRuntime");
			}

			SensorListenerList listenerList = sensorListeners.get(reg.getSensor());

			if (listenerList == null) {
				listenerList = new SensorListenerList();

				sensorListeners.put(reg.getSensor(), listenerList);

				needsCommandUpdate = true;
			}
			listenerList.add(reg.getListener());
		}

		if (needsCommandUpdate && isPresent()) {
			updateObserverCommand();
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removeSensorListeners(List<SensorListenerRegistration<?>> listeners) throws RoboticsException {
		boolean needsCommandUpdate = false;

		for (SensorListenerRegistration<?> reg : listeners) {

			if (reg.getSensor().getRuntime() != this) {
				throw new RoboticsException(
						"Can only remove sensor listeners with the Sensor having the right RoboticsRuntime");
			}

			SensorListenerList listenerList = sensorListeners.get(reg.getSensor());

			if (listenerList == null) {
				return;
			}

			listenerList.remove(reg.getListener());

			if (listenerList.isEmpty()) {
				sensorListeners.remove(reg.getSensor());
				needsCommandUpdate = true;
			}
		}

		if (needsCommandUpdate) {
			updateObserverCommand();
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateObserverCommand() throws RoboticsException {
		if (sensorListeners.isEmpty()) {
			if (sensorListenerHandle != null) {
				try {
					sensorListenerHandle.cancel();
				} catch (RoboticsException ex) {
				}
			}
			sensorListenerHandle = null;
			return;
		}

		final Set<Sensor<?>> absentSensors = new HashSet<Sensor<?>>();

		Command observer = createWaitCommand(getName() + ":SensorListeners");

		observer.addTakeoverAllowedCondition(State.True());

		for (final Sensor<?> s : sensorListeners.keySet()) {
			if (!s.isAvailable()) {
				absentSensors.add(s);
			} else {
				try {
					observer.addObserver(s, new SensorListener() {
						@Override
						public void onValueChanged(Object newValue) {
							SensorListenerList<?> sensorListenerList = sensorListeners.get(s);
							if (sensorListenerList == null) {
								return;
							}
							((SensorListenerList) sensorListenerList).valueChanged(newValue);

						}
					});
				} catch (RoboticsException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (sensorListenerHandle != null && sensorListenerHandle.getOccurredException() == null) {
			sensorListenerHandle = observer.scheduleAfter(sensorListenerHandle);
		} else {
			sensorListenerHandle = observer.start();
		}

		// when the observer command dies, retry...
		sensorListenerHandle.addStatusListener(new CommandStatusListener() {
			@Override
			public void statusChanged(CommandStatus newStatus) {
				if (newStatus == CommandStatus.ERROR) {
					sensorListenerHandle = null;
					try {
						updateObserverCommand();
					} catch (RoboticsException e) {
					}
				}
			}
		});

		// while we have absent sensors, search for ones that appear
		if (absentSensors.size() > 0) {
			final CommandHandle listenerHandle = sensorListenerHandle;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (sensorListenerHandle == listenerHandle) {
						for (Sensor<?> s : absentSensors) {
							if (s.isAvailable()) {
								try {
									updateObserverCommand();
								} catch (RoboticsException e) {
								}
								return;
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}).start();
		}
	}

	private class SensorListenerList<T> extends ArrayList<SensorListener<T>> {
		/**
		 *
		 */
		private static final long serialVersionUID = 1462101597073795749L;
		private T lastValue;

		@Override
		public synchronized boolean add(SensorListener<T> newListener) {
			if (lastValue != null) {
				newListener.onValueChanged(lastValue);
			}

			return super.add(newListener);
		}

		public synchronized void valueChanged(T newValue) {
			this.lastValue = newValue;
			for (SensorListener<T> s : this) {
				s.onValueChanged(newValue);
			}
		}

		@Override
		public synchronized boolean remove(Object o) {
			return super.remove(o);
		}
	}

	@Override
	protected synchronized void beforeUninitialization() throws RoboticsException {
		sensorListeners.clear();
		if (sensorListenerHandle != null) {
			try {
				sensorListenerHandle.cancel();
			} catch (CommandException e) {
				// ignore that
			}
			sensorListenerHandle = null;
		}

		super.beforeUninitialization();
	}

}
