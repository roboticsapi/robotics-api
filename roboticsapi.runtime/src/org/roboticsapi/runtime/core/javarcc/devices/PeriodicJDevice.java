/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.devices;

public abstract class PeriodicJDevice extends AbstractJDevice {

	public PeriodicJDevice(double cycleTime) {
		addTask(new PeriodicTask(cycleTime) {
			@Override
			public void doPeriodicTask() {
				PeriodicJDevice.this.doPeriodicTask();
			}
		});
	}

	public abstract void doPeriodicTask();

}
