/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.extension;

import org.roboticsapi.core.RoboticsObject;

/**
 * Interface for extensions providing device implementations.
 */
public interface RoboticsObjectBuilder extends Extension {

	/**
	 * Returns true if a robotics object of the given type can be build by this
	 * robotics builder. This method must return true at least for all names
	 * provided by {@link RoboticsObjectBuilder#getProvidedTypes()}
	 * 
	 * @param type the type
	 * @return true if a robotics object can be build
	 */
	public boolean canBuild(String type);

	/**
	 * Instantiates a robotics object of the given type. This method must return
	 * valid robotics objects at least for all types where
	 * {@link RoboticsObjectBuilder#canBuild(String)} returns true.
	 * 
	 * @param type the type
	 * @return the robotics object
	 */
	public RoboticsObject build(String type);

	/**
	 * Returns a set of provided types. For all types,
	 * {@link RoboticsObjectBuilder#canBuild(String)} returns true and
	 * {@link RoboticsObjectBuilder#build(String)} returns a valid robotics object.
	 * 
	 * @return a set of provided types
	 */
	public String[] getProvidedTypes();

}
