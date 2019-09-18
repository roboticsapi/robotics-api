/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.TransformationException;

/**
 * Interface for simple, non-actuated gripping fingers used e.g. in 2-Finger
 * parallel/concentric grippers. A gripping finger is usually mounted onto a
 * gripper's {@link BaseJaw}.
 *
 * @see ParallelGripper
 */
public interface GrippingFinger extends Finger {

	/**
	 * Gets the base {@link Frame} of this gripping finger.
	 *
	 * It should be aligned as follows:
	 * <ul>
	 * <li>the frame's x-axis points away from the gripper's center frame.
	 * <li>the frame's z-axis points away from the gripper's base frame.
	 * </ul>
	 *
	 * @return the base {@link Frame}
	 */
	@Override
	Frame getBase();

	/**
	 * Gets the finger's tip {@link Frame}.
	 *
	 * @return the tip frame.
	 */
	Frame getTipFrame();

	/**
	 * Gets the offset between the finger's base frame and its tip frame. A positive
	 * finger offset increases the finger distance compared to the base jaws; a
	 * negative finger offset decreases the finger distance.
	 *
	 * @return the offset between the finger's base frame and its contact frame in
	 *         [m].
	 *
	 * @throws TransformationException if the transformation between base jaw and
	 *                                 gripping finger cannot be calculated
	 */
	double getOffset();

	double getLength();

}
