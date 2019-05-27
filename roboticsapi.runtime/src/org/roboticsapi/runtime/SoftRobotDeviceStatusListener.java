/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import java.util.List;

public interface SoftRobotDeviceStatusListener {
	public void isOperational();

	public void isSafeOperational();

	public void isOffline();

	public void isPresent(String deviceType, List<String> interfaces);

	public void isAbsent();

}
