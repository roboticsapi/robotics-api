/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimevector.GetFromVectorRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.VectorToXYZ;

public class RealtimeVectorComponentMapper
		extends TypedRealtimeValueFragmentFactory<Double, GetFromVectorRealtimeDouble> {

	public RealtimeVectorComponentMapper() {
		super(GetFromVectorRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(GetFromVectorRealtimeDouble value)
			throws MappingException, RpiException {
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value);
		VectorToXYZ vec = ret.add(new VectorToXYZ());
		ret.addDependency(value.getVector(), ret.addInPort("inValue", vec.getInValue()));
		switch (value.getComponent()) {
		case X:
			ret.defineResult(vec.getOutX());
			break;
		case Y:
			ret.defineResult(vec.getOutY());
			break;
		case Z:
			ret.defineResult(vec.getOutZ());
			break;
		}
		return ret;
	}
}
