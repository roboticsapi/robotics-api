/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RotatedRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.VectorRotate;

public class RotatedRealtimeVectorMapper extends TypedRealtimeValueFragmentFactory<Vector, RotatedRealtimeVector> {

	public RotatedRealtimeVectorMapper() {
		super(RotatedRealtimeVector.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(RotatedRealtimeVector value)
			throws MappingException, RpiException {
		VectorRotate rot = new VectorRotate();
		RealtimeVectorFragment ret = new RealtimeVectorFragment(value, rot.getOutValue());
		ret.addDependency(value.getRotation(), ret.addInPort("inRotation", rot.getInRot()));
		ret.addDependency(value.getVector(), ret.addInPort("inValue", rot.getInValue()));
		return ret;
	}
}
