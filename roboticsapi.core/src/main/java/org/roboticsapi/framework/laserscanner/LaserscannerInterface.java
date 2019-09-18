/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.laserscanner;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;

/**
 * Sensor interface to access laser scanner range measurements
 */
public interface LaserscannerInterface extends SensorInterface {

	/**
	 * Returns the number of points measured
	 */
	int getPointCount();

	/**
	 * Returns the angle of the first measurement (index 0) [rad]
	 */
	double getStartAngle();

	/**
	 * Returns the angle of the last measurement (index getPointCount() - 1) [rad]
	 */
	double getEndAngle();

	/**
	 * Returns a RealtimeDoubleArray representing the range readings in the given
	 * angle range [m]
	 */
	RealtimeDoubleArray getRanges();

}
