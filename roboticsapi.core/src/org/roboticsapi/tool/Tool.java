/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;

/**
 * A tool device.
 */
public interface Tool extends Actuator, PhysicalObject {

	/**
	 * Gets the base {@link Frame} of this tool.
	 * 
	 * @return the base {@link Frame}
	 */
	public Frame getBase();

	/**
	 * Gets the effector {@link Frame} of this tool.
	 * 
	 * @return the effector {@link Frame}
	 */
	public Frame getEffectorFrame();

//	/**
//	 * Gets the weight of this tool.
//	 * 
//	 * @return the weight [kg].
//	 */	
//	public double getWeight();

}
