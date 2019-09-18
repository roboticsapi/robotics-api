/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Collection;
import java.util.List;

/**
 * Interface for all configurable objects in the Robotics API.
 */
public interface RoboticsObject {

	/**
	 * Listener which gets notified when a {@link RoboticsObject} holds adds a
	 * reference to another {@link RoboticsObject}.
	 */
	public interface ReferenceListener {

		/**
		 * Called when a reference is added
		 *
		 * @param referencingObject object that holds a reference to referencedObject
		 * @param referencedObject  the object referenced
		 */
		public void referenceAdded(RoboticsObject referencingObject, RoboticsObject referencedObject);

		/**
		 * Called when a reference is removed
		 *
		 * @param referencingObject object that held a reference to referencedObject
		 * @param referencedObject  the object referenced
		 */
		public void referenceRemoved(RoboticsObject referencingObject, RoboticsObject referencedObject);
	}

	/**
	 * Listener which gets notified when a {@link RoboticsObject} was
	 * initialized/uninitialized.
	 */
	public interface InitializationListener {

		/**
		 * Called when the given {@link RoboticsObject} was initialized.
		 *
		 * @param o the robotics object
		 */
		public void onInitialized(RoboticsObject o);

		/**
		 * Called when the given {@link RoboticsObject} was uninitialized.
		 *
		 * @param o the robotics object
		 */
		public void onUninitialized(RoboticsObject o);

	}

	/**
	 * Gets the name of this robotics object.
	 *
	 * @return the name.
	 */
	public abstract String getName();

	public abstract void setName(String name);

	/**
	 * Returns whether this robotics object is properly initialized.
	 *
	 * @return <code>true</code> if this robotics object is properly initialized;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isInitialized();

	/**
	 * Adds a {@link InitializationListener}.
	 *
	 * @param listener the listener to add.
	 */
	public abstract void addInitializationListener(InitializationListener listener);

	/**
	 * Removes a {@link InitializationListener}.
	 *
	 * @param listener the listener to remove.
	 */
	public abstract void removeInitializationListener(InitializationListener listener);

	// /**
	// * Adds a reference to this object
	// *
	// * @param referencingObject
	// * object that references this object
	// */
	// public abstract void addReference(RoboticsObject referencingObject);
	//
	// /**
	// * Removes a reference to this object
	// *
	// * @param referencingObject
	// * object that referenced this object
	// */
	// public abstract void removeReference(RoboticsObject referencingObject);

	/**
	 * Retrieves all objects currently referencing this object
	 *
	 * @return collection of objects referencing this object
	 */
	public abstract Collection<RoboticsObject> getReferences();

	// /**
	// * Retrieves all objects referenced by this object
	// *
	// * @return collection of objects referenced by this object
	// */
	// public abstract Collection<RoboticsObject> getReferencedObjects();

	/**
	 * Adds a reference listener
	 *
	 * @param listener reference listener to call when references are added or
	 *                 removed
	 */
	public abstract void addReferenceListener(ReferenceListener listener);

	/**
	 * Removes a reference listener
	 *
	 * @param listener reference listener to call when references are added or
	 *                 removed
	 */
	public abstract void removeReferenceListener(ReferenceListener listener);

	/**
	 * Listener which gets notified when a {@link Relationship} was added or
	 * removed.
	 */
	public interface RelationshipListener<R extends Relationship> {

		/**
		 * Called when the given {@link Relationship} was added.
		 * <p>
		 * When the listener is added to a {@link RoboticsObject}, it also gets informed
		 * about current {@link Relationship}s.
		 *
		 * @param r the added relationship
		 */
		public void onAdded(R r);

		/**
		 * Called when the given {@link Relationship} was removed.
		 * <p>
		 * When the listener is removed from a {@link RoboticsObject}, it also gets
		 * informed about the current {@link Relationship}s.
		 *
		 * @param r the removed relationship
		 */
		public void onRemoved(R r);

	}

	/**
	 * Retrieves all {@link Relationship}s of a given type
	 *
	 * @param type type of relationship
	 * @return list of all {@link Relationship}s
	 */
	public abstract <R extends Relationship> List<R> getRelationships(Class<R> type);

	/**
	 * Adds a listener for {@link Relationship}s of a given type
	 *
	 * @param type     type of relationship
	 * @param listener listener informed about {@link Relationship}s
	 */
	public abstract <R extends Relationship> void addRelationshipListener(Class<R> type,
			RelationshipListener<R> listener);

	/**
	 * Removes a listener for {@link Relationship}s of a given type
	 *
	 * @param type     type of relationship
	 * @param listener listener informed about {@link Relationship}s
	 */
	public abstract <R extends Relationship> void removeRelationshipListener(Class<R> type,
			RelationshipListener<R> listener);

}