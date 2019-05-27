/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.EntityUtils;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

public abstract class AbstractToolAdapter extends AbstractComposedEntity implements ToolAdapter {

	private Frame baseFrame, toolFrame;
	private StaticConnection connection;

	public AbstractToolAdapter() {
		super();
	}

	@Override
	public final Frame getBase() {
		return this.baseFrame;
	}

	@Override
	public final void setBase(Frame base) {
		immutableWhenInitialized();
		this.baseFrame = base;
	}

	@Override
	public final Frame getToolFrame() {
		return this.toolFrame;
	}

	@ConfigurationProperty
	public final void setToolFrame(Frame toolFrame) {
		immutableWhenInitialized();
		this.toolFrame = toolFrame;
	}

	@Override
	public final Tool getTool() {
		if (toolFrame != null) {
			return EntityUtils.getAncestor(toolFrame, Tool.class);
		}
		return null;
	}

	@Override
	public ToolChanger getToolChanger() {
		// TODO Implement
		return null;
	}

	@Override
	protected final void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (baseFrame == null || !baseFrame.isInitialized()) {
			createdObjects.put("base", baseFrame = new Frame(getName() + " Base"));
		}
	}

	@Override
	protected final void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey("base")) {
			baseFrame = null;
		}
	}

	@Override
	protected final void validateConfigurationProperties() throws ConfigurationException {
		checkNoParent("base", baseFrame);
		checkNotNullAndInitialized("toolFrame", toolFrame);

		validateToolAdapterProperties();
	}

	protected void validateToolAdapterProperties() throws ConfigurationException {

	}

	@Override
	protected final void setupEntities() throws EntityException, InitializationException {
		baseFrame.setParent(this);

		Transformation transformation = getBase2ToolTransformation();
		connection = new StaticConnection(transformation);
		connection.setParent(this);
		baseFrame.addRelation(connection, toolFrame);
	}

	protected abstract Transformation getBase2ToolTransformation();

	@Override
	protected final void cleanupEntities() throws EntityException, InitializationException {
		connection.setParent(null);
		baseFrame.removeRelation(connection);
		connection = null;

		baseFrame.setParent(null);
	}

}
