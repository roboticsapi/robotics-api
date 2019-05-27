/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;

public interface FrameProjector {
	public RelationSensor project(RelationSensor toProject, DeviceParameterBag parameters)
			throws TransformationException;
}
