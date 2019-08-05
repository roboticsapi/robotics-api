/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.OnlineObject;

public class MockOnlineObjectImpl extends MockRoboticsObjectImpl implements OnlineObject {
	private final List<OperationStateListener> operationStateListeners = new ArrayList<OperationStateListener>();
	private OperationState state = null;
	private boolean present = false;

	public MockOnlineObjectImpl() {
		setName("MockOnlineObject");
	}

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
		operationStateListeners.add(listener);
	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
		if (!operationStateListeners.isEmpty()) {
			operationStateListeners.remove(listener);
		}
	}

	public void setState(OperationState state) {
		this.state = state;
	}

	@Override
	public OperationState getState() {
		return state;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	@Override
	public boolean isPresent() {
		return present;
	}
}
