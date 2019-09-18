/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.HashSet;
import java.util.Set;

public final class ParentRelationship extends AbstractRelationship<RoboticsEntity, RoboticsEntity> {

	public ParentRelationship(RoboticsEntity parent, RoboticsEntity child) {
		super(parent, child);
	}

	public final RoboticsEntity getParent() {
		return super.getFrom();
	}

	public final RoboticsEntity getChild() {
		return super.getTo();
	}

	@Override
	public final boolean canEstablish(RelationshipChangeSet situation) {
		return !getParent().isInitialized() && !hasAnyParent(getChild(), situation)
				&& !hasAnyTransitiveHierarchicalRelationship(getChild(), getParent(), situation);
	}

	@Override
	public final boolean canRemove(RelationshipChangeSet situation) {
		return !getParent().isInitialized();
	}

	private final static boolean hasAnyParent(RoboticsEntity child, RelationshipChangeSet situation) {
		for (ParentRelationship r : situation.getRelationships(child, ParentRelationship.class)) {
			if (r.getChild() == child) {
				return true;
			}
		}
		return false;
	}

	private final static boolean isHierarchicalChild(RoboticsEntity child, RoboticsEntity parent,
			RelationshipChangeSet situation) {
		return isHierarchicalChild(child, parent, situation, new HashSet<>());
	}

	private final static boolean isHierarchicalChild(RoboticsObject child, RoboticsEntity parent,
			RelationshipChangeSet situation, Set<RoboticsObject> visited) {
		visited.add(child);
		for (ParentRelationship r : situation.getRelationships(child, ParentRelationship.class)) {
			RoboticsEntity p = r.getParent();
			if (visited.contains(p)) {
				continue;
			}
			if (p == parent || isHierarchicalChild(p, parent, situation, visited)) {
				return true;
			}
		}
		return false;
	}

	private final static boolean hasAnyTransitiveHierarchicalRelationship(RoboticsEntity first, RoboticsEntity second,
			RelationshipChangeSet situation) {
		return first == second || isHierarchicalChild(first, second, situation)
				|| isHierarchicalChild(second, first, situation);
	}

}
