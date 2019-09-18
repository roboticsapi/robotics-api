/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.RoboticsEntity;

/**
 * {@link PhysicalObject} is an interface for physically existing objects (e.g.
 * robots, workpieces).
 */
public interface PhysicalObject extends RoboticsEntity {

	/**
	 * Retrieves the base/reference frame for locating the physical object.
	 *
	 * @return the reference frame
	 */
	public Frame getBase();

}