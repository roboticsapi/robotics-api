/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.PropertySet;

public abstract class AbstractEntity extends AbstractRoboticsObject implements Entity {

	private ComposedEntity parent = null;
	private final PropertySet properties = new PropertySet(this);
	private final List<EntityListener> listeners = new ArrayList<EntityListener>();

	@Override
	public ComposedEntity getParent() {
		return this.parent;
	}

	@Override
	public void setParent(ComposedEntity parent) throws EntityException {
		if (this.parent != null) {
			if (parent == null) {
				ComposedEntity temp = this.parent;

				try {
					this.parent = null;
					temp.removeChild(this);
					notifyListenersOnRelationRemoved(temp, this);
				} catch (EntityException e) {
					this.parent = temp;
					throw e;
				}
			} else if (this.parent != parent) {
				throw new EntityException("Cannot set new parent before old parent is set to null!");
			}
		} else if (parent != null) {
			try {
				this.parent = parent;
				parent.addChild(this);
				notifyListenersOnRelationAdded(parent, this);
			} catch (EntityException e) {
				this.parent = null;
				throw e;
			}
		}
	}

	@Override
	public final void addEntityListener(EntityListener l) {
		listeners.add(l);
	}

	@Override
	public final void removeEntityListener(EntityListener l) {
		listeners.remove(l);
	}

	protected final void notifyListenersOnRelationAdded(ComposedEntity parent, Entity child) {
		for (EntityListener l : this.listeners) {
			l.onRelationAdded(parent, child);
		}
	}

	protected final void notifyListenersOnRelationRemoved(ComposedEntity parent, Entity child) {
		for (EntityListener l : this.listeners) {
			l.onRelationRemoved(parent, child);
		}
	}

	@Override
	public void addProperty(Property property) {
		properties.add(property);
	}

	@Override
	public Set<Property> getProperties() {
		return Collections.unmodifiableSet(properties);
	}

	@Override
	public <T extends Property> Set<T> getProperties(Class<T> type) {
		return Collections.unmodifiableSet(properties.subset(type));
	}

	public <T extends Property> boolean hasProperty(Class<T> type) {
		return !(getProperties(type).isEmpty());
	}

	public void removeProperty(Property property) {
		properties.remove(property);
	}

	public <T extends Property> void removeProperties(Class<T> type) {
		Set<T> toRemove = getProperties(type);
		properties.removeAll(toRemove);
	}

	@Override
	public void addPropertyListener(PropertyListener l) {
		properties.addPropertyListener(l);
	}

	@Override
	public void removePropertyListener(PropertyListener l) {
		properties.removePropertyListener(l);
	}
}
