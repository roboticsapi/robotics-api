/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

public interface NetcommPrimitive {
	/**
	 * Retrieves the Netcomm value transferred to RPI
	 * 
	 * @return Netcomm value
	 */
	public NetcommValue getNetcommToRPI();

	/**
	 * Retrieves the Netcomm value received from RPI
	 * 
	 * @return Netcomm value
	 */
	public NetcommValue getNetcommFromRPI();

	/**
	 * Retrieves the Netcomm key for this primitive
	 * 
	 * @return Key of the netcomm (must be unique in the net)
	 */
	public String getNetcommKey();

	/**
	 * Sets the Netcomm key for this primitive
	 * 
	 * @return Key of the netcomm (must be unique in the net)
	 */
	public void setNetcommKey(String key);

}
