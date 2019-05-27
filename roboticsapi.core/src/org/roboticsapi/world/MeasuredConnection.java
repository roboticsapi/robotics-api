/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class MeasuredConnection extends Placement {

	private final RelationSensor measuredRelationSensor;
	private final VelocitySensor measuredVelocitySensor;

	public MeasuredConnection(RelationSensor measuredRelationSensor, VelocitySensor measuredVelocitySensor)
			throws SensorReadException {
		super(checkRelationSensorOnNotNull(measuredRelationSensor).getTransformationSensor().getCurrentValue());
		this.measuredRelationSensor = measuredRelationSensor;
		this.measuredVelocitySensor = measuredVelocitySensor;
	}

	private static RelationSensor checkRelationSensorOnNotNull(RelationSensor sensor) {
		if (sensor == null) {
			throw new IllegalArgumentException("RelationSensor object may not be null.");
		}

		return sensor;
	}

	@Override
	public RelationSensor getRelationSensor() {
		return getMeasuredRelationSensor();
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return measuredRelationSensor;
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return getMeasuredVelocitySensor();
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return measuredVelocitySensor;
	}

}
