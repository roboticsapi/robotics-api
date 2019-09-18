/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractRoboticsEntity extends AbstractRoboticsObject implements RoboticsEntity {

	private final PropertySet properties = new PropertySet(this);

	@Override
	public final void addProperty(Property property) {
		properties.add(property);
	}

	@Override
	public final Set<Property> getProperties() {
		return Collections.unmodifiableSet(properties);
	}

	@Override
	public final <T extends Property> Set<T> getProperties(Class<T> type) {
		return Collections.unmodifiableSet(properties.subset(type));
	}

	@Override
	public final void addPropertyListener(PropertyListener l) {
		properties.addPropertyListener(l);
	}

	@Override
	public final void removePropertyListener(PropertyListener l) {
		properties.removePropertyListener(l);
	}

}