/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public abstract class InternalOperationState {

	public static interface StateListener {
		void stateEntered(InternalOperationState state);

		void stateLeft(InternalOperationState state, InternalOperationState nextState);
	}

	public static abstract class StateEnteredListener implements StateListener {
		@Override
		public void stateLeft(InternalOperationState state, InternalOperationState nextState) {
			// do nothing
		}
	}

	public abstract class StateLeftListener implements StateListener {
		@Override
		public void stateEntered(InternalOperationState state) {
			// do nothing
		}
	}

	protected ComposedInternalOperationState parent = null;
	protected String name;
	private final List<StateListener> listeners = new ArrayList<InternalOperationState.StateListener>();
	private final AbstractOnlineObject object;

	public InternalOperationState(String name, AbstractOnlineObject object) {
		this.name = name;
		this.object = object;
	}

	protected void setParent(ComposedInternalOperationState parent) {
		this.parent = parent;
	}

	protected ComposedInternalOperationState getParent() {
		return parent;
	}

	public void addListener(StateListener listener) {
		listeners.add(listener);
	}

	public void removeListener(StateListener listener) {
		listeners.remove(listener);
	}

	protected void notifyEntered(InternalOperationState state) {
		for (StateListener listener : listeners) {
			listener.stateEntered(state);
		}
	}

	protected void notifyLeft(InternalOperationState state, InternalOperationState nextState) {
		for (StateListener listener : listeners) {
			listener.stateLeft(state, nextState);
		}
	}

	protected void changeState(InternalOperationState newState) {
		ComposedInternalOperationState parent = getParent();

		if (getParent() == null) {
			throw new RuntimeException("State cannot be changed on root element");
		}

		InternalOperationState oldActive = parent.getActiveState();
		oldActive.beforeStateLeft();

		newState.beforeStateEntered(oldActive);

		parent.setActiveState(newState);

		notifyLeft(oldActive, newState);

		oldActive.afterStateLeft(newState);

		newState.notifyEntered(newState);

		newState.afterStateEntered();
	}

	protected void beforeStateLeft() {
	}

	protected void afterStateLeft(InternalOperationState nextState) {
	}

	protected void beforeStateEntered(InternalOperationState previousState) {
	}

	protected void afterStateEntered() {
	}

	public abstract OperationState getOperationState();

	public abstract boolean isInitialized();

	public abstract boolean isPresent();

	public void initialized() {
	}

	public void uninitialized() {
	}

	public void presentChecked(boolean result) {
	}

	public void dependentObjectPresentChanged(RoboticsObject changedObject, boolean present) {
	}

	public void goOffline() {
		throw new IllegalStateException("Cannot go offline from state " + getName());

	}

	public String getName() {
		return name;
	}

	public void goSafeoperational() {
		throw new IllegalStateException("Cannot go Safeoperational from state " + getName());

	}

	public void goOperational() {
		throw new IllegalStateException("Cannot go Operational from state " + getName());

	}

	protected AbstractOnlineObject getObject() {
		return object;
	}
}
