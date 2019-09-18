/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Transformation;

/**
 * Sensor for arrays of {@link Transformation}s.
 */
public abstract class RealtimeTransformationArray extends RealtimeValue<Transformation[]> {

	@Override
	public final RealtimeTransformationArray substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeTransformationArray) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeTransformationArray performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	private final int size;

	/**
	 * Default constructor.
	 *
	 * @param runtime the sensor's runtime
	 * @param size    size of the array
	 */
	public RealtimeTransformationArray(final RoboticsRuntime runtime, int size) {
		super(runtime);
		this.size = size;
	}

	/**
	 * Default constructor.
	 *
	 * @param size   size of the array
	 * @param values the sensor's inner values
	 */
	public RealtimeTransformationArray(int size, RealtimeValue<?>... values) {
		super(values);
		this.size = size;
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
	 * Returns an array of {@link RealtimeTransformation}s.
	 *
	 * @return an array of {@link RealtimeTransformation}s
	 */
	public RealtimeTransformation[] getTransformations() {
		RealtimeTransformation[] ret = new RealtimeTransformation[size];
		for (int i = 0; i < size; i++) {
			ret[i] = getTransformation(i);
		}
		return ret;
	}

	/**
	 * Returns the {@link RealtimeTransformation} with the given <code>index</code>.
	 *
	 * @param index the index
	 * @return the sensor for the given <code>index</code>
	 */
	public RealtimeTransformation getTransformation(int index) {
		return new GetFromArrayRealtimeTransformation(this, index);
	}

	/**
	 * Constructs a new {@link RealtimeTransformationArray} from given single
	 * {@link RealtimeTransformation}s.
	 *
	 * @param transformations the sensors
	 * @return the array sensor
	 */
	public static RealtimeTransformationArray fromSensors(RealtimeTransformation... transformations) {
		return new MergedRealtimeTransformationArray(transformations);
	}

	@Override
	public RealtimeBoolean isNull() {
		return new RealtimeTransformationArrayIsNull(this);
	}

	@Override
	public RealtimeTransformationArray fromHistory(RealtimeDouble age, double maxAge) {
		// FIXME: implement
		return null;
	}

	public static RealtimeTransformationArray createFromConstant(Transformation[] transformations) {
		// FIXME: implement
		return null;
	}

}
