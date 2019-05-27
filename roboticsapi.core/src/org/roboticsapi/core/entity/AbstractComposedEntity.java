/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.entity;

import java.util.Set;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.EntitySet;
import org.roboticsapi.core.util.EntitySet.Verifier;

public abstract class AbstractComposedEntity extends AbstractEntity implements ComposedEntity {

	private final EntitySet set = new EntitySet(this, new Verifier() {

		@Override
		public void acceptRemoving(Entity child) throws EntityException {
			acceptRemovingChild(child);
		}

		@Override
		public void acceptAdding(Entity child) throws EntityException {
			acceptAddingChild(child);
		}
	});

	@Override
	public Set<Entity> getChildren() {
		return set.getChildren();
	}

	@Override
	public void addChild(Entity child) throws EntityException {
		set.add(child);
		notifyListenersOnRelationAdded(this, child);
	}

	@Override
	public boolean canAddChild(Entity child) {
		return set.canAdd(child);
	}

	@Override
	public void removeChild(Entity child) throws EntityException {
		set.remove(child);
		notifyListenersOnRelationRemoved(this, child);
	}

	@Override
	public boolean canRemoveChild(Entity child) {
		return set.canRemove(child);
	}

	protected void acceptAddingChild(Entity child) throws EntityException {
		if (this.isInitialized()) {
			throw new EntityException("Cannot add new child entity when already initialized.");
		}
	}

	protected void acceptRemovingChild(Entity child) throws EntityException {
		if (this.isInitialized()) {
			throw new EntityException("Cannot remove child entity when already initialized.");
		}
	}

	@Override
	protected void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();
		setupEntities();
	}

	/**
	 * Method that can be overridden for setting up internal entities (e.g.
	 * Entities, Frames, Relations) before the object is initialized.
	 *
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 *
	 * @see #beforeInitialization()
	 */
	protected void setupEntities() throws EntityException, InitializationException {
	}

	@Override
	protected void afterUninitialization() throws RoboticsException {
		cleanupEntities();
		super.afterUninitialization();
	}

	/**
	 * Method that can be overridden for cleaning up internal entities (e.g.
	 * Entities, Frames, Relations) after the object was uninitialized.
	 *
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 *
	 * @see #afterUninitialization()
	 */
	protected void cleanupEntities() throws EntityException, InitializationException {
	}

	/**
	 * Helper method that tries to uninitialize a {@link RoboticsObject} during
	 * cleanup.
	 * <p>
	 * Can be used as follows: <code>object = uninitialize(object);</code>
	 *
	 * @param object the object to uninitialize.
	 * @return always <code>null</code> for an easy cleanup
	 *
	 * @see #cleanupEntities()
	 */
	protected final <T extends RoboticsObject> T uninitialize(T object) {
		if (object != null && object.isInitialized()) {
			try {
				getContext().uninitialize(object);
			} catch (InitializationException e) {
				// ignore but log
				// TODO: Log problem
			}
		}
		return null;
	}

	protected final <T extends RoboticsObject> T initialize(T object) throws InitializationException {
		if (object != null && !object.isInitialized()) {
			getContext().initialize(object);
		}
		return object;
	}
}
