/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.framework.cartesianmotion.controller.CartesianController;

/**
 * Switch to specified controller of the cartesian device
 */
public class SwitchCartesianController extends Action {

	private final CartesianController controller;

	/**
	 * Gets the associated Controller.
	 *
	 * @return Controller
	 */
	public CartesianController getController() {
		return controller;
	}

	/**
	 * Constructor
	 *
	 * @param controller to be used by the cartesian device
	 */
	public SwitchCartesianController(CartesianController controller) {
		super(0, true, false);
		this.controller = controller;
	}

}
