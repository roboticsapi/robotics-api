/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;

public class Observer<T> {
	private Command command;
	private final SensorListener<T> listener;
	private final Sensor<T> sensor;
	private final boolean async;

	/**
	 * Creates a new Observer with the given values.
	 * 
	 * @param command  The Command the constructed Observer belongs to.
	 * @param sensor   The sensor value the constructed Observer observes.
	 * @param listener The listener that the constructed Observer notifies.
	 */
	public Observer(Command command, Sensor<T> sensor, SensorListener<T> listener) {
		this(sensor, listener, false);
		this.setCommand(command);
	}

	/**
	 * Creates a new Observer with the given values.
	 * 
	 * @param command  The Command the constructed Observer belongs to.
	 * @param sensor   The sensor value the constructed Observer observes.
	 * @param listener The listener that the constructed Observer notifies.
	 * @param async    Notify the listener asynchronously.
	 */
	public Observer(Command command, Sensor<T> sensor, SensorListener<T> listener, boolean async) {
		this(sensor, listener, async);
		this.setCommand(command);
	}

	/**
	 * Creates a new Observer with the given values.
	 * 
	 * @param sensor   The sensor value the constructed Observer observes.
	 * @param listener The listener that the constructed Observer notifies.
	 */
	public Observer(Sensor<T> sensor, SensorListener<T> listener) {
		this(sensor, listener, false);
	}

	/**
	 * Creates a new Observer with the given values.
	 * 
	 * @param sensor   The sensor value the constructed Observer observes.
	 * @param listener The listener that the constructed Observer notifies.
	 * @param async    Notify the listener asynchronously.
	 */
	public Observer(Sensor<T> sensor, final SensorListener<T> listener, boolean async) {
		this.sensor = sensor;
		this.async = async;
		if (!async) {
			this.listener = listener;
		} else {
			this.listener = new SensorListener<T>() {
				@Override
				public void onValueChanged(final T newValue) {
					if (getCommand() == null) {
						throw new UnsupportedOperationException(
								"Method getCommand() returned null. This is not allowed. Set valid Command before.");
					}

					if (getCommand().getCommandHandle() == null) {
						throw new UnsupportedOperationException(
								"Method getCommand().getCommandHandle() returned null. This is not allowed. Set valid CommandHandle for the Command before.");
					}

					getCommand().getCommandHandle().startThread(new Runnable() {
						@Override
						public void run() {
							listener.onValueChanged(newValue);
						}
					});
				}
			};
		}
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public SensorListener<T> getListener() {
		return listener;
	}

	public Sensor<T> getSensor() {
		return sensor;
	}

	// public Monitor<T> createMonitor(SensorListener<T> listener) {
	// Monitor<T> createMonitor = sensor.createMonitor(listener);
	//
	// createMonitor.setCommand(command);
	// return createMonitor;
	// }

	public boolean isAsync() {
		return async;
	}
}
