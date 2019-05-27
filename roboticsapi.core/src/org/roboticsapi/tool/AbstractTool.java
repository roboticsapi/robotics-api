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
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;

/**
 * An abstract implementation for tools.
 */
public abstract class AbstractTool<D extends ToolDriver> extends AbstractActuator<D> implements Tool {

	private Frame base;
	private Frame effectorFrame;

	/**
	 * Creates a new tool.
	 */
	public AbstractTool() {
		super();
	}

	/**
	 * Gets the base {@link Frame} of this tool.
	 * 
	 * @return the base {@link Frame}
	 */
	@Override
	public Frame getBase() {
		return this.base;
	}

	@ConfigurationProperty(Optional = true)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.base = base;
	}

	/**
	 * Gets the effector {@link Frame} of this tool.
	 * 
	 * @return the effector {@link Frame}
	 */
	@Override
	public Frame getEffectorFrame() {
		return this.effectorFrame;
	}

	@ConfigurationProperty(Optional = true)
	public void setEffectorFrame(Frame effectorFrame) {
		immutableWhenInitialized();
		this.effectorFrame = effectorFrame;
	}

	@Override
	protected final void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		base = fill("base", base, new Frame(getName() + " Base Frame"), createdObjects);
		effectorFrame = fill("effectorFrame", effectorFrame, new Frame(getName() + " Effector Frame"), createdObjects);

		fillAutomaticToolProperties(createdObjects);
	}

	/**
	 * Fills automatic configuration properties of derived {@link Tool}s.
	 * 
	 * @param createdObjects map with all created objects.
	 * 
	 * @see #fillAutomaticConfigurationProperties(Map)
	 */
	protected void fillAutomaticToolProperties(Map<String, RoboticsObject> createdObjects) {
	}

	@Override
	protected final void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		clearAutomaticToolProperties(createdObjects);

		base = clear("base", base, createdObjects);
		effectorFrame = clear("effectorFrame", effectorFrame, createdObjects);
	}

	/**
	 * Clears automatic configuration properties of derived {@link Tool}s.
	 * 
	 * @param createdObjects map with all created objects.
	 * 
	 * @see #clearAutomaticConfigurationProperties(Map)
	 */
	protected void clearAutomaticToolProperties(Map<String, RoboticsObject> createdObjects) {
	}

	@Override
	protected final void setupEntities() throws EntityException, InitializationException {
		setup(base);
		setup(effectorFrame);

		setupToolEntities();
	}

	/**
	 * Method that can be overridden for setting up internal tool entities (e.g.
	 * Frames, Relations).
	 * 
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 */
	protected void setupToolEntities() throws EntityException, InitializationException {
		// empty
	}

	@Override
	protected final void cleanupEntities() throws EntityException, InitializationException {
		cleanupToolEntities();

		cleanup(base);
		cleanup(effectorFrame);
	}

	/**
	 * Method that can be overridden for cleaning up internal tool entities (e.g.
	 * Frames, Relations).
	 * 
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 */
	protected void cleanupToolEntities() throws EntityException, InitializationException {
		// empty
	}

	protected void setup(Frame frame) throws EntityException {
		frame.setParent(this);
	}

	protected void cleanup(Frame frame) throws EntityException, InitializationException {
		frame.setParent(null);
	}

	protected void setup(Relation relation) throws EntityException, InitializationException {
		if (!relation.isInitialized()) {
			getContext().initialize(relation);
		}
		relation.setParent(this);
	}

	protected void cleanup(Relation relation) throws EntityException, InitializationException {
		if (relation.isInitialized()) {
			getContext().uninitialize(relation);
		}
		relation.setParent(null);
	}

	@Override
	protected void setupDriver(D driver) {
		// empty...
	}
}
