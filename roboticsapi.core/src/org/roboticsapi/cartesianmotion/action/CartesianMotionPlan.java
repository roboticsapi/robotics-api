/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.action.Plan;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;

public interface CartesianMotionPlan extends Plan {

	public Frame getBaseFrame();

	public Transformation getTransformationAt(double time);

	public Twist getTwistAt(double time);

	/**
	 * Retrieves the point in time during the execution of this plan when the
	 * actuator reaches the given transformation relative to the plan's base Frame.
	 *
	 * @param t the transformation
	 * @return the time (in s) when the transformation is reached, or null if it is
	 *         never reached
	 */
	public abstract Double getTimeForTransformation(Transformation t);

	/**
	 * Retrieves the point in time during the execution of this plan when the
	 * actuator reaches the given velocity relative to the plan's base Frame.
	 *
	 * @param t the velocity
	 * @return the time (in s) when the velocity is reached, or null if it is never
	 *         reached
	 */
	public abstract Double getTimeForTwist(Twist t);

}
