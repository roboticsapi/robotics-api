/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.world.Transformation;

/**
 * Transformation sensor which extracts a single {@link RealtimeTransformation}
 * from a {@link RealtimeTransformationArray}.
 */
public final class GetFromArrayRealtimeTransformation extends RealtimeTransformation {

	/**
	 * The inner array sensor.
	 */
	private final RealtimeTransformationArray inner;

	/**
	 * The index.
	 */
	private final int index;

	/**
	 * Default constructor.
	 *
	 * @param transformations the array sensor
	 * @param index           the index
	 *
	 * @throws ArrayIndexOutOfBoundsException if <code>index</code> is greater or
	 *                                        equal to the size of the array.
	 */
	GetFromArrayRealtimeTransformation(RealtimeTransformationArray transformations, int index) {
		super(transformations);

		if (index >= transformations.getSize()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		this.inner = transformations;
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
	public RealtimeTransformationArray getTransformationArray() {
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
		return classEqual(obj) && inner.equals(((GetFromArrayRealtimeTransformation) obj).inner)
				&& index == ((GetFromArrayRealtimeTransformation) obj).index;
	}

	@Override
	public final int hashCode() {
		return classHash(inner, index);
	}

}
