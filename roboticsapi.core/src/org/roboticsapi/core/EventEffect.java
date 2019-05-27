/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * An event handler for Robotics API commands
 */
public abstract class EventEffect {
	/** Event handled */
	private Command context;

	/**
	 * Creates an event handler for a given event
	 * 
	 * @param event event handled
	 */
	protected EventEffect() {
		this(null);
	}

	protected EventEffect(Command context) {
		setContext(context);
	}

	/**
	 * Retrieves the context of the event handler
	 * 
	 * @return context of the event handler (command during which the event is
	 *         expected)
	 */
	public Command getContext() {
		return context;
	}

	/**
	 * Sets the context of the event handler
	 * 
	 * @param context context of the event handler (command during which the event
	 *                is expected, or null for any)
	 */
	public void setContext(final Command context) {
		this.context = context;

		validate();
	}

	protected void validate() {
	}

}
