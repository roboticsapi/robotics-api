/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;

/**
 * Interface for gripping fingers.
 *
 * @see Gripper
 */
public interface Finger extends PhysicalObject {

	/**
	 * Gets the base {@link Frame} of this finger.
	 *
	 * @return the base {@link Frame}
	 */
	@Override
	public Frame getBase();

}
