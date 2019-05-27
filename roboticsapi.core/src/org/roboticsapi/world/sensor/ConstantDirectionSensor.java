/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Vector;

public final class ConstantDirectionSensor extends DirectionSensor {

	private final Direction value;

	public ConstantDirectionSensor(Direction direction) {
		super(VectorSensor.fromConstant(checkDirectionOnNotNull(direction).getValue()),
				checkDirectionOnNotNull(direction).getOrientation());
		value = direction;
	}

	private static Direction checkDirectionOnNotNull(Direction direction) {
		if (direction == null) {
			throw new IllegalArgumentException("Argument direction may not be null.");
		}

		return direction;
	}

	public ConstantDirectionSensor(Orientation orientation, Vector value) {
		this(new Direction(orientation, value));
	}

	public Direction getConstantValue() {
		return value;
	}

	@Override
	protected Direction calculateCheapValue() {
		return getConstantValue();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value.equals(((ConstantDirectionSensor) obj).value);
	}

	@Override
	public int hashCode() {
		return classHash(value);
	}

}
