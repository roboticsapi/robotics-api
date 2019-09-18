/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.AbstractRoboticsEntity;
import org.roboticsapi.core.RoboticsEntity;

public class MockRoboticsEntityImpl extends AbstractRoboticsEntity implements RoboticsEntity {
	private final List<InitializationListener> initializationListeners = new ArrayList<InitializationListener>();

	public MockRoboticsEntityImpl() {
		setName("MockRoboticsObject");
	}

	protected List<InitializationListener> getInitializationListeners() {
		return initializationListeners;
	}

}
