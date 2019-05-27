/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.javarcc;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.runtime.controlcore.SoftRobotNetHandle;
import org.roboticsapi.runtime.javarcc.JNet;
import org.roboticsapi.runtime.javarcc.JNet.JNetListener;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.rpi.NetStatus;
import org.roboticsapi.runtime.rpi.NetStatusListener;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotJavaNetHandle implements SoftRobotNetHandle {
	private JNet net;
	private SoftRobotJavaControlCore rcc;
	private String name;
	private NetStatus lastStatus;
	private List<NetStatusListener> listeners = new ArrayList<NetStatusListener>();

	public JNet getNet() {
		return net;
	}

	public SoftRobotJavaNetHandle(SoftRobotJavaControlCore rcc, JNet net, String name) {
		this.rcc = rcc;
		this.net = net;
		this.name = name;
		net.setListener(new JNetListener() {
			@Override
			public void updated() {
				updateStatus();
			}
		});
	}

	@Override
	public boolean start() throws RPIException {
		boolean ret = rcc.start(this);
		if (ret)
			lastStatus = NetStatus.RUNNING;
		return ret;
	}

	@Override
	public boolean scheduleAfter(NetHandle previousNet) throws RPIException {
		return rcc.schedule(this, previousNet);
	}

	@Override
	public boolean abort() throws RPIException {
		if (net.isUnloaded())
			return false;
		net.kill();
		return true;
	}

	@Override
	public boolean cancel() throws RPIException {
		if (net.isUnloaded())
			return false;
		net.cancel();
		return true;
	}

	@Override
	public void unload() throws RPIException {
		rcc.unloadNet(this);
	}

	@Override
	public void waitComplete() throws RPIException {
		while (lastStatus != NetStatus.TERMINATED) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RPIException(e);
			}
		}

		try {
			for (Thread t : startedThreads)
				t.join();
		} catch (InterruptedException e) {
			throw new RPIException(e);
		}
	}

	@Override
	public NetStatus getStatus() throws RPIException {
		return lastStatus;
	}

	private void updateStatus() {
		NetStatus ret = NetStatus.READY;
		if (net.isCompleted() || net.isKilled())
			ret = NetStatus.TERMINATED;
		else if (net.isCancelled())
			ret = NetStatus.CANCELLING;
		else if (net.isStarted())
			ret = NetStatus.RUNNING;
		else
			ret = NetStatus.READY;

		if (lastStatus != ret) {
			lastStatus = ret;
			for (NetStatusListener listener : listeners)
				listener.statusChanged(ret);
		}

	}

	@Override
	public void addStatusListener(NetStatusListener listener) throws RPIException {
		updateStatus();
		listener.statusChanged(lastStatus);
		listeners.add(listener);
	}

	@Override
	public void suspendUpdate() {
	}

	@Override
	public void resumeUpdate() {
	}

	@Override
	public String getName() {
		return name;
	}

	List<Thread> startedThreads = new ArrayList<Thread>();

	public void startThread(Runnable thread) {
		Thread t = new Thread(thread);
		startedThreads.add(t);
		t.start();
	}

}
