/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Interface for all configurable objects in the Robotics API.
 */
public interface RoboticsObject {

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

}