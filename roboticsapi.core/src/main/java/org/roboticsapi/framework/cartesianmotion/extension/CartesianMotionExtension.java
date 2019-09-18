/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.extension;

import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuator;

public class CartesianMotionExtension extends AbstractRoboticsObjectBuilder {

	public CartesianMotionExtension() {
		super(CartesianActuator.class);
	}

}
