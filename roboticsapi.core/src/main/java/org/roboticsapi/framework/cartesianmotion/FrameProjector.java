/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;

public interface FrameProjector {
	public RealtimePose project(RealtimePose target, RealtimePose motionCenter, DeviceParameterBag parameters)
			throws TransformationException;
}
