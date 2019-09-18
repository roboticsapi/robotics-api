/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

/**
 * A Robotics API world model {@link Relation} that describes the logical
 * relationship between two {@link Frame}s.
 */
public abstract class LogicalRelation extends Relation {

	protected LogicalRelation(Frame from, Frame to) {
		super(from, to);
	}

	/**
	 * Checks if the relation is variable, i.e. has a transformation that can change
	 * while initialized
	 *
	 * @return true if the relation is variable
	 */
	public abstract boolean isVariable();

	/**
	 * Checks if the relation is persistent, i.e. is not usually removed in the
	 * application
	 *
	 * @return true if the relation is persistent (e.g. StaticConnection)
	 */
	public abstract boolean isPersistent();

}
