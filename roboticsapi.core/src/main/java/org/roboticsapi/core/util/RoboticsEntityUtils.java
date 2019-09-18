/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.ParentRelationship;
import org.roboticsapi.core.RoboticsEntity;

public class RoboticsEntityUtils {

	public static <T extends RoboticsEntity> Set<T> getChildren(RoboticsEntity entity, Class<T> type) {
		return getDescendants(entity, type, false);
	}

	public static Set<RoboticsEntity> getDescendants(RoboticsEntity entity) {
		return getDescendants(entity, RoboticsEntity.class, true);
	}

	public static <T extends RoboticsEntity> Set<T> getDescendants(RoboticsEntity entity, Class<T> type) {
		return getDescendants(entity, type, true);
	}

	protected static <T extends RoboticsEntity> Set<T> getDescendants(RoboticsEntity entity, Class<T> type,
			boolean recursively) {
		Set<T> ret = new HashSet<T>();

		for (RoboticsEntity e : getChildren(entity)) {
			if (type.isAssignableFrom(e.getClass())) {
				ret.add(type.cast(e));
			}
			if (recursively) {
				ret.addAll(getDescendants(e, type, recursively));
			}
		}
		return ret;
	}

	public static Set<RoboticsEntity> getAncestors(RoboticsEntity entity) {
		return getAncestors(entity, RoboticsEntity.class);
	}

	public static <T extends RoboticsEntity> Set<T> getAncestors(RoboticsEntity entity, Class<T> type) {
		Set<T> results = new HashSet<T>();

		while ((entity = getParent(entity)) != null) {
			if (type.isInstance(entity)) {
				T t = type.cast(entity);
				results.add(t);
			}
		}
		return results;
	}

	/**
	 * Retrieves the oldest ancestor of the specified {@link RoboticsEntity} that
	 * matches the specified type.
	 *
	 * @param entity The entity
	 * @param type   The type to match
	 * @return the ancestor or <code>null</code> if no appropriate ancestor was
	 *         found
	 */
	public static <T extends RoboticsEntity> T getAncestor(RoboticsEntity entity, Class<T> type) {
		T result = null;

		while ((entity = getParent(entity)) != null) {
			if (type.isInstance(entity)) {
				result = type.cast(entity);
			}
		}
		return result;
	}

	/**
	 * Retrieves the first ancestor of the specified {@link RoboticsEntity} that
	 * matches the specified type.
	 *
	 * @param entity The entity
	 * @return the ancestor or <code>null</code> if no appropriate ancestor was
	 *         found
	 */
	public static RoboticsEntity getParent(RoboticsEntity entity) {
		for (ParentRelationship r : entity.getRelationships(ParentRelationship.class)) {
			if (r.getChild() == entity) {
				return r.getParent();
			}
		}
		return null;
	}

	/**
	 * Retrieves all first-level children of the specified {@link RoboticsEntity}
	 * that matches the specified type.
	 * 
	 * @param entity The entity
	 * @return a list of all first-level children
	 */
	public static Set<RoboticsEntity> getChildren(RoboticsEntity entity) {
		Set<RoboticsEntity> result = new HashSet<>();
		for (ParentRelationship r : entity.getRelationships(ParentRelationship.class)) {
			if (r.getParent() == entity) {
				result.add(r.getChild());
			}
		}
		return result;
	}
}
