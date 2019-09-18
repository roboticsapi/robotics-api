/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.javarcc.JNet;
import org.roboticsapi.facet.javarcc.JSchedulingRule;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.ControlCore;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public class SimpleExecutor implements Runnable {
	private ControlCore owner;
	private List<JNet> activeNets = new ArrayList<JNet>();
	private List<JSchedulingRule> rules = new ArrayList<JSchedulingRule>();
	private Thread thread = null;
	private Object syncObject = new Object();
	private Executor notifyExecutor;

	public SimpleExecutor(ControlCore owner, Executor notifyExecutor) {
		this.owner = owner;
		this.notifyExecutor = notifyExecutor;
	}

	public void addRule(JSchedulingRule rule) {
		RAPILogger.getLogger(this).fine("Adding rule " + rule);
		synchronized (syncObject) {
			List<JSchedulingRule> rules = new ArrayList<>(this.rules);
			rules.add(rule);
			this.rules = rules;
			if (activeNets.isEmpty() && thread == null) {
				checkRules();
				if (!activeNets.isEmpty()) {
					thread = new Thread(this, owner.toString());
					thread.setDaemon(true);
					thread.start();
				}
			}
		}
	}

	public void run() {
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
					RAPILogger.getLogger(this).fine("Completed " + net);
					List<JNet> activeNets = new ArrayList<>(this.activeNets);
					activeNets.remove(net);
					this.activeNets = activeNets;
				} else if (net.isKilled()) {
					RAPILogger.getLogger(this).fine("Aborted " + net);
					List<JNet> activeNets = new ArrayList<>(this.activeNets);
					activeNets.remove(net);
					this.activeNets = activeNets;
				}
			}

			synchronized (syncObject) {
				checkRules();
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
						RAPILogger.getLogger(this)
								.warning(owner + ": RPI executor caught up with time: " + sleep + " ms");
					}
					Thread.sleep(sleep);
				} else if (sleep < -500) {
					if (!timeWarn) {
						timeWarn = true;
						RAPILogger.getLogger(this)
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

	private void checkRules() {
		for (JSchedulingRule rule : rules) {
			if (rule == null)
				continue;
			Boolean cond = rule.getCondition().isTrue();
			if (cond == null)
				continue;
			if (cond) {

				boolean allowed = true;
				List<JDevice> occupiedActuators = new ArrayList<>();
				for (JNet net : activeNets)
					if (!rule.getKillNets().contains(net))
						occupiedActuators.addAll(net.getFragment().getActuators());
				for (JNet net : rule.getStartNets())
					for (JDevice device : net.getFragment().getActuators())
						allowed &= !occupiedActuators.contains(device);

				if (!allowed) {
					RAPILogger.getLogger(this).fine("Could not fire rule " + rule);
					List<JSchedulingRule> rules = new ArrayList<>(this.rules);
					rules.remove(rule);
					this.rules = rules;
					notifyExecutor.execute(rule::markFailed);
					continue;
				}

				RAPILogger.getLogger(this).fine("Firing rule " + rule);

				if (!rule.getKillNets().isEmpty()) {
					RAPILogger.getLogger(this).fine("Killing " + Arrays.toString(rule.getKillNets().toArray()));
					for (JNet net : rule.getKillNets()) {
						if (!net.isUnloaded())
							net.kill();
					}
				}
				if (!rule.getStartNets().isEmpty()) {
					RAPILogger.getLogger(this).fine("Starting " + Arrays.toString(rule.getStartNets().toArray()));
				}
				List<JNet> activeNets = new ArrayList<>(this.activeNets);
				activeNets.removeAll(rule.getKillNets());
				activeNets.addAll(rule.getStartNets());
				this.activeNets = activeNets;

				if (!rule.getCancelNets().isEmpty()) {
					RAPILogger.getLogger(this).fine("Cancelling " + Arrays.toString(rule.getCancelNets().toArray()));
					for (JNet net : rule.getCancelNets()) {
						if (!net.isUnloaded())
							net.cancel();
					}
				}
				List<JSchedulingRule> rules = new ArrayList<>(this.rules);
				rules.remove(rule);
				this.rules = rules;
				notifyExecutor.execute(rule::markFired);
				continue;
			}
			if (rule.getCondition().isFinal()) {
				List<JSchedulingRule> rules = new ArrayList<>(this.rules);
				rules.remove(rule);
				this.rules = rules;
				notifyExecutor.execute(rule::markInactive);
			}
		}
	}

	public void shutdown() {
		rules = new ArrayList<>();
		activeNets = new ArrayList<>();
	}

	public void checkBlockEventHandlerThread() throws RpiException {
		if (Thread.currentThread() == thread)
			throw new RpiException(
					"Illegal thread access - don't use blocking RAPI calls in synchronous EventHandlers / Listeners!");
	}
}
