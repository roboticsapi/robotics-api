/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.XYZToRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.VectorFromXYZ;

public class RealtimeVectorFromComponentsMapper extends TypedRealtimeValueFragmentFactory<Vector, XYZToRealtimeVector> {

	public RealtimeVectorFromComponentsMapper() {
		super(XYZToRealtimeVector.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(XYZToRealtimeVector value)
			throws MappingException, RpiException {
		VectorFromXYZ vec = new VectorFromXYZ();
		RealtimeVectorFragment ret = new RealtimeVectorFragment(value, vec.getOutValue());
		ret.addDependency(value.getXComponent(), ret.addInPort("inX", vec.getInX()));
		ret.addDependency(value.getYComponent(), ret.addInPort("inY", vec.getInY()));
		ret.addDependency(value.getZComponent(), ret.addInPort("inZ", vec.getInZ()));
		return ret;
	}

}
