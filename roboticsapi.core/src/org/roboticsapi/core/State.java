/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.AndState;
import org.roboticsapi.core.state.ExceptionState;
import org.roboticsapi.core.state.ExplicitState;
import org.roboticsapi.core.state.FalseState;
import org.roboticsapi.core.state.LongState;
import org.roboticsapi.core.state.NotState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.core.state.TrueState;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * A Robotics API State describes a certain aspect of a system's total state.
 * This particular aspect can be active at a given time or not active.
 * 
 * States can be used to trigger {@link EventEffect}s when they are entered or
 * left in running {@link Command}s.
 */
public abstract class State {

	/**
	 * Instantiates a new state.
	 */
	protected State() {
	}

	/**
	 * Returns the negated State of this State (a State that is active when this
	 * State is not active and not active when this State is active).
	 * 
	 * @return the negated State of this State
	 */
	public State not() {
		return new NotState(this);
	}

	/**
	 * Returns a State that is active as soon as this State has been active at least
	 * once.
	 * 
	 * @return the derived State
	 */
	public State hasBeenActive() {
		return new ExplicitState(this, new FalseState());
	}

	/**
	 * Returns a State that is active each time this State is active and the given
	 * other State is active at a certain point in time.
	 * 
	 * @param other the other State
	 * @return the combined State
	 */
	public State and(final State other) {
		return new AndState(this, other);
	}

	/**
	 * Returns a State that is active each time this State is active or the given
	 * other State is active at a certain point in time.
	 * 
	 * @param other the other State
	 * @return the combined State
	 */
	public State or(final State other) {
		return new OrState(this, other);
	}

	/**
	 * Returns a State that is active each time this State and the given other State
	 * are not both active.
	 * 
	 * @param other the other State
	 * @return the combined State
	 */
	public State xor(final State other) {
		return (not().and(other)).or(and(other.not()));
	}

	/**
	 * Returns a State that becomes active as soon as this State has been active for
	 * a certain time.
	 * 
	 * @param seconds the time (in seconds) this State has to be active to trigger
	 *                the returned State
	 * @return the derived State
	 */
	public State forSeconds(double seconds) {
		return new LongState(this, seconds);
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}

	public boolean subclassEquals(State other) {
		return getClass() != other.getClass() && getClass().isAssignableFrom(other.getClass());
	}

	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getClass());
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * Returns a State that is always active.
	 * 
	 * @return true State
	 */
	public static State True() {
		return new TrueState();
	}

	/**
	 * Returns a State that is never active.
	 * 
	 * @return false State
	 */
	public static State False() {
		return new FalseState();
	}

	/**
	 * Returns a State that becomes active upon the occurrence of the given
	 * {@link CommandRealtimeException}.
	 * 
	 * @param exception the exception that activates the returned State
	 * @return the exception State
	 */
	public static State fromException(CommandRealtimeException exception) {
		return new ExceptionState(exception);
	}

	/**
	 * Returns a State that becomes active upon the occurrence of any of the given
	 * {@link CommandRealtimeException}s.
	 * 
	 * @param exceptions the exceptions that activate the returned State
	 * @return the exception State
	 */
	public static State fromExceptions(List<CommandRealtimeException> exceptions) {
		OrState ret = new OrState();
		for (CommandRealtimeException ex : exceptions) {
			ret.addState(new ExceptionState(ex));
		}
		return ret;
	}
}
