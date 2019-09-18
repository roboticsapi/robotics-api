/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.AbstractRelationship;
import org.roboticsapi.core.RelationshipChangeSet;

/**
 * A Robotics API world model relation (between two frames)
 */
public abstract class Relation extends AbstractRelationship<Frame, Frame> {

	/**
	 * Creates a new relation between two frames
	 *
	 * @param from source frame
	 * @param to   destination frame
	 */
	protected Relation(Frame from, Frame to) {
		super(from, to);
	}

	/**
	 * Returns the Frame at the other "end" of this Relation w.r.t. to the given
	 * Frame. Returns null if the given Frame is neither "from" nor "to" of this
	 * Relation.
	 *
	 * @param f the given Frame
	 * @return the Frame at the other "end" of this Relation
	 */
	public Frame getOther(Frame f) {
		if (getFrom().equals(f)) {
			return getTo();
		} else if (f == null) {
			if (getTo() == null) {
				return getFrom();
			} else {
				return null;
			}
		} else if (f.equals(getTo())) {
			return getFrom();
		} else {
			return null;
		}
	}

	@Override
	public boolean canRemove(RelationshipChangeSet situation) {
		return true;
	}

	@Override
	public boolean canEstablish(RelationshipChangeSet situation) {
		return true;
	}
}
