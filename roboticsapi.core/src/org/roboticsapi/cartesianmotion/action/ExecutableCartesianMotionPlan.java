/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.State;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.sensor.TransformationSensor;

public interface ExecutableCartesianMotionPlan extends CartesianMotionPlan {

	public TransformationSensor getTransformationSensorAt(DoubleSensor time);

	public BooleanSensor getStateSensorAt(DoubleSensor time, State state);
}
