/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.core.DeviceParameters;

public class HybridMoveInterfaceImpl extends ActuatorInterfaceImpl<CartesianMotionDevice>
		implements HybridMoveInterface {

	public HybridMoveInterfaceImpl(CartesianMotionDevice device) {
		super(device);
	}

	@Override
	public HybridMove hybridMove(DeviceParameters... parameters) {
		return new HybridMove(getDevice(), getDefaultParameters().withParameters(parameters));
	}

}
