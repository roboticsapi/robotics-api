/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.driver;

import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.OnlineObject.OperationStateListener;

public final class GenericInstantiator implements OperationStateListener {

	final private GenericLoadable driver;
	private boolean wasBuilt = false;

	public GenericInstantiator(GenericLoadable driver) {
		super();
		this.driver = driver;
	}

	@Override
	public void operationStateChanged(OnlineObject object, OperationState newState) {
		if (newState == OperationState.ABSENT) {
			wasBuilt = driver.build();
		}

		if (newState == OperationState.NEW) {
			if (wasBuilt) {
				driver.delete();
			}
		}
	}

}
