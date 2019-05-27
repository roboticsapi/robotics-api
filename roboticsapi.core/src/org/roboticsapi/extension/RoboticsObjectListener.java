/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.extension;

import org.roboticsapi.core.RoboticsObject;

/**
 * Listener for the event that a {@link RoboticsObject} becomes available or
 * unavailable.
 */
public interface RoboticsObjectListener extends Extension {

	/**
	 * Called when a {@link RoboticsObject} becomes available for the listener.
	 * 
	 * @param runtime the newly available object.
	 */
	void onAvailable(RoboticsObject object);

	/**
	 * Called when a {@link RoboticsObject} becomes unavailable for the listener.
	 * 
	 * @param runtime the soon unavailable object.
	 */
	void onUnavailable(RoboticsObject object);

}
