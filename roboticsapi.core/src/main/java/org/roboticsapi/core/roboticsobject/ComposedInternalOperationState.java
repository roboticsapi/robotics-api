/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public abstract class ComposedInternalOperationState extends InternalOperationState {

	protected final List<InternalOperationState> innerStates = new ArrayList<InternalOperationState>();
	protected InternalOperationState activeInnerState = null;

	public ComposedInternalOperationState(AbstractOnlineObject object, InternalOperationState startState,
			InternalOperationState... furtherStates) {
		super("ComposedInternalOperationState", object);

		innerStates.add(startState);
		startState.setParent(this);
		for (InternalOperationState f : furtherStates) {
			innerStates.add(f);
			f.setParent(this);
		}

		activeInnerState = startState;
	}

	@Override
	public String getName() {
		return activeInnerState.getName();
	}

	@Override
	public OperationState getOperationState() {
		return activeInnerState.getOperationState();
	}

	@Override
	public void initialized() {
		activeInnerState.initialized();
	}

	@Override
	public void uninitialized() {
		activeInnerState.uninitialized();
	}

	@Override
	public void presentChecked(boolean result) {
		activeInnerState.presentChecked(result);
	}

	@Override
	public void dependentObjectPresentChanged(RoboticsObject changedObject, boolean present) {
		activeInnerState.dependentObjectPresentChanged(changedObject, present);
	}

	public InternalOperationState getActiveState() {
		return activeInnerState;
	}

	public void setActiveState(InternalOperationState newState) {
		activeInnerState = newState;
	}

	@Override
	protected void afterStateLeft(InternalOperationState newState) {
		activeInnerState.afterStateLeft(newState);
		activeInnerState.notifyLeft(activeInnerState, newState);
	}

	@Override
	protected void afterStateEntered() {
		activeInnerState.notifyEntered(activeInnerState);
		activeInnerState.afterStateEntered();
	}

	@Override
	public void goOffline() {
		activeInnerState.goOffline();
	}

	@Override
	public void goOperational() {
		activeInnerState.goOperational();
	}

	@Override
	public void goSafeoperational() {
		activeInnerState.goSafeoperational();
	}

}
