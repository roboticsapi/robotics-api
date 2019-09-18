/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class OperationStateMachine extends ComposedInternalOperationState {

	public OperationStateMachine(AbstractOnlineObject object) {
		super(object, new NewOperationState(object), new InitializedOperationState(object));
	}

	@Override
	public boolean isInitialized() {
		return activeInnerState.isInitialized();
	}

	@Override
	public boolean isPresent() {
		return activeInnerState.isPresent();
	}

	public InitializedOperationState getInitializedState() {
		return (InitializedOperationState) innerStates.get(1);
	}

	public NewOperationState getNewState() {
		return (NewOperationState) innerStates.get(0);
	}

	public UnknownOperationState getUnknownOperationState() {
		return getInitializedState().getUnknownState();
	}

	public AbsentOperationState getAbsentOperationState() {
		return getInitializedState().getAbsentState();
	}

	public PresentOperationState getPresentOperationState() {
		return getInitializedState().getPresentState();
	}

	public OfflineOperationState getOfflineOperationState() {
		return getPresentOperationState().getOfflineOperationState();
	}

	public SafeoperationalOperationState getSafeOperationalOperationState() {
		return getPresentOperationState().getSafeoperationalOperationState();
	}

	public OperationalOperationState getOperationalOperationState() {
		return getPresentOperationState().getOperationalOperationState();
	}
}
