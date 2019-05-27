/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;

/**
 * A Robotics API world model object
 */
public abstract class AbstractPhysicalObject extends AbstractComposedEntity implements PhysicalObject {

	private Frame base;

	@Override
	public final Frame getBase() {
		return base;
	}

	@ConfigurationProperty(Optional = true)
	@Override
	public final void setBase(Frame base) {
		immutableWhenInitialized();
		this.base = base;
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);

		if (base == null || !base.isInitialized()) {
			base = new Frame(getName() + " Base");
			createdObjects.put("base", base);
		}
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey("base")) {
			base = null;
		}
		super.clearAutomaticConfigurationProperties(createdObjects);
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();

		base.setParent(this);
	}

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		base.setParent(null);
		super.cleanupEntities();
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();
		checkNotNullAndInitialized("base", base); // TODO @Alwin: checkNoParent
	}

}
