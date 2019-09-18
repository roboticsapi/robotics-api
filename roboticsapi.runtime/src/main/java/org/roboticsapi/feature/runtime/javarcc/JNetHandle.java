/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.facet.javarcc.JNet;
import org.roboticsapi.facet.javarcc.JNet.JNetListener;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetStatus;
import org.roboticsapi.facet.runtime.rpi.NetStatusListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public class JNetHandle implements NetHandle {
	private JNet net;
	private JavaControlCore rcc;
	private String name;
	private NetStatus lastStatus;
	private List<NetStatusListener> listeners = new ArrayList<NetStatusListener>();

	public JNet getNet() {
		return net;
	}

	public JNetHandle(JavaControlCore rcc, JNet net, String name) {
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
	public boolean start() throws RpiException {
		return rcc.start(Arrays.asList((NetHandle) this));
	}

	@Override
	public boolean scheduleWhen(List<NetResult> results) throws RpiException {
		return rcc.schedule(results, new ArrayList<NetHandle>(), new ArrayList<NetHandle>(),
				Arrays.asList(this)) != null;
	}

	@Override
	public boolean scheduleAfter(List<NetResult> results) throws RpiException {
		List<NetHandle> stopNets = new ArrayList<NetHandle>();
		for (NetResult result : results)
			stopNets.add(result.getHandle());
		return rcc.schedule(results, stopNets, new ArrayList<NetHandle>(), Arrays.asList(this)) != null;
	}

	@Override
	public boolean abort() throws RpiException {
		if (net.isUnloaded())
			return false;
		net.kill();
		return true;
	}

	@Override
	public boolean cancel() throws RpiException {
		if (net.isUnloaded())
			return false;
		net.cancel();
		return true;
	}

	@Override
	public void unload() throws RpiException {
		rcc.unloadNet(this);
	}

	@Override
	public void waitComplete() throws RpiException {
		while (lastStatus != NetStatus.TERMINATED) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RpiException(e);
			}
		}

		try {
			for (Thread t : startedThreads)
				t.join();
		} catch (InterruptedException e) {
			throw new RpiException(e);
		}
	}

	@Override
	public NetStatus getStatus() throws RpiException {
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
	public void addStatusListener(NetStatusListener listener) throws RpiException {
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

	@Override
	public NetResult createResult(NetcommValue condition) {
		return new JNetResult(this, condition);
	}
}
