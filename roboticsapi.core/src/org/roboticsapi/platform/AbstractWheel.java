/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Frame;

/**
 * Abstract implementation for a {@link Wheel}.
 */
public abstract class AbstractWheel extends AbstractComposedEntity implements Wheel {

	private Frame baseFrame;

	/**
	 * Constructor.
	 */
	protected AbstractWheel() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param name the wheel's name
	 */
	protected AbstractWheel(String name) {
		this();
		setName(name);
	}

	@Override
	public Frame getBase() {
		return this.baseFrame;
	}

	@Override
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.baseFrame = base;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getName() + "'";
	}

	@Override
	public Platform getParent() {
		return (Platform) super.getParent();
	}

	@Override
	public void setParent(ComposedEntity parent) throws EntityException {
		if (parent != null && !(parent instanceof Platform)) {
			throw new EntityException("The wheel's parent needs to be a platform!");
		}
		super.setParent(parent);
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);

		if (baseFrame == null) {
			createdObjects.put("base", baseFrame = new Frame(getName() + " Base"));
		}
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey("base")) {
			baseFrame = null;
		}
		super.clearAutomaticConfigurationProperties(createdObjects);
	}

	@Override
	protected final void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();

		baseFrame.setParent(this);
	}

	@Override
	protected final void cleanupEntities() throws EntityException, InitializationException {
		super.cleanupEntities();

		baseFrame.setParent(null);
	}

}
