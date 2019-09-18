/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SensorListenerTest {

	@Test
	public void testDropSensorListenerDropsValues() throws InterruptedException {
		List<Integer> notified = new ArrayList<>();
		DropRealtimeValueListener<Integer> listener = new DropRealtimeValueListener<Integer>(1) {
			@Override
			protected void valueChanged(Integer newValue) {
				notified.add(newValue);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		};

		listener.onValueChanged(1);
		listener.onValueChanged(2);
		Thread.sleep(400);
		listener.onValueChanged(3);
		Thread.sleep(100);

		assertEquals(2, notified.size());
	}

	@Test
	public void testQueueSensorListenerQueuesValues() throws InterruptedException {
		List<Integer> notified = new ArrayList<>();
		QueueRealtimeValueListener<Integer> listener = new QueueRealtimeValueListener<Integer>(1) {
			@Override
			protected void valueChanged(Integer newValue) {
				notified.add(newValue);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		};

		listener.onValueChanged(1);
		listener.onValueChanged(2);
		Thread.sleep(400);
		listener.onValueChanged(3);
		Thread.sleep(100);

		assertEquals(3, notified.size());
	}

	@Test
	public void testDropSensorListenerReusesThread() throws InterruptedException {
		List<Thread> threads = new ArrayList<>();
		DropRealtimeValueListener<Integer> listener = new DropRealtimeValueListener<Integer>(0.2) {
			@Override
			protected void valueChanged(Integer newValue) {
				threads.add(Thread.currentThread());
			}
		};

		listener.onValueChanged(1);
		Thread.sleep(100);
		listener.onValueChanged(2);
		Thread.sleep(300);
		listener.onValueChanged(2);
		Thread.sleep(100);

		assertEquals(3, threads.size());
		assertEquals(threads.get(0), threads.get(1));
		assertNotEquals(threads.get(0), threads.get(2));
	}

	@Test
	public void testQueueSensorListenerReusesThread() throws InterruptedException {
		List<Thread> threads = new ArrayList<>();
		QueueRealtimeValueListener<Integer> listener = new QueueRealtimeValueListener<Integer>(0.2) {
			@Override
			protected void valueChanged(Integer newValue) {
				threads.add(Thread.currentThread());
			}
		};

		listener.onValueChanged(1);
		Thread.sleep(100);
		listener.onValueChanged(2);
		Thread.sleep(300);
		listener.onValueChanged(2);
		Thread.sleep(100);

		assertEquals(3, threads.size());
		assertEquals(threads.get(0), threads.get(1));
		assertNotEquals(threads.get(0), threads.get(2));
	}
}
