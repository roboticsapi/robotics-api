/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.realtimercc;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.roboticsapi.runtime.controlcore.SoftRobotNetHandle;
import org.roboticsapi.runtime.controlcore.SoftRobotNetXml;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.rpi.NetStatus;
import org.roboticsapi.runtime.rpi.NetStatusListener;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotWebsocketNetHandle implements SoftRobotNetHandle {
	protected boolean suspended = false;
	private final Hashtable<String, NetcommValue> suspendedNetcommToRPI = new Hashtable<String, NetcommValue>();
	private Fragment net = null;
	private String xml = null;
	final private String name;
	private final List<NetStatusListener> statusListeners = new Vector<NetStatusListener>();
	private NetStatus resultStatus = null;
	private NetStatus status = null;
	private RPIException resultException = null;
	private final SoftRobotWebsocketControlCore controlCore;
	private final List<Thread> startedThreads = new Vector<Thread>();

	protected void setResultStatus(NetStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	protected void setStatusException(RPIException resultException) {
		this.resultException = resultException;
	}

	/**
	 * Creates a new SoftRobot net
	 *
	 * @param controlCore
	 *
	 * @param uri         URI of the net
	 */
	public SoftRobotWebsocketNetHandle(SoftRobotWebsocketControlCore controlCore, final String name) {
		this.controlCore = controlCore;
		this.name = name;
	}

	@Override
	public boolean start() throws RPIException {
		return controlCore.startNet(this);
	}

	@Override
	public boolean abort() throws RPIException {
		return controlCore.abortNet(this);
	}

	@Override
	public boolean cancel() throws RPIException {
		return controlCore.cancelNet(this);
	}

	@Override
	public void unload() throws RPIException {
		if (resultStatus == null) {
			controlCore.unloadNet(this);
			xml = null;
			net = null;
		}
	}

	@Override
	public void waitComplete() throws RPIException {
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
			throw new RPIException("Interrupted");
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
	public NetStatus getStatus() throws RPIException {
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
			if (status == NetStatus.RUNNING
					&& !(this.status == NetStatus.SCHEDULED || this.status == NetStatus.READY)) {
				return;
			}

			this.status = status;
			synchronized (statusListeners) {
				for (final NetStatusListener l : statusListeners) {
					controlCore.queueNotify(new Runnable() {
						@Override
						public void run() {
							try {
								l.statusChanged(status);
							} catch (Exception ex) {
								setStatusException(new RPIException("Error in net status listener", ex));
							}
						};
					});
				}
			}
		}
	}

	@Override
	public void addStatusListener(final NetStatusListener listener) throws RPIException {
		synchronized (statusListeners) {
			statusListeners.add(listener);
		}
		listener.statusChanged(getStatus());
	}

	@Override
	public boolean scheduleAfter(NetHandle previousNet) throws RPIException {
		return controlCore.scheduleNet(this, previousNet);
	}

	@Override
	public String getName() {
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
			} catch (RPIException e) {
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
					} catch (RPIException e) {
						// TODO: do something reasonable - what?
						e.printStackTrace();
					}
				}
			}
		});
	}
	// @Override
	// public void startThread(Runnable thread) {
	// controlCore.startThread(thread);
	// }
}
