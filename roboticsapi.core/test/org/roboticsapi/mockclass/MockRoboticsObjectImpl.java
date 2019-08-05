/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.RoboticsObject;

public class MockRoboticsObjectImpl extends AbstractRoboticsObject implements RoboticsObject {
	private final List<InitializationListener> initializationListeners = new ArrayList<InitializationListener>();

	public MockRoboticsObjectImpl() {
		setName("MockRoboticsObject");
	}

	protected List<InitializationListener> getInitializationListeners() {
		return initializationListeners;
	}

}
