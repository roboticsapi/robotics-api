/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class Observer<T> {
	private Command command;
	private final RealtimeValueListener<T> listener;
	private final RealtimeValue<T> sensor;
	private final RealtimeBoolean condition;
	private final boolean async;

	/**
	 * Creates a new Observer with the given values.
	 *
	 * @param sensor    The sensor value the constructed Observer observes.
	 * @param listener  The listener that the constructed Observer notifies.
	 * @param condition The condition when to observe the sensor (null means always)
	 * @param async     Notify the listener asynchronously.
	 */
	protected Observer(RealtimeValue<T> sensor, final RealtimeValueListener<T> listener, RealtimeBoolean condition,
			boolean async) {
		this.sensor = sensor;
		this.async = async;
		this.condition = condition;
		if (!async) {
			this.listener = listener;
		} else {
			this.listener = new RealtimeValueListener<T>() {
				@Override
				public void onValueChanged(final T newValue) {
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

	public RealtimeValueListener<T> getListener() {
		return listener;
	}

	public RealtimeBoolean getCondition() {
		return condition;
	}

	public RealtimeValue<T> getSensor() {
		return sensor;
	}

	public boolean isAsync() {
		return async;
	}
}
