/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Set;

public interface RoboticsEntity extends RoboticsObject {

	/**
	 * Adds a property for this <code>PhysicalObject</code>.
	 *
	 * @param property property for this device
	 */
	public abstract void addProperty(Property property);

	/**
	 * Gets the properties of this <code>PhysicalObject</code>.
	 *
	 * @return a set with all properties
	 */
	public abstract Set<Property> getProperties();

	/**
	 * Get all properties of the given type.
	 *
	 * @param type The property type
	 * @return a set with all found properties of the given type.
	 */
	public abstract <T extends Property> Set<T> getProperties(Class<T> type);

	/**
	 * Adds an {@link PropertyListener}.
	 *
	 * @param l the listener.
	 */
	public void addPropertyListener(PropertyListener l);

	/**
	 * Removes an {@link PropertyListener}.
	 *
	 * @param l the listener.
	 */
	public void removePropertyListener(PropertyListener l);

}
