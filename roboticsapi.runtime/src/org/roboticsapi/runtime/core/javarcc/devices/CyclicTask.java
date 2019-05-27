/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.devices;

/**
 * A task that is executed cyclically (doCyclicTask will be called without
 * intentional delay.
 */
public abstract class CyclicTask {

	/**
	 * The task to execute. This method is called cyclically, without intentional
	 * delay.
	 */
	public abstract void doCyclicTask() throws InterruptedException;

}
