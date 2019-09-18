/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;

/**
 * Abstract implementation for a {@link RealtimeDouble} that is composed of two
 * other {@link RealtimeDouble}s.
 */
public abstract class BinaryFunctionRealtimeDouble extends RealtimeDouble {

	/**
	 * The first value.
	 */
	private final RealtimeDouble value1;

	/**
	 * The second value.
	 */
	private final RealtimeDouble value2;

	/**
	 * Constructor.
	 *
	 * @param value1 the first value.
	 * @param value2 the second value.
	 * @throws IllegalArgumentException if the given {@link RealtimeValue}s have
	 *                                  different {@link RoboticsRuntime}s.
	 */
	public BinaryFunctionRealtimeDouble(RealtimeDouble value1, RealtimeDouble value2) {
		super(value1, value2);

		this.value1 = value1;
		this.value2 = value2;
	}

	/**
	 * Returns the first {@link RealtimeDouble}.
	 *
	 * @return the first value.
	 */
	public final RealtimeDouble getFirstValue() {
		return value1;
	}

	/**
	 * Returns the second {@link RealtimeDouble}.
	 *
	 * @return the second value.
	 */
	public final RealtimeDouble getSecondValue() {
		return value2;
	}

	@Override
	protected final Double calculateCheapValue() {
		Double value1 = this.value1.getCheapValue();
		if (value1 == null) {
			return null;
		}

		Double value2 = this.value2.getCheapValue();
		if (value2 == null) {
			return null;
		}
		return computeCheapValue(value1, value2);
	}

	/**
	 * Computes the cheap value for the composed {@link RealtimeDouble}.
	 *
	 * @param value1 the cheap value of the first {@link RealtimeDouble}
	 * @param value2 the cheap value of the second {@link RealtimeDouble}
	 * @return the computed cheap value.
	 */
	protected abstract Double computeCheapValue(double value1, double value2);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && value1.equals(((BinaryFunctionRealtimeDouble) obj).value1)
				&& value2.equals(((BinaryFunctionRealtimeDouble) obj).value2);
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
