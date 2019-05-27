/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import java.util.Map;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * The Robotics API world model world (distinguished entity object)
 */
public class World extends AbstractComposedEntity {

	/** world origin */
	private WorldOrigin origin;

	/**
	 * Creates a new world ;)
	 */
	public World() {
		super();
	}

	/**
	 * Retrieves the world origin
	 *
	 * @return world origin
	 */
	public WorldOrigin getOrigin() {
		return this.origin;
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);
		this.origin = new WorldOrigin(this, "World origin");
		createdObjects.put("origin", this.origin);
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		this.origin = null;
		createdObjects.remove("origin");
		super.clearAutomaticConfigurationProperties(createdObjects);
	}

	@Override
	protected void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();
		this.origin.setParent(this);
	}

	@Override
	protected void afterUninitialization() throws RoboticsException {
		this.origin.setParent(null);
		super.afterUninitialization();
	}

	@Override
	protected void acceptAddingChild(Entity child) throws EntityException {
		if (!(child instanceof Frame)) {
			throw new EntityException("The world only accepts frames as children!");
		}
	}

	@Override
	protected void acceptRemovingChild(Entity child) throws EntityException {
		// empty...
	}
}
