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
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

public abstract class AbstractToolChanger extends AbstractComposedEntity implements ToolChanger {

	private Frame baseFrame, mountFrame, adapterFrame;
	private StaticConnection mountConnection, adapterConnection;

	public AbstractToolChanger() {
		super();
	}

	@Override
	public final Frame getBase() {
		return this.baseFrame;
	}

	@Override
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.baseFrame = base;
	}

	@Override
	public final Frame getAdapterFrame() {
		return this.adapterFrame;
	}

	@ConfigurationProperty
	public final void setAdapterFrame(Frame adapterFrame) {
		immutableWhenInitialized();
		this.adapterFrame = adapterFrame;
	}

	@Override
	public final Frame getMountFrame() {
		return this.mountFrame;
	}

	@ConfigurationProperty
	public final void setMountFrame(Frame mountFrame) {
		immutableWhenInitialized();
		this.mountFrame = mountFrame;
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

		checkNotNullAndInitialized("mountFrame", this.mountFrame);
		checkNotNullAndInitialized("adapterFrame", this.adapterFrame);

		validateToolChangerProperties();
	}

	protected void validateToolChangerProperties() throws ConfigurationException {

	}

	@Override
	protected final void setupEntities() throws EntityException, InitializationException {
		baseFrame.setParent(this);

		Transformation transformation = getBase2MountTransformation();
		mountConnection = new StaticConnection(transformation);
		mountConnection.setParent(this);
		mountFrame.addRelation(mountConnection, baseFrame);

		transformation = getBase2AdapterTransformation();
		adapterConnection = new StaticConnection(transformation);
		adapterConnection.setParent(this);
		baseFrame.addRelation(adapterConnection, adapterFrame);
	}

	protected abstract Transformation getBase2MountTransformation();

	protected abstract Transformation getBase2AdapterTransformation();

	@Override
	protected final void cleanupEntities() throws EntityException, InitializationException {
		adapterConnection.setParent(null);
		baseFrame.removeRelation(adapterConnection);
		adapterConnection = null;

		mountConnection.setParent(null);
		mountFrame.removeRelation(mountConnection);
		mountConnection = null;

		baseFrame.setParent(null);
	}
}
