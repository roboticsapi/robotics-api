/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

/**
 * Abstract implementation for {@link GrippingFinger}s.
 */
public abstract class AbstractGrippingFinger extends AbstractComposedEntity implements GrippingFinger {

	private Frame baseFrame, tipFrame;
	private StaticConnection connection;

	@Override
	public final Frame getBase() {
		return baseFrame;
	}

	@Override
	@ConfigurationProperty(Optional = true)
	public final void setBase(Frame base) {
		immutableWhenInitialized();
		this.baseFrame = base;
	}

	@Override
	public final Frame getTipFrame() {
		return tipFrame;
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);

		if (baseFrame == null) {
			createdObjects.put("base", baseFrame = new Frame(getName() + " Base"));
		}
		if (tipFrame == null) {
			createdObjects.put("tipFrame", tipFrame = new Frame(getName() + " Tip"));
		}
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey("base")) {
			baseFrame = null;
		}
		if (createdObjects.containsKey("tipFrame")) {
			tipFrame = null;
		}
		super.clearAutomaticConfigurationProperties(createdObjects);
	}

	@Override
	protected final void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();

		baseFrame.setParent(this);
		tipFrame.setParent(this);

		Transformation transformation = new Transformation(getOffset(), 0, getLength(), 0, 0, 0);
		connection = new StaticConnection(transformation);
		connection.setParent(this);

		baseFrame.addRelation(connection, tipFrame);
	}

	@Override
	protected final void cleanupEntities() throws EntityException, InitializationException {
		super.cleanupEntities();

		uninitialize(connection);
		connection.setParent(null);

		baseFrame.setParent(null);
		tipFrame.setParent(null);
	}

}
