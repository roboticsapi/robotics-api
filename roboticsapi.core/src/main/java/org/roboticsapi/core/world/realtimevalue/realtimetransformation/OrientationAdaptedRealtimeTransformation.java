/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;

public class OrientationAdaptedRealtimeTransformation extends RealtimeTransformation {

	private final RealtimeTransformation otherTansformation;
	private final RealtimeRotation orientationChange;

	/**
	 * Creates a RealtimeTransformation with modified orientation
	 *
	 * @param tansformation     transformation to modify
	 * @param orientationChange rotation of the new orientation relative to the old
	 *                          one
	 */
	OrientationAdaptedRealtimeTransformation(RealtimeTransformation tansformation, RealtimeRotation orientationChange) {
		super(tansformation, orientationChange);
		this.otherTansformation = tansformation;
		this.orientationChange = orientationChange;
	}

	public RealtimeRotation getOrientationChange() {
		return orientationChange;
	}

	public RealtimeTransformation getOtherTansformation() {
		return otherTansformation;
	}

	@Override
	public boolean isAvailable() {
		return orientationChange.isAvailable() && otherTansformation.isAvailable();
	}

	@Override
	protected Transformation calculateCheapValue() {
		Transformation other = otherTansformation.getCheapValue();
		Rotation ori = orientationChange.getCheapValue();

		return other == null || ori == null ? null
				: new Transformation(ori.invert(), Vector.getNullVector()).multiply(other)
						.multiply(new Transformation(ori, Vector.getNullVector()));
	}

}
