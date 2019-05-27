/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Transformation;

/**
 * Transformation sensor which extracts a single {@link TransformationSensor}
 * from a {@link TransformationArraySensor}.
 */
public final class FromArrayTransformationSensor extends TransformationSensor {

	/**
	 * The inner array sensor.
	 */
	private final TransformationArraySensor inner;

	/**
	 * The index.
	 */
	private final int index;

	/**
	 * Default constructor.
	 *
	 * @param arraySensor the array sensor
	 * @param index       the index
	 *
	 * @throws ArrayIndexOutOfBoundsException if <code>index</code> is greater or
	 *                                        equal to the size of the array.
	 */
	public FromArrayTransformationSensor(TransformationArraySensor arraySensor, int index) {
		super(selectRuntime(arraySensor));
		addInnerSensors(arraySensor);

		if (index >= arraySensor.getSize()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		this.inner = arraySensor;
		this.index = index;
	}

	/**
	 * Returns the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Return the inner array sensor.
	 *
	 * @return the array sensor.
	 */
	public TransformationArraySensor getTransformationArray() {
		return this.inner;
	}

	@Override
	public boolean isAvailable() {
		return inner.isAvailable();
	}

	@Override
	protected final Transformation calculateCheapValue() {
		Transformation[] value = inner.getCheapValue();

		if (value == null) {
			return null;
		}
		return value[index];
	}

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && inner.equals(((FromArrayTransformationSensor) obj).inner)
				&& index == ((FromArrayTransformationSensor) obj).index;
	}

	@Override
	public final int hashCode() {
		return classHash(inner, index);
	}

}
