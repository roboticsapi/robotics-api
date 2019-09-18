/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * An event handler for Robotics API commands
 */
public class EventHandler {
	/** Event handled */
	private RealtimeBoolean state;
	private Command context;
	private EventEffect effect;

	/**
	 * Creates an event handler for a given event
	 *
	 * @param state event handled
	 * @throws RoboticsException if the event is invalid
	 */
	private EventHandler(final RealtimeBoolean state, EventEffect effect) throws RoboticsException {
		setState(state);
		setEffect(effect);
	}

	public static EventHandler OnStateEntered(RealtimeBoolean state, EventEffect effect, Command command)
			throws RoboticsException {
		return new EventHandler(state, effect);
	}

	public static EventHandler OnStateFirstEntered(RealtimeBoolean state, EventEffect effect, Command command)
			throws RoboticsException {
		return new EventHandler(state.hasBeenActive(command.getRuntime()), effect);
	}

	public static EventHandler OnStateLeft(RealtimeBoolean state, EventEffect effect, Command command)
			throws RoboticsException {
		return new EventHandler(state.not().and(state.hasBeenActive(command.getRuntime())), effect);
	}

	public static EventHandler OnStateFirstLeft(RealtimeBoolean state, EventEffect effect, Command command)
			throws RoboticsException {
		return new EventHandler(
				state.not().and(state.hasBeenActive(command.getRuntime()).hasBeenActive(command.getRuntime())), effect);
	}

	/**
	 * Sets the effect of this event handler
	 *
	 * @param effect effect to execute when event occurs
	 * @throws RoboticsException
	 */
	public void setEffect(EventEffect effect) throws RoboticsException {
		this.effect = effect;
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
	private void setState(final RealtimeBoolean cause) throws RoboticsException {
		this.state = cause;
	}

	/**
	 * Retrieves the event handled
	 *
	 * @return event handled by this event handler
	 */
	public RealtimeBoolean getState() {
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
	}

}
