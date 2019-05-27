/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;

public class EntityUtils {

	public static <T extends Entity> Set<T> getChildren(ComposedEntity entity, Class<T> type) {
		return getDescendants(entity, type, false);
	}

	public static Set<Entity> getDescendants(ComposedEntity entity) {
		return getDescendants(entity, Entity.class, true);
	}

	public static <T extends Entity> Set<T> getDescendants(ComposedEntity entity, Class<T> type) {
		return getDescendants(entity, type, true);
	}

	protected static <T extends Entity> Set<T> getDescendants(ComposedEntity entity, Class<T> type,
			boolean recursively) {
		Set<T> ret = new HashSet<T>();

		for (Entity e : entity.getChildren()) {
			if (type.isAssignableFrom(e.getClass())) {
				ret.add(type.cast(e));
			}

			if (recursively && e instanceof ComposedEntity) {
				ComposedEntity ce = (ComposedEntity) e;
				ret.addAll(getDescendants(ce, type, recursively));
			}
		}
		return ret;
	}

	public static Set<ComposedEntity> getAncestors(Entity entity) {
		return getAncestors(entity, ComposedEntity.class);
	}

	public static <T extends ComposedEntity> Set<T> getAncestors(Entity entity, Class<T> type) {
		Set<T> results = new HashSet<T>();

		while ((entity = entity.getParent()) != null) {
			if (type.isInstance(entity)) {
				T t = type.cast(entity);
				results.add(t);
			}
		}
		return results;
	}

	/**
	 * Retrieves the oldest ancestor of the specified {@link Entity} that matches
	 * the specified type.
	 * 
	 * @param entity The entity
	 * @param type   The type to match
	 * @return the ancestor or <code>null</code> if no appropriate ancestor was
	 *         found
	 */
	public static <T extends ComposedEntity> T getAncestor(Entity entity, Class<T> type) {
		T result = null;

		while ((entity = entity.getParent()) != null) {
			if (type.isInstance(entity)) {
				result = type.cast(entity);
			}
		}
		return result;
	}
}
