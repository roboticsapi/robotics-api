/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.world.Transformation;

public class MergedRealtimeTransformationArray extends RealtimeTransformationArray {

	private final RealtimeTransformation[] values;

	MergedRealtimeTransformationArray(RealtimeTransformation[] transformations) {
		super(transformations.length, transformations);
		this.values = transformations;
	}

	@Override
	public RealtimeTransformation[] getTransformations() {
		return values;
	}

	@Override
	public RealtimeTransformation getTransformation(int index) {
		return values[index];
	}

	@Override
	protected Transformation[] calculateCheapValue() {
		Transformation[] ret = new Transformation[values.length];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = values[i].getCheapValue();

			if (ret[i] == null) {
				return null;
			}
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && values.equals(((MergedRealtimeTransformationArray) obj).values);
	}

	@Override
	public int hashCode() {
		return classHash((Object) values);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(values);
	}

	@Override
	public String toString() {
		String ret = "";
		for (RealtimeTransformation sensor : values) {
			ret += sensor + ", ";
		}
		if (values.length != 0) {
			ret = ret.substring(0, ret.length() - 3);
		}
		return "array(" + ret + ")";
	}

}
