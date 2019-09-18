/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVectorAtTime;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.VectorAtTime;

public class RealtimeVectorAtTimeMapper extends TypedRealtimeValueFragmentFactory<Vector, RealtimeVectorAtTime> {

	public RealtimeVectorAtTimeMapper() {
		super(RealtimeVectorAtTime.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(RealtimeVectorAtTime value)
			throws MappingException, RpiException {
		VectorAtTime vector = new VectorAtTime(value.getMaxAge());
		RealtimeVectorFragment ret = new RealtimeVectorFragment(value, vector.getOutValue());
		ret.addDependency(value.getVector(), ret.addInPort("inValue", vector.getInValue()));
		ret.addDependency(value.getAge(), ret.addInPort("inAge", vector.getInAge()));
		return ret;
	}
}
