/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

public abstract class AbstractRelationship<F extends RoboticsObject, T extends RoboticsObject> implements Relationship {

	private final F from;
	private final T to;

	public AbstractRelationship(F from, T to) {
		if (from == null) {
			throw new IllegalArgumentException("From may not be null.");
		}
		if (to == null) {
			throw new IllegalArgumentException("To may not be null.");
		}
		this.from = from;
		this.to = to;
	}

	@Override
	public final F getFrom() {
		return from;
	}

	@Override
	public final T getTo() {
		return to;
	}

	@Override
	public final RoboticsObject getOther(RoboticsObject other) throws IllegalArgumentException {
		if (other == this.from) {
			return this.to;
		}
		if (other == this.to) {
			return this.from;
		}
		throw new IllegalArgumentException("Given object is neither source or target.");
	}

	@Override
	public void establish() {
		if (!canEstablish(RelationshipChangeSet.getUnmodified())) {
			throw new IllegalStateException();
		}
		((AbstractRoboticsObject) from).addRelationship(this);
		((AbstractRoboticsObject) to).addRelationship(this);
		((AbstractRoboticsObject) from).notifyRelationshipAdded(this);
		((AbstractRoboticsObject) to).notifyRelationshipAdded(this);
	}

	@Override
	public void remove() {
		if (!canRemove(RelationshipChangeSet.getUnmodified())) {
			throw new IllegalStateException();
		}
		((AbstractRoboticsObject) from).removeRelationship(this);
		((AbstractRoboticsObject) to).removeRelationship(this);
		((AbstractRoboticsObject) from).notifyRelationshipRemoved(this);
		((AbstractRoboticsObject) to).notifyRelationshipRemoved(this);
	}

	@Override
	public abstract boolean canEstablish(RelationshipChangeSet situation);

	@Override
	public abstract boolean canRemove(RelationshipChangeSet situation);

	@Override
	public String toString() {
		return getClass().getSimpleName() + " from " + getFrom() + " to " + getTo();
	}

}
