/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * The TransformationSensorConnection is a Connection created from
 * TransformationSensors.
 */
public class TransformationSensorConnection extends DynamicConnection {

	private final TransformationSensor measuredSensor, sensor;
	private final VelocitySensor velSensor;
	private final VelocitySensor measuredVelSensor;

	/**
	 * Instantiates a new TransformationSensorConnection.
	 *
	 * @param sensor         the TransformationSensor determining this Connection's
	 *                       Transformation
	 * @param measuredSensor the TransformationSensor determining this Connection's
	 *                       measured Transformation
	 */
	public TransformationSensorConnection(TransformationSensor sensor, TransformationSensor measuredSensor,
			VelocitySensor velSensor, VelocitySensor measuredVelSensor) {
		super();
		this.sensor = sensor;
		this.measuredSensor = measuredSensor;

		this.velSensor = velSensor;
		this.measuredVelSensor = measuredVelSensor;
	}

	/**
	 * Instantiates a new TransformationSensorConnection using one
	 * TransformationSensor to determine target and measured Transformation.
	 *
	 * @param from   the 'from' end of the Connection
	 * @param to     the 'to' end of the Connection
	 * @param sensor the TransformationSensor determining this Connection's
	 *               Transformation and measured Transformation
	 */
	public TransformationSensorConnection(TransformationSensor sensor, VelocitySensor velSensor) {
		this(sensor, sensor, velSensor, velSensor);
	}

	@Override
	public RelationSensor getRelationSensor() {
		return new RelationSensor(getTransformationSensor(), getFrom(), getTo());
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return new RelationSensor(getMeasuredTransformationSensor(), getFrom(), getTo());
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return velSensor;
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return measuredVelSensor;
	}

	/**
	 * Gets the {@link TransformationSensor} that determines this Relation's
	 * commanded Transformation.
	 *
	 * 'Commanded' in this context means the desired value that the Transformation
	 * should have. In the context of actuators, this value can differ from the
	 * actual ('measured') Transformation value, which is obtainable by
	 * {@link TransformationSensorConnection#getMeasuredTransformationSensor()}.
	 *
	 * @return the TransformationSensor
	 */
	public TransformationSensor getTransformationSensor() {
		return sensor;
	}

	/**
	 * Gets the {@link TransformationSensor} that determines this Relation's
	 * measured Transformation.
	 *
	 * 'Measured' in this context means the actual value that the Transformation
	 * has. In the context of actuators, this value can differ from the desired
	 * ('commanded') Transformation value, which is obtainable by
	 * {@link TransformationSensorConnection#getTransformationSensor()}.
	 *
	 * @return the TransformationSensor
	 */
	public TransformationSensor getMeasuredTransformationSensor() {
		return measuredSensor;
	}
}
