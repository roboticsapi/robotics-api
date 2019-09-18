/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.device;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;

public interface CartesianActuatorDriver extends ActuatorDriver {

	Relation createRelation(Frame from, Frame to);

	@Override
	public CartesianMotionDevice getDevice();

}
