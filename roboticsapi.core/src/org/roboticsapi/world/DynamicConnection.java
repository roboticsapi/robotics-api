/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.SensorReadException;

/**
 * A Robotics API world model dynamic connection (e.g. representing a device)
 */
public abstract class DynamicConnection extends Connection {

	/** transformation between the frames */

	/**
	 * Creates a new undeletable dynamic connection
	 * 
	 */
	protected DynamicConnection() {
		super();
	}

	/**
	 * Creates a new dynamic connection
	 * 
	 * @param deletable whether the relation is deletable
	 */
	// protected DynamicConnection(boolean deletable) {
	// super(deletable);
	// }

	/**
	 * Retrieves the current transformation between source and destination frame
	 * 
	 * @return the current transformation
	 * @throws RoboticsException
	 */
	@Override
	public Transformation getTransformation() throws TransformationException {
		if (getRelationSensor() == null) {
			throw new UnsupportedOperationException(
					"Method getRelationSensor() returned null. This is not allowed here. Check implementation.");
		}

		try {
			return getRelationSensor().getCurrentValue();
		} catch (SensorReadException e) {
			throw new TransformationException("Could not read Transformation", e);
		}
	}

	@Override
	public Transformation getMeasuredTransformation() throws TransformationException {
		if (getMeasuredRelationSensor() == null) {
			throw new UnsupportedOperationException(
					"Method getMeasuredRelationSensor() returned null. This is not allowed here. Check implementation.");
		}

		try {
			return getMeasuredRelationSensor().getCurrentValue();
		} catch (SensorReadException e) {
			throw new TransformationException("Could not read Transformation", e);
		}
	}

}
