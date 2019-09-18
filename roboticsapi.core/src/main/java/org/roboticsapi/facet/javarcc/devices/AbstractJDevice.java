/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.roboticsapi.core.util.RAPILogger;

public abstract class AbstractJDevice implements JDevice {

	private int locks = 0;
	private boolean critical = false;
	private Object lockObject = new Object();
	private boolean stopNow = false;
	private List<CyclicTask> tasks = new ArrayList<>();

	public void addTask(CyclicTask task) {
		if (tasks.contains(task))
			throw new IllegalArgumentException();
		tasks.add(task);
	}

	private final Object monitor = new Object();
	private final Map<CyclicTask, Thread> threads = new HashMap<>();

	@Override
	public void start() throws Exception {
		stopNow = false;
		for (CyclicTask task : tasks) {
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!stopNow) {
						try {
							task.doCyclicTask();
						} catch (InterruptedException e) {
						} catch (Exception e) {
							RAPILogger.getLogger(this).log(Level.WARNING, "Unexpected exception in cyclic task", e);
						}
					}
					synchronized (monitor) {
						threads.remove(task);
						monitor.notify();
					}
				}
			});
			t.setName("JDevice " + getClass().getSimpleName());
			threads.put(task, t);
			t.start();
		}
	}

	@Override
	public void destroy() throws Exception {
		stopNow = true;
		synchronized (monitor) {
			threads.forEach((task, thread) -> {
				thread.interrupt();
			});
		}
		synchronized (monitor) {
			while (!threads.isEmpty()) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	public void lock() {
		synchronized (lockObject) {
			while (critical)
				waitForLock();
			locks++;
		}
	}

	@Override
	public void unlock() {
		synchronized (lockObject) {
			locks--;
			if (locks == 0)
				lockObject.notify();
		}
	}

	private void waitForLock() {
		try {
			lockObject.wait();
		} catch (InterruptedException e) {
		}
	}

	protected boolean isLocked() {
		synchronized (lockObject) {
			return locks > 0;
		}
	}

	protected void enterCriticalSection() {
		synchronized (lockObject) {
			while (isLocked())
				waitForLock();
			critical = true;
		}
	}

	protected void leaveCriticalSection() {
		synchronized (lockObject) {
			critical = false;
			lockObject.notify();
		}
	}

}
