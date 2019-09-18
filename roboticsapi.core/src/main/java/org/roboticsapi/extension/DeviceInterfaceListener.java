/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.extension;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceInterface;

public interface DeviceInterfaceListener extends Extension {

	public abstract void onDeviceInterfaceAdded(Device device, Class<? extends DeviceInterface> type);

}