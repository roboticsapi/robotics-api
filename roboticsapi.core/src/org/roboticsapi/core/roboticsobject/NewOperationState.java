/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class NewOperationState extends InternalOperationState {

	public NewOperationState(AbstractOnlineObject object) {
		super("New", object);
	}

	@Override
	protected OperationStateMachine getParent() {
		return (OperationStateMachine) super.getParent();
	}

	@Override
	public OperationState getOperationState() {
		return OperationState.NEW;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public boolean isPresent() {
		return getOperationState().isPresent();
	}

	@Override
	public void initialized() {
		changeState(getParent().getInitializedState());
	}

}
