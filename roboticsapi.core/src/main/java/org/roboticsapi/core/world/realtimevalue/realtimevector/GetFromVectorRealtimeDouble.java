/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Vector;

/**
 * Extracts a certain component from a RealtimeVector.
 */
public final class GetFromVectorRealtimeDouble extends RealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new GetFromVectorRealtimeDouble(vector.substitute(substitutionMap), component);
	}

	/**
	 * Transformation Components.
	 */
	public enum VectorComponent {
		X, Y, Z
	}

	private final VectorComponent component;
	private final RealtimeVector vector;

	/**
	 * Instantiates a new TransformationComponentSensor.
	 *
	 * @param vector    the TransformationSensor to extract a component from
	 * @param component the component to extract
	 */
	GetFromVectorRealtimeDouble(RealtimeVector vector, VectorComponent component) {
		super(vector);
		this.vector = vector;
		this.component = component;
	}

	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	public VectorComponent getComponent() {
		return component;
	}

	/**
	 * Gets the original VectorSensor.
	 *
	 * @return the sensor
	 */
	public RealtimeVector getVector() {
		return vector;
	}

	@Override
	protected Double calculateCheapValue() {
		Vector curr = getVector().getCheapValue();
		if (curr == null) {
			return null;
		}
		if (component == VectorComponent.X) {
			return curr.getX();
		} else if (component == VectorComponent.Y) {
			return curr.getY();
		} else {
			return curr.getZ();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && vector.equals(((GetFromVectorRealtimeDouble) obj).vector)
				&& component == ((GetFromVectorRealtimeDouble) obj).component;
	}

	@Override
	public int hashCode() {
		return classHash(vector, component);
	}

	@Override
	public boolean isAvailable() {
		return vector.isAvailable();
	}

	@Override
	public String toString() {
		return vector + "." + component;
	}
}
