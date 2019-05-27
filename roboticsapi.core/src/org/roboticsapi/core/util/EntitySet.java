/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;

public class EntitySet {

	public interface Verifier {

		public void acceptAdding(Entity child) throws EntityException;

		public void acceptRemoving(Entity child) throws EntityException;

	}

	private final Set<Entity> children = new HashSet<Entity>();
	private final ComposedEntity parent;
	private final Verifier verifier;

	public EntitySet(ComposedEntity parent) {
		this(parent, null);
	}

	public EntitySet(ComposedEntity parent, Verifier filter) {
		this.parent = parent;
		this.verifier = filter;
	}

	public void add(Entity child) throws EntityException {
		if (child.getParent() != parent) {
			child.setParent(parent);
			// TODO: is this return statement correct here? Seems like
			// acceptAddingChild is not called, is that okay?
			return;
		}
		acceptAddingChild(child);
		children.add(child);
	}

	public boolean canAdd(Entity child) {
		try {
			acceptAddingChild(child);
			return true;
		} catch (EntityException e) {
			return false;
		}
	}

	protected void acceptAddingChild(Entity child) throws EntityException {
		if (this.verifier != null) {
			this.verifier.acceptAdding(child);
		}
	}

	public void remove(Entity child) throws EntityException {
		if (child.getParent() != null) {
			if (child.getParent() != parent) {
				throw new IllegalArgumentException("Given child entity belongs to another parent!");
			}
			child.setParent(null);
			return;
		}
		acceptRemovingChild(child);
		children.remove(child);
	}

	public boolean canRemove(Entity child) {
		try {
			acceptRemovingChild(child);
			return true;
		} catch (EntityException e) {
			return false;
		}
	}

	protected void acceptRemovingChild(Entity child) throws EntityException {
		if (this.verifier != null) {
			this.verifier.acceptRemoving(child);
		}
	}

	public Set<Entity> getChildren() {
		return Collections.unmodifiableSet(this.children);
	}

	public Set<Entity> getDescendants() {
		return Collections.unmodifiableSet(EntityUtils.getDescendants(parent, Entity.class));
	}

	public <T extends Entity> Set<T> getChildren(Class<T> type) {
		return Collections.unmodifiableSet(EntityUtils.getChildren(parent, type));
	}

	public <T extends Entity> Set<T> getDescendants(Class<T> type) {
		return Collections.unmodifiableSet(EntityUtils.getDescendants(parent, type));
	}

	public boolean contains(Object o) {
		return children.contains(o);
	}

}
