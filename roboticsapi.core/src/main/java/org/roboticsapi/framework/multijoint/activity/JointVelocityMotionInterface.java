/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.framework.multijoint.MultiJointDevice;

public interface JointVelocityMotionInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} that controls the rotational Joint velocity of
	 * a {@link MultiJointDevice}. The joint is expected to apply the given velocity
	 * immediately.
	 *
	 * In contrast, the device is allowed to obey its maximum acceleration while
	 * trying to apply the given velocity when using the method
	 * {@link #followVelocity(RealtimeDouble[] velocity, DeviceParameters...)}, .
	 *
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Joint velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	Activity holdVelocity(RealtimeDouble[] velocity, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the rotational Joint velocity of
	 * a {@link MultiJointDevice}. The joint is expected to apply the given velocity
	 * immediately.
	 *
	 * In contrast, the device is allowed to obey its maximum acceleration while
	 * trying to apply the given velocity when using the method
	 * {@link #followVelocity(RealtimeDouble[] velocity, DeviceParameters...)}, .
	 *
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Joint velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity holdVelocity(RealtimeDoubleArray velocity, DeviceParameters... parameters)
			throws RoboticsException {
		return holdVelocity(velocity.getDoubles(), parameters);
	}

	/**
	 * Creates an {@link RtActivity} that controls the Joint velocity of a
	 * {@link MultiJointDevice}. The Joint should try to apply the given velocity
	 * according to its capabilities.
	 *
	 * In contrast, the device has to exactly apply the given velocity when using
	 * the method {@link #holdVelocity(RealtimeDouble[] velocity,
	 * DeviceParameters...)}, .
	 *
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Joint velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	Activity followVelocity(RealtimeDouble[] velocity, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Joint velocity of a
	 * {@link MultiJointDevice}. The Joint should try to apply the given velocity
	 * according to its capabilities.
	 *
	 * In contrast, the device has to exactly apply the given velocity when using
	 * the method {@link #holdVelocity(RealtimeDouble[] velocity,
	 * DeviceParameters...)}, .
	 *
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Joint velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity followVelocity(RealtimeDoubleArray velocity, DeviceParameters... parameters)
			throws RoboticsException {
		return followVelocity(velocity.getDoubles(), parameters);
	}

}
