/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class InitializedOperationState extends ComposedInternalOperationState {

	public InitializedOperationState(AbstractOnlineObject object) {
		super(object, new UnknownOperationState(object), new AbsentOperationState(object),
				new PresentOperationState(object));
	}

	@Override
	protected OperationStateMachine getParent() {
		return (OperationStateMachine) super.getParent();
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public boolean isPresent() {
		return activeInnerState.isPresent();
	}

	@Override
	public void uninitialized() {
		changeState(getParent().getNewState());
	}

	public PresentOperationState getPresentState() {
		return (PresentOperationState) innerStates.get(2);
	}

	public AbsentOperationState getAbsentState() {
		return (AbsentOperationState) innerStates.get(1);
	}

	public UnknownOperationState getUnknownState() {
		return (UnknownOperationState) innerStates.get(0);
	}

}
