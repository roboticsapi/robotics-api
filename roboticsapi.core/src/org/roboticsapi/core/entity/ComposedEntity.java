/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.entity;

import java.util.Set;

import org.roboticsapi.core.exception.EntityException;

public interface ComposedEntity extends Entity {

	/**
	 * Retrieves the entity's children.
	 * 
	 * @return the entity's children
	 */
	public Set<Entity> getChildren();

	public void addChild(Entity child) throws EntityException;

	public boolean canAddChild(Entity child);

	public void removeChild(Entity child) throws EntityException;

	public boolean canRemoveChild(Entity child);

}
