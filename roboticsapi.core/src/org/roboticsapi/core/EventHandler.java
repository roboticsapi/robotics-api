/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * An event handler for Robotics API commands
 */
public class EventHandler {
	/** Event handled */
	private State state;
	private Command context;
	private EventEffect effect;

	/**
	 * Creates an event handler for a given event
	 * 
	 * @param state event handled
	 * @throws RoboticsException if the event is invalid
	 */
	private EventHandler(final State state, EventEffect effect) throws RoboticsException {
		this(state, effect, null);
	}

	private EventHandler(final State state, EventEffect effect, Command context) throws RoboticsException {
		setState(state);
		setEffect(effect);
		setContext(context);
	}

	public static EventHandler OnStateEntered(State state, EventEffect effect) throws RoboticsException {
		return new EventHandler(state, effect);
	}

	public static EventHandler OnStateFirstEntered(State state, EventEffect effect) throws RoboticsException {
		return new EventHandler(state.hasBeenActive(), effect);
	}

	public static EventHandler OnStateLeft(State state, EventEffect effect) throws RoboticsException {
		return new EventHandler(state.not().and(state.hasBeenActive()), effect);
	}

	public static EventHandler OnStateFirstLeft(State state, EventEffect effect) throws RoboticsException {
		return new EventHandler(state.not().and(state.hasBeenActive().hasBeenActive()), effect);
	}

	/**
	 * Sets the effect of this event handler
	 * 
	 * @param effect effect to execute when event occurs. May not be null.
	 * @throws RoboticsException
	 */
	public void setEffect(EventEffect effect) throws RoboticsException {
		if (effect == null) {
			throw new IllegalArgumentException("Effect must not be null.");
		}

		this.effect = effect;

		validate();
	}

	/**
	 * Retrieves the effect of this event handler
	 * 
	 * @return effect of the event handler
	 */
	public EventEffect getEffect() {
		return effect;
	}

	/**
	 * Sets the event to handle
	 * 
	 * @param cause event to handle
	 * @throws RoboticsException
	 */
	private void setState(final State cause) throws RoboticsException {
		this.state = cause;

		validate();
	}

	/**
	 * Retrieves the event handled
	 * 
	 * @return event handled by this event handler
	 */
	public State getState() {
		return state;
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
	 * @throws RoboticsException
	 */
	public void setContext(final Command context) throws RoboticsException {
		this.context = context;

		validate();
	}

	// TODO: is the branch if (getContext() != null) redundant? Or is designed
	// for extensibility?
	protected void validate() throws RoboticsException {
		if (this.context != null) {
			effect.setContext(this.context);
		}

		if (getContext() != null) {
			if (!getContext().validateState(getState())) {
				getContext().validateState(getState());
				throw new RoboticsException("State (" + getState().toString() + ") is invalid in the given context ("
						+ getContext().toString() + ")");
			}
		}

		if (effect != null) {
			effect.validate();
		}
	}

}
