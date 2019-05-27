/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.devices;

/**
 * A task that is executed periodically (doPeriodicTask will be called with the
 * given cycle time).
 */
public abstract class PeriodicTask extends CyclicTask {

	private double cycleTime;
	Long nextTime = null;

	public PeriodicTask(double cycleTime) {
		this.cycleTime = cycleTime;
	}

	@Override
	public final void doCyclicTask() throws InterruptedException {
		doPeriodicTask();

		if (nextTime == null)
			nextTime = System.nanoTime();
		nextTime += (long) (1000000000L * cycleTime);
		long dur = nextTime - System.nanoTime();
		if (dur > 0)
			Thread.sleep(dur / 1000000L, (int) (dur % 1000000));
	}

	/**
	 * The task to execute. This method is called periodically, with the given cycle
	 * time.
	 */
	public abstract void doPeriodicTask();

}
