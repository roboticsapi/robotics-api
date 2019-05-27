/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

/**
 * This interface represents a {@link ParallelGripper}'s base jaw.
 */
public interface BaseJaw extends Finger, RoboticsObject {

	/**
	 * Returns the frame for mounting a {@link GrippingFinger}.
	 * 
	 * @return the mount frame.
	 */
	Frame getMountFrame();

	/**
	 * Retrieves and returns a mounted {@link GrippingFinger}.
	 * 
	 * @return a mounted {@link GrippingFinger} or <code>null</code> if none can be
	 *         retrieved.
	 */
	GrippingFinger getMountedFinger();

	/**
	 * Mounts the given {@link GrippingFinger} onto this base jaw with the given
	 * transformation (between the base jaw's mount frame and the finger's base
	 * frame).
	 * 
	 * @param finger         the gripping finger to mount.
	 * @param transformation transformation between the base jaw's mount frame and
	 *                       the finger's base frame
	 * @throws RoboticsException if the given gripping finger cannot be mounted.
	 */
	void mount(GrippingFinger finger, Transformation transformation) throws RoboticsException;

	/**
	 * Unmounts a beforehand mounted {@link GrippingFinger} from this base jaw.
	 * 
	 * @return the unmounted gripping finger
	 * @throws RoboticsException if the given gripping finger cannot be unmounted.
	 */
	GrippingFinger unmount() throws RoboticsException;

}
