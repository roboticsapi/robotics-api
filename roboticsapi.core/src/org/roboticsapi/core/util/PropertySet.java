/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.entity.PropertyListener;

public class PropertySet extends HashSet<Property> {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5448598673749219438L;

	private final Entity entity;
	private final List<PropertyListener> listeners = new ArrayList<PropertyListener>();

	public PropertySet(Entity entity) {
		super();
		this.entity = entity;
	}

	@Override
	public boolean add(Property property) {
		boolean ret = super.add(property);

		if (ret) {
			notifyListeners(property, entity);
		}
		return ret;
	}

	public <T extends Property> Set<T> subset(Class<T> type) {
		Set<T> subset = new HashSet<T>();

		for (Property p : this) {
			if (type.isInstance(p)) {
				subset.add(type.cast(p));
			}
		}
		return subset;
	}

	public void addPropertyListener(PropertyListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	public void removePropertyListener(PropertyListener l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	private void notifyListeners(Property property, Entity e) {
		synchronized (listeners) {
			for (PropertyListener l : this.listeners) {
				l.onPropertyAdded(e, property);
			}
		}
	}

}
