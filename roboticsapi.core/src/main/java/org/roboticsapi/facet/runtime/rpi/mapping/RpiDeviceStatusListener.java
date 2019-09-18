/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.Map;

import org.roboticsapi.facet.runtime.rpi.RpiParameters;

public interface RpiDeviceStatusListener {
	public void isOperational();

	public void isSafeOperational();

	public void isOffline();

	public void isPresent(String deviceType, Map<String, RpiParameters> interfaces);

	public void isAbsent();

}
