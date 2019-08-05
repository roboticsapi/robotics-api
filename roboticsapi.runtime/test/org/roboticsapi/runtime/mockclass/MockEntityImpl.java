/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.EntityListener;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.entity.PropertyListener;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.PropertySet;

public class MockEntityImpl extends AbstractRoboticsObject implements Entity {
	private ComposedEntity parent = null;
	private final List<EntityListener> entityListeners = new ArrayList<EntityListener>();
	private final PropertySet properties = new PropertySet(this);

	@Override
	public ComposedEntity getParent() {
		return parent;
	}

	@Override
	public void setParent(ComposedEntity parent) throws EntityException {
		this.parent = parent;
	}

	@Override
	public void addEntityListener(EntityListener l) {
		entityListeners.add(l);
	}

	@Override
	public void removeEntityListener(EntityListener l) {
		entityListeners.remove(l);
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

	@Override
	public void addPropertyListener(PropertyListener l) {
		properties.addPropertyListener(l);
	}

	@Override
	public void removePropertyListener(PropertyListener l) {
		properties.removePropertyListener(l);
	}

	@Override
	public <T extends Property> boolean hasProperty(Class<T> type) {
		return !(getProperties(type).isEmpty());
	}

	@Override
	public void removeProperty(Property property) {
		properties.remove(property);
	}

	@Override
	public <T extends Property> void removeProperties(Class<T> type) {
		Set<T> toRemove = getProperties(type);
		properties.removeAll(toRemove);
	}
}
