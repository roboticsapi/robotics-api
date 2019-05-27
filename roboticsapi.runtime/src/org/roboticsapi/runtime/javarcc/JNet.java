/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.roboticsapi.runtime.core.javarcc.devices.DeviceRegistry;
import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.rpi.types.Type;

public class JNet {
	public interface JNetListener {
		void updated();
	}

	public interface JNetcommListener {
		void valueChanged(String value);

		void updatePerformed();
	}

	private DeviceRegistry devices;
	private JNetListener listener = null;
	private Map<String, JNetcommData<?>> netcomm = new HashMap<>();
	private JFragment root;
	private String description;
	private double cycleTime = 0.01;
	private JNet successor = null;

	private boolean completed = false;
	private boolean cancelled = false;
	private boolean killed = false;
	private int cycle = 0;
	private long time = -1;
	private String name;
	private Executor executor;
	private Map<JInPort<?>, String[]> debug;

	public JNet(String name, String description, JFragment root, DeviceRegistry devices, Executor netcommExecutor,
			Map<JInPort<?>, String[]> debug) {
		this.name = name;
		this.description = description;
		this.root = root;
		this.devices = devices;
		this.executor = netcommExecutor;
		this.debug = debug;
		root.setNet(this);
	}

	@Override
	public String toString() {
		return name + " (" + description + ")";
	}

	public void setListener(JNetListener listener) {
		this.listener = listener;
	}

	public JFragment getFragment() {
		return root;
	}

	public int getCycle() {
		return cycle;
	}

	public double getCycleTime() {
		return cycleTime;
	}

	public void readSensor() {
		root.readSensor();
	}

	public void updateData() {
		if (cycle == 0) {
			time = System.currentTimeMillis();
		} else {
			time += getCycleTime() * 1000;
		}
		cycle++;
		root.updateData();
		root.recordDebugValues(debug);
	}

	public void writeActuator() {
		root.writeActuator();
		if (root.getOutPort("outTerminate") != null && root.getOutPort("outTerminate").get() != null
				&& ((RPIbool) root.getOutPort("outTerminate").get()).get()) {
			completed = true;
			notifyStatusChanged();
		}
	}

	private Map<JNetcommListener, String> lastReport = new HashMap<>();
	private long lastReportTime = 0;
	private final int MIN_REPORT_DELAY = 20;

	public void sendNetcomm() {
		if (System.currentTimeMillis() - lastReportTime < MIN_REPORT_DELAY)
			return;
		JFragment root = this.root;
		if (root == null)
			return;

		Map<JNetcommListener, String> valuesToReport = new HashMap<>();
		Map<JNetcommListener, String> update = new HashMap<>();
		List<JNetcommListener> listeners = new ArrayList<>();
		root.addValuesToReport(valuesToReport);
		root.addListenersToReport(listeners);
		for (Map.Entry<JNetcommListener, String> entry : valuesToReport.entrySet()) {
			if (!lastReport.containsKey(entry.getKey()) || !lastReport.get(entry.getKey()).equals(entry.getValue())) {
				update.put(entry.getKey(), entry.getValue());
			}
		}

		if (update.isEmpty())
			return;

		for (Map.Entry<JNetcommListener, String> entry : update.entrySet()) {
			lastReport.put(entry.getKey(), entry.getValue());
		}
		lastReportTime = System.currentTimeMillis();

		executor.execute(() -> {
			for (Map.Entry<JNetcommListener, String> entry : update.entrySet()) {
				entry.getKey().valueChanged(entry.getValue());
			}
			for (JNetcommListener listener : listeners) {
				listener.updatePerformed();
			}
		});
	}

	public void notifyListener() {
		if (listener != null) {
			executor.execute(() -> listener.updated());
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isKilled() {
		return killed;
	}

	public boolean isUnloaded() {
		return root == null;
	}

	public void kill() {
		// for (JNetcommData<?> value : netcomm.values())
		// value.markKilled();
		if (killed == false) {
			killed = true;
			notifyStatusChanged();
		}
	}

	public void cancel() {
		if (cancelled == false) {
			cancelled = true;
			notifyStatusChanged();
		}
	}

	private void notifyStatusChanged() {
		lastReportTime = 0;
		sendNetcomm();
		notifyListener();
	}

	public Long getTime() {
		return time;
	}

	public void addNetcomm(String key, JNetcommData<?> data) {
		netcomm.put(key, data);
	}

	@SuppressWarnings("unchecked")
	public <T extends Type> JNetcommData<T> getNetcomm(String key) {
		return (JNetcommData<T>) netcomm.get(key);
	}

	public boolean isStarted() {
		return cycle > 0;
	}

	public void lockSensors() {
		if (root != null)
			for (JDevice d : root.getSensors())
				d.lock();
	}

	public void unlockSensors() {
		if (root != null)
			for (JDevice d : root.getSensors())
				d.unlock();
	}

	public void lockActuators() {
		if (root != null)
			for (JDevice d : root.getActuators())
				d.lock();
	}

	public void unlockActuators() {
		if (root != null)
			for (JDevice d : root.getActuators())
				d.unlock();
	}

	public <T extends JDevice> T getDevice(String type, Class<T> clazz) {
		return devices.get(type, clazz);
	}

	public void scheduleNotificationTask(Runnable task) {
		executor.execute(task);
	}

	public void setSuccessor(JNet successor) {
		this.successor = successor;
	}

	public void unload() {
		if (root != null)
			root.cleanup();
		root = null;
	}

	public boolean hasSuccessor() {
		return successor != null;
	}

	public JNet getSuccessor() {
		return successor;
	}
}
