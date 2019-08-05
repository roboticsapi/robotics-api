/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.exception.OperationStateUnknownException;

public class MockAbstractOnlineObject extends AbstractOnlineObject {
	@Override
	public void mirrorOperationState(OnlineObject other) {
		super.mirrorOperationState(other);
	}

	@Override
	public boolean checkPresent() throws OperationStateUnknownException {
		return super.checkPresent();
	}
}
