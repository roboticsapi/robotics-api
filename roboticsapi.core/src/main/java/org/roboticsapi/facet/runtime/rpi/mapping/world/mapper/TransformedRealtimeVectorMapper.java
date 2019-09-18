/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.TransformedRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class TransformedRealtimeVectorMapper extends TypedRealtimeValueAliasFactory<Vector, TransformedRealtimeVector> {

	public TransformedRealtimeVectorMapper() {
		super(TransformedRealtimeVector.class);
	}

	@Override
	protected RealtimeValue<Vector> createAlias(TransformedRealtimeVector value) {
		return value.getVector().transform(value.getTransformation().getRotation())
				.add(value.getTransformation().getTranslation());
	}
}
