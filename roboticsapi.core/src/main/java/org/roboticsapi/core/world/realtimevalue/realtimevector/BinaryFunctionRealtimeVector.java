/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.world.Vector;

/**
 * Abstract implementation for a {@link RealtimeVector} that is composed of two
 * other {@link RealtimeVector}s.
 */
public abstract class BinaryFunctionRealtimeVector extends RealtimeVector {

	/**
	 * The first value.
	 */
	private final RealtimeVector value1;

	/**
	 * The second value.
	 */
	private final RealtimeVector value2;

	/**
	 * Constructor.
	 *
	 * @param value1 the first value.
	 * @param value2 the second value.
	 * @throws IllegalArgumentException if the given sensor have different
	 *                                  {@link RoboticsRuntime}s.
	 */
	BinaryFunctionRealtimeVector(RealtimeVector value1, RealtimeVector value2) {
		super(value1, value2);

		this.value1 = value1;
		this.value2 = value2;
	}

	/**
	 * Returns the first value.
	 *
	 * @return the first value.
	 */
	public final RealtimeVector getFirstValue() {
		return value1;
	}

	/**
	 * Returns the second value.
	 *
	 * @return the second value.
	 */
	public final RealtimeVector getSecondValue() {
		return value2;
	}

	@Override
	protected final Vector calculateCheapValue() {
		Vector v1 = value1.getCheapValue();
		if (v1 == null) {
			return null;
		}

		Vector v2 = value2.getCheapValue();
		if (v2 == null) {
			return null;
		}
		return computeCheapValue(v1, v2);
	}

	/**
	 * Computes the cheap value for the composed {@link RealtimeVector}.
	 *
	 * @param value1 the cheap value of the first {@link RealtimeVector}
	 * @param value2 the cheap value of the second {@link RealtimeVector}
	 * @return the computed cheap value.
	 */
	protected abstract Vector computeCheapValue(Vector value1, Vector value2);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && value1.equals(((BinaryFunctionRealtimeVector) obj).value1)
				&& value2.equals(((BinaryFunctionRealtimeVector) obj).value2);
	}

	@Override
	public final int hashCode() {
		return classHash(value1, value2);
	}

	@Override
	public final boolean isAvailable() {
		return areAvailable(value1, value2);
	}

}
