/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.framework.tool.Tool;

/**
 * A (finger-based) gripping device (e.g. a parallel gripper or a dexterous
 * hand).
 */
public interface Gripper extends Tool {

	/**
	 * Gets the number of gripping fingers.
	 *
	 * @return the number of gripping fingers.
	 */
	public int getFingerCount();

	/**
	 * Gets the {@link Finger} for the given index (zero-based).
	 *
	 * @param index the finger index
	 * @return the gripping finger
	 */
	public Finger getFinger(int index);

	/**
	 * Gets all of the gripper's {@link Finger}s.
	 *
	 * @return the gripping fingers
	 */
	public Finger[] getFingers();

	/**
	 * Returns the gripper's recommended workpiece weight in [kg].
	 *
	 * @return the recommended workpiece weight in [kg].
	 */
	public double getRecommendedWorkpieceWeight();

	@Override
	GripperDriver getDriver();

}
