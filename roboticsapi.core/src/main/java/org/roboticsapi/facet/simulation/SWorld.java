/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.util.RAPILogger;

public class SWorld extends AbstractRoboticsObject implements Runnable {
	private static final long TIMESTEP = 10;

	private final Dependency<String> identifierBase;
	private List<SEntity> entities = new ArrayList<SEntity>();
	private Map<SEntity, Long> times = new HashMap<SEntity, Long>();

	private Thread simThread = null;

	public SWorld() {
		identifierBase = createDependency("identifierBase", "sim://"); // "rmi://127.0.0.1:1199/");
	}

	@Override
	protected void afterInitialization() {
		super.afterInitialization();
		simThread = new Thread(this, "SWorld " + getName());
		simThread.setDaemon(true);
		simThread.start();
	}

	@Override
	protected void afterUninitialization() {
		super.afterUninitialization();
		simThread = null;
	}

	public void addEntity(SEntity entity) {
		times.put(entity, System.currentTimeMillis());
		entities.add(entity);
	}

	public void removeEntity(SEntity entity) {
		entities.remove(entity);
		times.remove(entity);
	}

	@Override
	public void run() {
		Long start = System.currentTimeMillis();
		int cycle = 0;
		while (true) {
			if (Thread.currentThread() != simThread)
				return;
			cycle++;
			long goal = (long) (start + TIMESTEP * cycle);

			for (SEntity entity : new ArrayList<SEntity>(entities)) {
				try {
					Long last = times.get(entity);
					if (last == null)
						continue;
					double dt = 1000 / entity.getSimulationHz();
					if (dt < 1)
						dt = 1;
					while (last < goal) {
						last += (int) dt;
						entity.simulateStep(last);
					}
					times.put(entity, last);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Long now = System.currentTimeMillis();
				if (goal - now > 0) {
					if (timeWarn) {
						timeWarn = false;
						RAPILogger.getLogger(this)
								.warning(getName() + ": Simulation caught up with time: " + (goal - now) + " ms");
					}
					Thread.sleep(goal - now);
				} else if (goal - now < -500) {
					if (!timeWarn) {
						timeWarn = true;
						RAPILogger.getLogger(this)
								.warning(getName() + ": Simulation cannot hold up with time: " + (now - goal) + " ms");
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	boolean timeWarn = false;

	public List<SEntity> getEntities() {
		return entities;
	}

	@ConfigurationProperty
	public void setIdentifierBase(String identifierBase) {
		this.identifierBase.set(identifierBase);
	}

	public String getIdentifierBase() {
		return identifierBase.get();
	}
}
