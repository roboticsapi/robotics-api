/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.simulation;

import java.util.UUID;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.world.mutable.MutableTransformation;

public abstract class SEntity extends AbstractRoboticsObject {
	private UUID identifier = UUID.randomUUID();
	private final Dependency<SWorld> world;
	private final Dependency<SEntity> parent;

	public SEntity() {
		world = createDependency("world");
		parent = createDependency("parent", (SEntity) null);
	}

	public SWorld getWorld() {
		return world.get();
	}

	@ConfigurationProperty
	public void setWorld(SWorld world) {
		this.world.set(world);
	}

	@Override
	protected void afterInitialization() {
		super.afterInitialization();
		world.get().addEntity(this);
	}

	@Override
	protected void beforeUninitialization() {
		world.get().removeEntity(this);
		super.beforeUninitialization();
	}

	private MutableTransformation globalPosition = new MutableTransformation();

	public final MutableTransformation getPosition() {
		MutableTransformation parentPosition = null;
		MutableTransformation relativePosition = getRelativePosition();
		if (parent.get() != null)
			parentPosition = parent.get().getPosition();
		if (parentPosition != null) {
			parentPosition.multiplyTo(relativePosition, globalPosition);
		} else {
			relativePosition.copyTo(globalPosition);
		}
		return globalPosition;
	}

	public abstract MutableTransformation getRelativePosition();

	public SEntity getParent() {
		return parent.get();
	}

	@ConfigurationProperty
	public void setParent(SEntity parent) {
		this.parent.set(parent);
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();
		if (this.parent.get() == this)
			throw new ConfigurationException(this, "parent", "Parent may not be the item itself");
	}

	public abstract double getSimulationHz();

	public abstract void simulateStep(Long time);

	public String getIdentifier() {
		return getWorld().getIdentifierBase() + "/" + (getName() != null ? getName() : identifier.toString());
	}
}
