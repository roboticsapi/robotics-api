/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.world.Transformation;

/**
 * Sensor for arrays of {@link Transformation}s.
 */
public abstract class TransformationArraySensor extends Sensor<Transformation[]> {

	private final int size;

	/**
	 * Default constructor.
	 * 
	 * @param runtime the sensor's runtime
	 * @param size    size of the array
	 */
	public TransformationArraySensor(final RoboticsRuntime runtime, int size) {
		super(runtime);
		this.size = size;
	}

	@Override
	protected Transformation[] getDefaultValue() {
		return new Transformation[size];
	}

	/**
	 * Returns the size of the array sensor.
	 * 
	 * @return the size of the array
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns an array of {@link TransformationSensor}s.
	 * 
	 * @return an array of {@link TransformationSensor}s
	 */
	public TransformationSensor[] getSensors() {
		TransformationSensor[] ret = new TransformationSensor[size];
		for (int i = 0; i < size; i++) {
			ret[i] = getSensor(i);
		}
		return ret;
	}

	/**
	 * Returns the {@link TransformationSensor} with the given <code>index</code>.
	 * 
	 * @param index the index
	 * @return the sensor for the given <code>index</code>
	 */
	public TransformationSensor getSensor(int index) {
		return new FromArrayTransformationSensor(this, index);
	}

	/**
	 * Constructs a new {@link TransformationArraySensor} from given single
	 * {@link TransformationSensor}s.
	 * 
	 * @param sensors the sensors
	 * @return the array sensor
	 */
	public static TransformationArraySensor fromSensors(TransformationSensor... sensors) {
		return new MergedTransformationArraySensor(sensors);
	}

}
