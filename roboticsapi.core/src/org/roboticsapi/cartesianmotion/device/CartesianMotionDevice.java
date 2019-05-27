/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.device;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.world.Frame;

public interface CartesianMotionDevice extends Actuator {

	/**
	 * Gets the "fixed" reference frame, relative to which the moving frame is moved
	 * 
	 * @return the reference frame
	 */
	Frame getReferenceFrame();

	/**
	 * Gets the frame that is moved by this Cartesian actuator
	 * 
	 * @return the moving frame
	 */
	Frame getMovingFrame();

	/**
	 * Gets the default motion center.
	 * 
	 * @return the default motion center
	 */
	Frame getDefaultMotionCenter();
}
