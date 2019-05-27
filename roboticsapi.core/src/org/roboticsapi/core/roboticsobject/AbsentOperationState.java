/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class AbsentOperationState extends InternalOperationState {
	public AbsentOperationState(AbstractOnlineObject object) {
		// TODO: may argument object not be null?
		// super("Absent", Objects.requireNonNull(object,
		// "Argument object may not be null."));
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
			// TODO: getParent() may not be null
			if (getParent() == null) {
				throw new IllegalArgumentException(
						"There's no parent for this AbsentOperationState object. Set a valid parent before calling this operation.");
			}

			changeState(getParent().getUnknownState());
		} else {
			getObject().checkConnection();
		}
	}

}
