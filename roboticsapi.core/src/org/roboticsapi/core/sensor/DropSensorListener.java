/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.roboticsapi.core.SensorListener;

/**
 * A sensor listener that drops all values received while the previous value is
 * handled.
 * 
 * @param <T> type of sensor data
 */
public abstract class DropSensorListener<T> implements SensorListener<T> {

	Thread workerThread = new Thread();
	BlockingQueue<T> queue = new LinkedBlockingQueue<T>(1);

	public DropSensorListener() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						valueChanged(queue.take());
					}
				} catch (InterruptedException e) {
				}
			}
		});
		thread.setName("DropSensorListener " + getClass().getName());
		thread.setDaemon(true);
		thread.start();
	}

	protected abstract void valueChanged(T newValue);

	@Override
	public void onValueChanged(T newValue) {
		queue.poll();
		queue.offer(newValue);
	}

}
