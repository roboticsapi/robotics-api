/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.javarcc.interfaces;

import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPITwist;

public interface JCartesianPositionInterface extends JDevice {

	RPIFrame getMeasuredPosition();

	RPITwist getMeasuredVelocity();

	RPITwist getCommandedVelocity();

	RPIFrame getCommandedPosition();

	int getCartesianPositionDeviceError();

	int checkPosition(RPIFrame pos);

	void setPosition(RPIFrame pos, Long time);

}
