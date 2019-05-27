/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

/**
 * A Robotics API world model connection (between two frames)
 */
public abstract class Connection extends Relation {

	/**
	 * Creates a new deletable connection
	 * 
	 * @param transformation geometric relation between the frames
	 */
	protected Connection() {
		super();
	}

	/**
	 * Creates a new connection
	 * 
	 * @param transformation geometric relation between the frames
	 * @param deletable      whether this relation is deletable
	 */
	// protected Connection(boolean deletable) {
	// super();
	// }
}
