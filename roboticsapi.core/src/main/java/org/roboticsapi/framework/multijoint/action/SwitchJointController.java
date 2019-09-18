/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.framework.multijoint.controller.JointController;

/**
 * Switch to specified controller of the multi-joint device
 */
public class SwitchJointController extends Action {

	private final JointController controller;

	/**
	 * Gets the associated Controller.
	 *
	 * @return Controller
	 */
	public JointController getController() {
		return controller;
	}

	/**
	 * Constructor
	 *
	 * @param controller to be used by the multi-joint device
	 */
	public SwitchJointController(JointController controller) {
		super(0, true, false);
		this.controller = controller;
	}
}
