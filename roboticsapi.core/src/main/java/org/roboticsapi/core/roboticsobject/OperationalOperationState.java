/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.roboticsobject;

import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class OperationalOperationState extends InternalOperationState {

	public OperationalOperationState(AbstractOnlineObject object) {
		super("Operational", object);
	}

	@Override
	public OperationState getOperationState() {
		return OperationState.OPERATIONAL;
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
	protected PresentOperationState getParent() {
		return (PresentOperationState) super.getParent();
	}

	@Override
	public void goSafeoperational() {
		changeState(getParent().getSafeoperationalOperationState());
	}

	@Override
	public void goOffline() {
		changeState(getParent().getOfflineOperationState());
	}

	@Override
	public void goOperational() {
		// do nothing
		// TODO: change state to operational again for listeners?
	}

}
