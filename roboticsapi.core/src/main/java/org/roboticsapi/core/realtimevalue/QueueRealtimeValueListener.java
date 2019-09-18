/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.core.RealtimeValueListener;

/**
 * A sensor listener that queues all values received while the previous value is
 * handled, and processes them afterwards
 *
 * @param <T> type of sensor data
 */
public abstract class QueueRealtimeValueListener<T> implements RealtimeValueListener<T> {
	Thread workerThread;
	BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	private final double timeout;

	/**
	 * Creates a QueueSensorListener
	 *
	 * @param timeout number of seconds until idle handler thread is stopped
	 */
	public QueueRealtimeValueListener(double timeout) {
		this.timeout = timeout;
	}

	/**
	 * Creates a QueueSensorListener
	 */
	public QueueRealtimeValueListener() {
		this(2);
	}

	private void ensureThread() {
		if (workerThread != null) {
			return;
		}
		workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						T value = queue.poll((int) (timeout * 1000), TimeUnit.MILLISECONDS);
						if (value == null) {
							workerThread = null;
							return;
						}
						valueChanged(value);
					}
				} catch (InterruptedException e) {
					workerThread = null;
				}
			}
		});
		workerThread.setName("QueueSensorListener " + getClass().getName());
		workerThread.setDaemon(true);
		workerThread.start();
	}

	protected abstract void valueChanged(T newValue);

	@Override
	public void onValueChanged(T newValue) {
		queue.offer(newValue);
		ensureThread();
	}

}
