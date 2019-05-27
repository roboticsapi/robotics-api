/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc.executor;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.runtime.controlcore.javarcc.SoftRobotJavaNetHandle;
import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.javarcc.JNet;
import org.roboticsapi.runtime.rpi.ControlCore;
import org.roboticsapi.runtime.rpi.RPIException;

public class SimpleExecutor implements Runnable {
	private ControlCore owner;
	private List<JNet> activeNets = new ArrayList<JNet>();
	private Thread thread = null;
	private Object syncObject = new Object();

	public SimpleExecutor(ControlCore owner) {
		this.owner = owner;
	}

	public void run() {
		List<JNet> toStart = new ArrayList<>();
		while (true) {
			for (JNet net : activeNets) {
				if (net == null || net.isUnloaded())
					continue;
				net.lockSensors();
				net.readSensor();
				net.unlockSensors();
				net.updateData();
				net.lockActuators();
				net.writeActuator();
				net.unlockActuators();
				net.sendNetcomm();
				net.notifyListener();

				if (net.isCompleted()) {
					RAPILogger.getLogger().fine("Completed " + net);
					List<JNet> activeNets = new ArrayList<>(this.activeNets);
					activeNets.remove(net);
					this.activeNets = activeNets;
					JNet next = net.getSuccessor();
					if (next != null) {
						toStart.add(next);
					}
				}
			}

			synchronized (syncObject) {
				if (!toStart.isEmpty()) {
					for (JNet next : toStart) {
						if (!tryStart(next)) {
							RAPILogger.getLogger().fine("Failed to start scheduled net " + next);
						}
					}
					toStart.clear();
				}
				if (activeNets.isEmpty()) {
					thread = null;
					return;
				}
			}

			try {
				JNet net = activeNets.get(0);
				long cur = net.getTime();
				if (cur == -1)
					cur = System.currentTimeMillis();
				long sleep = cur + (long) (net.getCycleTime() * 1000) - System.currentTimeMillis();
				if (sleep > 0) {
					if (timeWarn) {
						timeWarn = false;
						RAPILogger.getLogger().warning(owner + ": RPI executor caught up with time: " + sleep + " ms");
					}
					Thread.sleep(sleep);
				} else if (sleep < -500) {
					if (!timeWarn) {
						timeWarn = true;
						RAPILogger.getLogger()
								.warning(owner + ": RPI executor cannot hold up with time: " + (-sleep) + " ms");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	boolean timeWarn = false;

	public void shutdown() {
		activeNets = new ArrayList<>();
	}

	public void checkBlockEventHandlerThread() throws RPIException {
		if (Thread.currentThread() == thread)
			throw new RPIException(
					"Illegal thread access - don't use blocking RAPI calls in synchronous EventHandlers / Listeners!");
	}

	public boolean schedule(SoftRobotJavaNetHandle netHandle, SoftRobotJavaNetHandle after) {
		RAPILogger.getLogger().fine("Scheduling " + netHandle + " after " + after);
		synchronized (syncObject) {
			if (after != null)
				after.getNet().setSuccessor(netHandle.getNet());
			else
				return tryStart(netHandle.getNet());
		}
		return true;
	}

	private boolean tryStart(JNet net) {
		boolean allowed = true;
		List<JDevice> occupiedActuators = new ArrayList<>();
		for (JNet onet : activeNets)
			occupiedActuators.addAll(onet.getFragment().getActuators());
		for (JDevice device : net.getFragment().getActuators())
			allowed &= !occupiedActuators.contains(device);

		if (!allowed) {
			RAPILogger.getLogger().fine("Could not start net " + net);
			return false;
		}

		RAPILogger.getLogger().fine("Starting net " + net);
		synchronized (syncObject) {
			List<JNet> activeNets = new ArrayList<>(this.activeNets);
			activeNets.add(net);
			this.activeNets = activeNets;

			if (activeNets.size() == 1 && thread == null) {
				thread = new Thread(this, owner.toString());
				thread.setDaemon(true);
				thread.start();
			}
		}
		return true;
	}
}
