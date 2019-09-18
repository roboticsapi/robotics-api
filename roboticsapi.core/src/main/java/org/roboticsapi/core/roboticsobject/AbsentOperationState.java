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

public class AbsentOperationState extends InternalOperationState {

	public AbsentOperationState(AbstractOnlineObject object) {
		super("Absent", object);
	}

	@Override
	protected InitializedOperationState getParent() {
		return (InitializedOperationState) super.getParent();
	}

	@Override
	public OperationState getOperationState() {
		return OperationState.ABSENT;
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
	public void presentChecked(boolean result) {
		if (result) {
			if (getObject().checkDependentObjects()) {
				changeState(getParent().getPresentState());
			}
		}
	}

	@Override
	public void dependentObjectPresentChanged(RoboticsObject changedObject, boolean present) {
		if (!present) {
			changeState(getParent().getUnknownState());
		} else {
			getObject().checkConnection();
		}
	}

}
