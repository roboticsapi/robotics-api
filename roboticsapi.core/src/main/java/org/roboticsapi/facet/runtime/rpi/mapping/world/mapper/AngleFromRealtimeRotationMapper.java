/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimerotation.GetAngleFromRotationRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationToAxisAngle;

public class AngleFromRealtimeRotationMapper
		extends TypedRealtimeValueFragmentFactory<Double, GetAngleFromRotationRealtimeDouble> {

	public AngleFromRealtimeRotationMapper() {
		super(GetAngleFromRotationRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(GetAngleFromRotationRealtimeDouble value)
			throws MappingException, RpiException {
		RotationToAxisAngle converter = new RotationToAxisAngle();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, converter.getOutAngle());
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue", converter.getInValue()));
		return ret;
	}
}
