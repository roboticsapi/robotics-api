/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.Set;

import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.EntitySet;

public class MockComposedEntityImpl extends MockEntityImpl implements ComposedEntity {
	private final EntitySet children = new EntitySet(this);

	@Override
	public Set<Entity> getChildren() {
		return children.getChildren();
	}

	@Override
	public void addChild(Entity child) throws EntityException {
		if (canAddChild(child)) {
			children.add(child);
		}
	}

	@Override
	public boolean canAddChild(Entity child) {
		return true;
	}

	@Override
	public void removeChild(Entity child) throws EntityException {
		if (canRemoveChild(child)) {
			children.remove(child);
		}
	}

	@Override
	public boolean canRemoveChild(Entity child) {
		return true;
	}
}
