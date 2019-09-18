/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class PresentOperationState extends ComposedInternalOperationState {

	public PresentOperationState(AbstractOnlineObject object) {
		super(object, new OfflineOperationState(object), new SafeoperationalOperationState(object),
				new OperationalOperationState(object));
	}

	@Override
	protected InitializedOperationState getParent() {
		return (InitializedOperationState) super.getParent();
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public void presentChecked(boolean result) {
		if (!result) {
			changeState(getParent().getAbsentState());
		}
	}

	protected OfflineOperationState getOfflineOperationState() {
		return (OfflineOperationState) innerStates.get(0);
	}

	protected SafeoperationalOperationState getSafeoperationalOperationState() {
		return (SafeoperationalOperationState) innerStates.get(1);
	}

	protected OperationalOperationState getOperationalOperationState() {
		return (OperationalOperationState) innerStates.get(2);
	}

	@Override
	public void dependentObjectPresentChanged(RoboticsObject changedObject, boolean present) {
		if (!present) {
			changeState(getParent().getUnknownState());
		}
	}
}
