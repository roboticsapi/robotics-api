/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class UnknownOperationState extends InternalOperationState {

	public UnknownOperationState(AbstractOnlineObject object) {
		super("Unknown", object);
	}

	@Override
	protected InitializedOperationState getParent() {
		return (InitializedOperationState) super.getParent();
	}

	@Override
	public OperationState getOperationState() {
		return OperationState.UNKNOWN;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public boolean isPresent() {
		return getOperationState().isPresent();
	}

	@Override
	public void dependentObjectPresentChanged(RoboticsObject changedObject, boolean present) {
		if (present) {
			getObject().checkConnection();
		}
	}

	@Override
	public void presentChecked(boolean result) {
		if (result) {
			changeState(getParent().getPresentState());
		} else {
			changeState(getParent().getAbsentState());
		}
	}
}
