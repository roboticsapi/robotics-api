/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * Abstract RoboticsRuntime implementation with basic handling of Device and
 * Sensor Configurations.
 */
public abstract class AbstractRuntime extends RoboticsRuntime {
	private final List<CommandHook> commandHooks = new Vector<CommandHook>();
	private final List<CommandFilter> commandFilters = new Vector<CommandFilter>();
	private CommandHandle sensorListenerHandle;
	private CommandResult sensorListenerResult;
	private final Map<RealtimeValue<?>, RealtimeValueListenerList<?>> sensorListeners = new HashMap<RealtimeValue<?>, RealtimeValueListenerList<?>>();

	public AbstractRuntime() {

		// refresh observer command when runtime becomes present
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

		// apply command filters
		addCommandHook(new CommandHookAdapter() {
			@Override
			public void commandLoadHook(Command command) {
				for (CommandFilter filter : commandFilters) {
					command.applyFilter(filter);
				}
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
	public void addCommandFilter(final CommandFilter filter) {
		commandFilters.add(filter);
	}

	@Override
	public void removeCommandFilter(CommandFilter filter) {
		commandFilters.remove(filter);
	}

	@Override
	public synchronized void addRelatimeValueListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException {
		boolean needsCommandUpdate = false;

		for (RealtimeValueListenerRegistration<?> reg : listeners) {

			if (reg.getSensor().getRuntime() != this && reg.getSensor().getRuntime() != null) {
				throw new RoboticsException(
						"Can only add sensor listeners with the Sensor having the right RoboticsRuntime");
			}

			needsCommandUpdate = addToListenerList(needsCommandUpdate, reg);
		}

		if (needsCommandUpdate && isPresent()) {
			updateObserverCommand();
		}

	}

	@SuppressWarnings("unchecked")
	private <U, T extends U> boolean addToListenerList(boolean needsCommandUpdate,
			RealtimeValueListenerRegistration<T> reg) {
		RealtimeValueListenerList<U> listenerList = (RealtimeValueListenerList<U>) sensorListeners.get(reg.getSensor());

		if (listenerList == null) {
			listenerList = new RealtimeValueListenerList<U>();

			sensorListeners.put(reg.getSensor(), listenerList);

			needsCommandUpdate = true;
		}
		listenerList.add((RealtimeValueListener<U>) reg.getListener());
		return needsCommandUpdate;
	}

	@Override
	public void removeRealtimeValueListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException {
		boolean needsCommandUpdate = false;

		for (RealtimeValueListenerRegistration<?> reg : listeners) {

			if (reg.getSensor().getRuntime() != this) {
				throw new RoboticsException(
						"Can only remove sensor listeners with the Sensor having the right RoboticsRuntime");
			}

			RealtimeValueListenerList<?> listenerList = sensorListeners.get(reg.getSensor());

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

	private synchronized void updateObserverCommand() throws RoboticsException {
		if (sensorListeners.isEmpty()) {
			if (sensorListenerHandle != null) {
				try {
					sensorListenerHandle.cancel();
				} catch (RoboticsException ex) {
				}
			}
			sensorListenerHandle = null;
			sensorListenerResult = null;
			return;
		}

		final Set<RealtimeValue<?>> absentSensors = new HashSet<RealtimeValue<?>>();

		Command observer = createWaitCommand(getName() + ":SensorListeners");
		observer.addCompletionResult("Cancelled", observer.getCancelState(), true);
		CommandResult takeoverResult = observer.addTakeoverResult("Takeover", RealtimeBoolean.TRUE, false);
		for (final RealtimeValue<?> s : sensorListeners.keySet()) {
			if (!s.isAvailable()) {
				absentSensors.add(s);
			} else {
				try {
					addObserver(observer, s);
				} catch (RoboticsException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (sensorListenerResult != null) {
			sensorListenerHandle = observer.scheduleAfter(sensorListenerResult);
		} else {
			sensorListenerHandle = observer.start();
		}
		sensorListenerResult = takeoverResult;

		// when the observer command dies, retry...
		final CommandHandle handle = sensorListenerHandle;
		sensorListenerHandle.addStatusListener(new CommandStatusListener() {
			@Override
			public void statusChanged(CommandStatus newStatus) {
				if (newStatus == CommandStatus.ERROR) {
					sensorListenerHandle = null;
					try {
						updateObserverCommand();
					} catch (RoboticsException e) {
					}
				} else if (newStatus == CommandStatus.TERMINATED) {
					try {
						handle.unload();
					} catch (CommandException e) {
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
						for (RealtimeValue<?> s : absentSensors) {
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

	private <T> void addObserver(Command observer, final RealtimeValue<T> s) throws RoboticsException {
		observer.addObserver(s, new RealtimeValueListener<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onValueChanged(T newValue) {
				RealtimeValueListenerList<T> sensorListenerList = (RealtimeValueListenerList<T>) sensorListeners.get(s);
				if (sensorListenerList == null) {
					return;
				}
				sensorListenerList.valueChanged(newValue);

			}
		});
	}

	private class RealtimeValueListenerList<T> extends ArrayList<RealtimeValueListener<T>> {
		/**
		 *
		 */
		private static final long serialVersionUID = 1462101597073795749L;
		private T lastValue;

		@Override
		public synchronized boolean add(RealtimeValueListener<T> newListener) {
			if (lastValue != null) {
				newListener.onValueChanged(lastValue);
			}

			return super.add(newListener);
		}

		public synchronized void valueChanged(T newValue) {
			this.lastValue = newValue;
			for (RealtimeValueListener<T> s : new ArrayList<RealtimeValueListener<T>>(this)) {
				s.onValueChanged(newValue);
			}
		}

		@Override
		public synchronized boolean remove(Object o) {
			return super.remove(o);
		}
	}

	@Override
	protected void beforeUninitialization() {
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
