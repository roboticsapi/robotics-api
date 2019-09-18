/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.javarcc.interfaces;

import org.roboticsapi.facet.javarcc.devices.JDevice;

public interface JRobotBaseInterface extends JDevice {

	int getWheelCount();

	double getWheelPosition(int i);

	double getWheelVelocity(int i);

}
