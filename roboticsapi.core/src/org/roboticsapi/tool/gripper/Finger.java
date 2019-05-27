/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;

/**
 * Interface for gripping fingers.
 * 
 * @see Gripper
 */
public interface Finger extends RoboticsObject, PhysicalObject {

	/**
	 * Gets the base {@link Frame} of this finger.
	 * 
	 * @return the base {@link Frame}
	 */
	@Override
	Frame getBase();

}
