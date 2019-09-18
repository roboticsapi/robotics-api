/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public interface ExecutableCartesianMotionPlan extends CartesianMotionPlan {

	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time);

	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state);

	public RealtimeTwist getTwistSensorAt(RealtimeDouble time);
}
