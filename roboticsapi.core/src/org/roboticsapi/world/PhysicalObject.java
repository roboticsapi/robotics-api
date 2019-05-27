/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.entity.ComposedEntity;

/**
 * {@link PhysicalObject} is an interface for physically existing objects (e.g.
 * robots, workpieces).
 */
public interface PhysicalObject extends ComposedEntity {

	/**
	 * Retrieves the base/reference frame for locating the physical object.
	 * 
	 * @return the reference frame
	 */
	public Frame getBase();

	/**
	 * Sets the base/reference frame where this physical object is located at.
	 * 
	 * @param base the reference frame
	 */
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base);

}