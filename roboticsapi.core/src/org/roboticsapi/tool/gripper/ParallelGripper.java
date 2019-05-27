/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.TransformationException;

/**
 * A parallel gripping device, i.e. the gripper has two parallel oriented
 * {@link GrippingFinger}.
 */
public interface ParallelGripper extends Gripper {

	Frame getContactFrame();

	/**
	 * Get the {@link Frame} that is aligned in the center between both base jaws.
	 * 
	 * @return a frame located in the center between both base jaws.
	 */
	Frame getFingerCenter();

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
	 * Returns the maximal possible opening width the {@link Finger}s in [m].
	 * 
	 * @return The maximal possible opening width in [m].
	 * @throws RoboticsException if the maximal opening width cannot be calculated.
	 */
	double getMaximalOpeningWidth() throws RoboticsException;

	/**
	 * Returns the current opening width (between the {@link Finger}s) in [m].
	 * 
	 * @return The current opening width between the gripping fingers.
	 * 
	 * @throws RoboticsException if no position sensor is available or the position
	 *                           sensor cannot be read.
	 */
	double getCurrentOpeningWidth() throws RoboticsException;

	/**
	 * Returns the current opening width (between the {@link BaseJaw}s) in [m].
	 * 
	 * @return The current opening width between the base jaws.
	 * 
	 * @throws RoboticsException if no position sensor is available or the position
	 *                           sensor cannot be read.
	 */
	double getCurrentBaseJawOpeningWidth() throws RoboticsException;

	/**
	 * Retrieves a {@link Sensor} measuring the opening width (between the
	 * {@link Finger}s) in [m].
	 * 
	 * @return a {@link Sensor} measuring distance between the gripping fingers or
	 *         <code>null</code> if not available.
	 */
	DoubleSensor getOpeningWidthSensor();

	/**
	 * Retrieves a {@link Sensor} measuring the opening width (between the
	 * {@link BaseJaw}s) in [m].
	 * 
	 * @return a {@link Sensor} measuring distance between the base jaws or
	 *         <code>null</code> if not available.
	 */
	DoubleSensor getBaseJawOpeningWidthSensor();

	/**
	 * Calculates the finger opening width in [m] from the base jaw opening width
	 * regarding {@link Finger} offsets.
	 * 
	 * @param baseJawOpeningWidth
	 * @return
	 */
	double getOpeningWidthFrom(double baseJawOpeningWidth) throws TransformationException;

	/**
	 * Calculates the base jaw opening width in [m] from the finger opening width
	 * regarding {@link Finger} offsets.
	 * 
	 * @param fingerOpeningWidth
	 * @return
	 */
	double getBaseJawOpeningWidthFrom(double fingerOpeningWidth) throws TransformationException;

}
