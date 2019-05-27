/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

/**
 * The Interface HoldMotionInterface creates RtActivities that let Actuators
 * hold certain poses.
 */
public interface HoldMotionInterface extends DeviceInterface {

	/**
	 * Creates an {@link RtActivity} that lets the {@link Actuator} hold the given
	 * position, specified as a Frame.
	 * 
	 * The Actuator is expected to have its motion center already located at the
	 * specified position. The Actuator will try to follow all motions of the given
	 * Frame without any interpolation.
	 * 
	 * @param position   the position to hold
	 * @param parameters DeviceParameters to use for RtActivity execution
	 * @return the RtActivity that lets the Actuator hold the given position
	 * @throws RoboticsException thrown if RtActivity creation failed
	 */
	public RtActivity holdCartesianPosition(Frame position, DeviceParameters... parameters) throws RoboticsException;
}
