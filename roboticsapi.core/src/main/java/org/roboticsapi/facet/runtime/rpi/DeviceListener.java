/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.Map;

public interface DeviceListener {

	/**
	 * Callback informing about a newly added device on a Control Core
	 *
	 * @param device     unique name of the device
	 * @param type       type of the device
	 * @param interfaces interfaces provided by the device, including their
	 *                   parameters
	 */
	void deviceAdded(String device, String type, Map<String, RpiParameters> interfaces);

	void deviceStatusChanged(String device, DeviceStatus status);

	void deviceRemoved(String device);

}
