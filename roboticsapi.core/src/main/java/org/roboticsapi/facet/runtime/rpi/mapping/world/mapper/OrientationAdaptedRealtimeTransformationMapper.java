/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.OrientationAdaptedRealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class OrientationAdaptedRealtimeTransformationMapper
		extends TypedRealtimeValueAliasFactory<Transformation, OrientationAdaptedRealtimeTransformation> {

	public OrientationAdaptedRealtimeTransformationMapper() {
		super(OrientationAdaptedRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValue<Transformation> createAlias(OrientationAdaptedRealtimeTransformation value) {

		return RealtimeTransformation.createFromRotation(value.getOrientationChange().invert())
				.multiply(value.getOtherTansformation())
				.multiply(RealtimeTransformation.createFromRotation(value.getOrientationChange()));

	}
}
