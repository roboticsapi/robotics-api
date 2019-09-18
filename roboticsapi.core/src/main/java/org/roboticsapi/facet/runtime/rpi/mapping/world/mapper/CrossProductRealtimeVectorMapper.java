/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.CrossProductRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.VectorCross;

public class CrossProductRealtimeVectorMapper
		extends TypedRealtimeValueFragmentFactory<Vector, CrossProductRealtimeVector> {

	public CrossProductRealtimeVectorMapper() {
		super(CrossProductRealtimeVector.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(CrossProductRealtimeVector value)
			throws MappingException, RpiException {
		VectorCross cross = new VectorCross();
		RealtimeVectorFragment ret = new RealtimeVectorFragment(value, cross.getOutValue());
		ret.addDependency(value.getFirstValue(), ret.addInPort("inFirst", cross.getInFirst()));
		ret.addDependency(value.getSecondValue(), ret.addInPort("inSecond", cross.getInSecond()));
		return ret;
	}

}
