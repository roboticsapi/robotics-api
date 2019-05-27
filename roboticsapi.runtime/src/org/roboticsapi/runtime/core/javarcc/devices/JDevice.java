/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.devices;

import org.roboticsapi.runtime.javarcc.JPrimitive;

public interface JDevice {

	/**
	 * Lock access to the device (i.e. don't change internal data until
	 * {@link JDevice} is unlocked. This method is called before {@link JPrimitive}s
	 * execute their {@link JPrimitive#readSensor()} or
	 * {@link JPrimitive#writeActuator()} method.
	 */
	public void lock();

	/**
	 * Unlock access to the device (Internal data may again be updated, e.g. through
	 * communication with the physical or simulation device). This method is called
	 * after {@link JPrimitive#readSensor()} and {@link JPrimitive#writeActuator()}.
	 */
	public void unlock();

	/**
	 * Start the {@link JDevice}'s work
	 */
	public void start() throws Exception;

	/**
	 * Clean up the {@link JDevice} that should be removed from the Java RCC.
	 */
	public void destroy() throws Exception;
}
