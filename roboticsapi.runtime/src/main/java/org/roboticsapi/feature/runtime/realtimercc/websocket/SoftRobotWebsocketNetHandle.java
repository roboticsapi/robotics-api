/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetStatus;
import org.roboticsapi.facet.runtime.rpi.NetStatusListener;
import org.roboticsapi.facet.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.feature.runtime.realtimercc.RccNetResult;
import org.roboticsapi.feature.runtime.realtimercc.SoftRobotNetHandle;
import org.roboticsapi.feature.runtime.realtimercc.SoftRobotNetXml;

public class SoftRobotWebsocketNetHandle implements SoftRobotNetHandle {
	protected boolean suspended = false;
	private final Hashtable<String, NetcommValue> suspendedNetcommToRPI = new Hashtable<String, NetcommValue>();
	private Fragment net = null;
	private String xml = null;
	private String name;
	private final List<NetStatusListener> statusListeners = new Vector<NetStatusListener>();
	private NetStatus resultStatus = null;
	private NetStatus status = NetStatus.LOADING;
	private RpiException resultException = null;
	private final SoftRobotWebsocketControlCore controlCore;
	private final List<Thread> startedThreads = new Vector<Thread>();

	protected void setResultStatus(NetStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	protected void setStatusException(RpiException resultException) {
		this.resultException = resultException;
	}

	@Override
	public boolean isUnloaded() {
		return status == NetStatus.UNLOADED;
	}

	/**
	 * Creates a new SoftRobot net
	 *
	 * @param controlCore
	 */
	public SoftRobotWebsocketNetHandle(SoftRobotWebsocketControlCore controlCore) {
		this.controlCore = controlCore;
	}

	@Override
	public boolean start() throws RpiException {
		return controlCore.startNets(Arrays.asList(this));
	}

	@Override
	public boolean abort() throws RpiException {
		return controlCore.abortNet(this);
	}

	@Override
	public boolean cancel() throws RpiException {
		return controlCore.cancelNet(this);
	}

	@Override
	public void unload() throws RpiException {
		if (resultStatus == null) {
			controlCore.unloadNet(this);
			xml = null;
			net = null;
		}
	}

	@Override
	public void waitComplete() throws RpiException {
		final Semaphore wait = new Semaphore(1);
		controlCore.checkBlockControlThread();
		try {
			wait.acquire();
		} catch (InterruptedException e1) {
		}

		// make sure to have a status listener
		addStatusListener(new NetStatusListener() {
			@Override
			public void statusChanged(final NetStatus newStatus) {
				if (newStatus == NetStatus.ERROR || newStatus == NetStatus.TERMINATED
						|| newStatus == NetStatus.REJECTED) {
					synchronized (SoftRobotWebsocketNetHandle.this) {
						wait.release();
					}
				}
			}
		});

		try {
			wait.acquire();
		} catch (InterruptedException e) {
			throw new RpiException("Interrupted");
		}

		for (Thread t : startedThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}

		// controlCore.waitForThreads();

		unload();
	}

	@Override
	public NetStatus getStatus() throws RpiException {
		if (resultException != null) {
			throw resultException;
		}

		if (resultStatus != null) {
			return resultStatus;
		}

		return status;
	}

	protected void setStatus(final NetStatus status) {
		if (this.status != status) {
			this.status = status;
			synchronized (statusListeners) {
				for (final NetStatusListener l : new ArrayList<NetStatusListener>(statusListeners)) {
					controlCore.queueNotify(new Runnable() {
						@Override
						public void run() {
							try {
								l.statusChanged(status);
							} catch (Exception ex) {
								setStatusException(new RpiException("Error in net status listener", ex));
							}
						};
					});
				}
			}
		}
	}

	@Override
	public void addStatusListener(final NetStatusListener listener) throws RpiException {
		statusListeners.add(listener);
		if (getStatus() != null) {
			listener.statusChanged(getStatus());
		}
	}

	@Override
	public String getName() {
		while (name == null) {
			synchronized (this) {
				if (name == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						return null;
					}
				}
			}
		}
		return name;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setNet(Fragment net) {
		this.net = net;
	}

	public String getXml() {
		if (xml != null) {
			return xml;
		}
		xml = controlCore.getNetXml(this);
		return xml;
	}

	public Fragment getNet() {
		if (net != null) {
			return net;
		}
		net = SoftRobotNetXml.fromXml(getXml());
		return net;
	}

	@Override
	public void startThread(Runnable thread) {
		Thread t = new Thread(thread);
		startedThreads.add(t);
		t.start();
	}

	@Override
	public void suspendUpdate() {
		synchronized (suspendedNetcommToRPI) {
			suspended = true;
		}
	}

	@Override
	public void resumeUpdate() {
		synchronized (suspendedNetcommToRPI) {
			suspended = false;
			try {
				controlCore.queueNetcommToRPI(suspendedNetcommToRPI);
			} catch (RpiException e) {
				// TODO: do something reasonable - what?
				e.printStackTrace();
			}
			suspendedNetcommToRPI.clear();
		}
	}

	void addNetcommToRPI(final NetcommValue value) {
		value.addNetcommListener(new NetcommListenerAdapter() {
			@Override
			public void valueChanged(final NetcommValue value) {
				if (suspended) {
					synchronized (suspendedNetcommToRPI) {
						suspendedNetcommToRPI.put(getName(), value);
					}
				} else {
					try {
						controlCore.queueNetcommToRPI(getName(), value);
					} catch (RpiException e) {
						// TODO: do something reasonable - what?
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public boolean scheduleAfter(List<NetResult> results) throws RpiException {
		List<NetHandle> stopHandles = new ArrayList<NetHandle>();
		for (NetResult result : results) {
			stopHandles.add(result.getHandle());
		}
		return controlCore.schedule(results, stopHandles, new ArrayList<NetHandle>(),
				Arrays.asList((NetHandle) this)) != null;
	}

	@Override
	public boolean scheduleWhen(List<NetResult> results) throws RpiException {
		return controlCore.schedule(results, new ArrayList<NetHandle>(), new ArrayList<NetHandle>(),
				Arrays.asList((NetHandle) this)) != null;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setName(String netName) {
		this.name = netName;
		synchronized (this) {
			notifyAll();
		}
	}

	@Override
	public NetResult createResult(NetcommValue condition) {
		return new RccNetResult(this, condition);
	}
}
