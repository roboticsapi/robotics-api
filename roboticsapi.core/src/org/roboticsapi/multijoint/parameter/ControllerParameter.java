/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.parameter;

import org.roboticsapi.core.DeviceParameters;

/**
 * Controller parameters for a robot.
 */
public class ControllerParameter implements DeviceParameters {

	private final Controller controller;

	public ControllerParameter(Controller controller) {
		this.controller = controller;
	}

	public Controller getController() {
		return controller;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof ControllerParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		// TODO: define some relation on Controller?
		return true;
	}

}
