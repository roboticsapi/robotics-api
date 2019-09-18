/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Orientation;
import org.roboticsapi.core.world.Point;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public interface CartesianVelocityMotionInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame. The frame is expected to apply the given velocity immediately.
	 *
	 * In contrast, the frame is allowed to obey its maximum acceleration while
	 * trying to apply the given velocity when using the method
	 * {@link #followVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	Activity holdVelocity(RealtimeVelocity velocity, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame. The frame is expected to apply the given velocity immediately.
	 *
	 * In contrast, the frame is allowed to obey its maximum acceleration while
	 * trying to apply the given velocity when using the method
	 * {@link #followVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param reference   the frame the controlled frame should move relative to
	 * @param orientation the orientation to interpret the twist (motion direction)
	 *                    in
	 * @param pivot       the center of rotation for rotational motions
	 * @param twist       the motion direction and velocity
	 * @param parameters  optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity holdVelocity(Frame reference, Orientation orientation, Point pivot, RealtimeTwist twist,
			DeviceParameters... parameters) throws RoboticsException {
		return holdVelocity(RealtimeVelocity.createFromTwist(reference, pivot, orientation, twist), parameters);
	}

	/**
	 * Creates an {@link RtActivity} that controls the translational Cartesian
	 * velocity of a frame. The frame is expected to apply the given velocity
	 * immediately.
	 *
	 * In contrast, the frame is allowed to obey its maximum acceleration while
	 * trying to apply the given velocity when using the method
	 * {@link #followVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param reference   the frame the controlled frame should move relative to
	 * @param orientation the orientation to interpret the motion direction in
	 * @param transVel    the translational motion direction and velocity
	 * @param parameters  optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity holdTranslationalVelocity(Frame reference, Orientation orientation, RealtimeVector transVel,
			DeviceParameters... parameters) throws RoboticsException {
		return holdVelocity(RealtimeVelocity.createFromTwist(reference, null, orientation,
				RealtimeTwist.createFromLinearAngular(transVel, RealtimeVector.ZERO)), parameters);
	}

	/**
	 * Creates an {@link RtActivity} that controls the rotational Cartesian velocity
	 * of a frame. The frame is expected to apply the given velocity immediately.
	 *
	 * In contrast, the frame is allowed to obey its maximum acceleration while
	 * trying to apply the given velocity when using the method
	 * {@link #followVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param reference   the frame the controlled frame should move relative to
	 * @param orientation the orientation to interpret the motion rotation axis in
	 * @param pivotPoint  the point the rotation should be executed around
	 * @param rotVel      axis giving the rotation direction and velocity
	 * @param parameters  optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity holdRotationalVelocity(Frame reference, Orientation orientation, Point pivotPoint,
			RealtimeVector rotVel, DeviceParameters... parameters) throws RoboticsException {
		return holdVelocity(RealtimeVelocity.createFromTwist(reference, pivotPoint, orientation,
				RealtimeTwist.createFromLinearAngular(RealtimeVector.ZERO, rotVel)), parameters);
	}

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame. The frame should try to apply the given velocity according to its
	 * capabilities.
	 *
	 * In contrast, has to exactly apply the given velocity when using the method
	 * {@link #holdVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param velocity   the velocity that should be applied
	 * @param parameters optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	Activity followVelocity(RealtimeVelocity velocity, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian velocity of a
	 * frame. The frame should try to apply the given velocity according to its
	 * capabilities.
	 *
	 * In contrast, has to exactly apply the given velocity when using the method
	 * {@link #holdVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param reference   the frame the controlled frame should move relative to
	 * @param orientation the orientation to interpret the twist (motion direction)
	 *                    in
	 * @param pivot       the center of rotation for rotational motions
	 * @param twist       the motion direction and velocity
	 * @param parameters  optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity followVelocity(Frame reference, Orientation orientation, Point pivot, RealtimeTwist twist,
			DeviceParameters... parameters) throws RoboticsException {
		return followVelocity(RealtimeVelocity.createFromTwist(reference, pivot, orientation, twist), parameters);
	}

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian translational
	 * velocity of a frame. The frame should try to apply the given velocity
	 * according to its capabilities.
	 *
	 * In contrast, has to exactly apply the given velocity when using the method
	 * {@link #holdVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param reference   the frame the controlled frame should move relative to
	 * @param orientation the orientation to interpret the motion direction in
	 * @param transVel    the translational motion direction and velocity
	 * @param parameters  optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity followTranslationalVelocity(Frame reference, Orientation orientation, RealtimeVector transVel,
			DeviceParameters... parameters) throws RoboticsException {
		return followVelocity(RealtimeVelocity.createFromTwist(reference, null, orientation,
				RealtimeTwist.createFromLinearAngular(transVel, RealtimeVector.ZERO)), parameters);
	}

	/**
	 * Creates an {@link RtActivity} that controls the Cartesian rotational velocity
	 * of a frame. The frame should try to apply the given velocity according to its
	 * capabilities.
	 *
	 * In contrast, has to exactly apply the given velocity when using the method
	 * {@link #holdVelocity(RealtimeVelocity, DeviceParameters...)}, .
	 *
	 * @param reference   the frame the controlled frame should move relative to
	 * @param orientation the orientation to interpret the motion rotation axis in
	 * @param pivotPoint  the point the rotation should be executed around
	 * @param rotVel      axis giving the rotation direction and velocity
	 * @param parameters  optional DeviceParameters
	 * @return an {@link RtActivity} for controlling the Cartesian velocity
	 *
	 * @throws RoboticsException if the requested kind of velocity is invalid
	 */
	default Activity followRotationalVelocity(Frame reference, Orientation orientation, Point pivotPoint,
			RealtimeVector rotVel, DeviceParameters... parameters) throws RoboticsException {
		return followVelocity(RealtimeVelocity.createFromTwist(reference, pivotPoint, orientation,
				RealtimeTwist.createFromLinearAngular(RealtimeVector.ZERO, rotVel)), parameters);
	}
}
