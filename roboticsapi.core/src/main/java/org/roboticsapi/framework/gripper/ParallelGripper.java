/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;

/**
 * A parallel gripping device, i.e. the gripper has two parallel oriented
 * {@link GrippingFinger}.
 */
public interface ParallelGripper extends Gripper {

	/**
	 * Gets the {@link BaseJaw} for the given index (zero-based).
	 *
	 * @param index the base jaw index
	 * @return the base jaw
	 */
	BaseJaw getBaseJaw(int index);

	/**
	 * Gets all of the gripper's {@link BaseJaw}s.
	 *
	 * @return the base jaws
	 */
	BaseJaw[] getBaseJaws();

	/**
	 * Gets the {@link Frame} which is the movement origin (fixed frame) of the
	 * moving {@link BaseJaw} with the given index.
	 *
	 * @param index the index of {@link BaseJaw}
	 * @return the movement origin
	 */
	public Frame getBaseJawOrigin(int index);

	/**
	 * Gets the stroke per finger in [m].
	 *
	 * @return the stroke per finger in [m].
	 */
	double getStrokePerFinger();

	/**
	 * Returns the minimal possible opening width between the {@link Finger}s in
	 * [m].
	 *
	 * @return The minimal possible opening width in [m].
	 * @throws RoboticsException if the minimal opening width cannot be calculated.
	 */
	double getMinimalOpeningWidth() throws RoboticsException;

	/**
	 * Returns the maximal possible opening width between the {@link Finger}s in
	 * [m].
	 *
	 * @return The maximal possible opening width in [m].
	 * @throws RoboticsException if the maximal opening width cannot be calculated.
	 */
	double getMaximalOpeningWidth() throws RoboticsException;

	/**
	 * Returns the minimal possible opening width between the {@link BaseJaw}s in
	 * [m].
	 *
	 * @return The minimal possible opening width in [m].
	 * @throws RoboticsException if the minimal opening width cannot be calculated.
	 */
	double getMinimalBaseJawOpeningWidth() throws RoboticsException;

	/**
	 * Returns the maximal possible opening width between the {@link BaseJaw}s in
	 * [m].
	 *
	 * @return The maximal possible opening width in [m].
	 * @throws RoboticsException if the maximal opening width cannot be calculated.
	 */
	double getMaximalBaseJawOpeningWidth() throws RoboticsException;

	/**
	 * Calculates the finger opening width in [m] from the base jaw opening width
	 * regarding {@link Finger} offsets.
	 *
	 * @param baseJawOpeningWidth
	 * @return
	 */
	double getOpeningWidthFrom(double baseJawOpeningWidth) throws TransformationException;

	/**
	 * Calculates the finger opening width in [m] from the base jaw opening width
	 * regarding {@link Finger} offsets.
	 *
	 * @param baseJawOpeningWidth
	 * @return
	 */
	RealtimeDouble getOpeningWidthFrom(RealtimeDouble baseJawOpeningWidth) throws TransformationException;

	/**
	 * Calculates the base jaw opening width in [m] from the finger opening width
	 * regarding {@link Finger} offsets.
	 *
	 * @param fingerOpeningWidth
	 * @return
	 */
	double getBaseJawOpeningWidthFrom(double fingerOpeningWidth) throws TransformationException;

	/**
	 * Calculates the base jaw opening width in [m] from the finger opening width
	 * regarding {@link Finger} offsets.
	 *
	 * @param fingerOpeningWidth
	 * @return
	 */
	RealtimeDouble getBaseJawOpeningWidthFrom(RealtimeDouble fingerOpeningWidth) throws TransformationException;

	/**
	 * Returns the transformation between base {@link Frame} and effector
	 * {@link Frame} of this tool.
	 *
	 * @return the transformation between base {@link Frame} and effector
	 *         {@link Frame}.
	 */
	Transformation getBaseToEffectorTransformation();

}
