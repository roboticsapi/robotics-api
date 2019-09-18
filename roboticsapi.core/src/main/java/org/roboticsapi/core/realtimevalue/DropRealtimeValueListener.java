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
 * A sensor listener that drops all values received while the previous value is
 * handled.
 *
 * @param <T> type of sensor data
 */
public abstract class DropRealtimeValueListener<T> implements RealtimeValueListener<T> {

	Thread workerThread;
	BlockingQueue<T> queue = new LinkedBlockingQueue<T>(1);
	private final double timeout;

	/**
	 * Creates a DropSensorListener
	 *
	 * @param timeout number of seconds until idle handler thread is stopped
	 */
	public DropRealtimeValueListener(double timeout) {
		this.timeout = timeout;
	}

	/**
	 * Creates a DropSensorListener
	 */
	public DropRealtimeValueListener() {
		this(2);
	}

	private void ensureThread() {
		synchronized (queue) {
			if (workerThread != null) {
				return;
			}
		}
		workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						T elem;
						elem = queue.poll((int) (timeout * 1000), TimeUnit.MILLISECONDS);
						if (elem == null) {
							workerThread = null;
							return;
						}
						valueChanged(elem);
					}
				} catch (InterruptedException e) {
					workerThread = null;
				}
			}
		});
		workerThread.setName("DropSensorListener " + getClass().getName());
		workerThread.setDaemon(true);
		workerThread.start();
	}

	protected abstract void valueChanged(T newValue);

	@Override
	public void onValueChanged(T newValue) {
		queue.poll();
		queue.offer(newValue);
		ensureThread();
	}

}
