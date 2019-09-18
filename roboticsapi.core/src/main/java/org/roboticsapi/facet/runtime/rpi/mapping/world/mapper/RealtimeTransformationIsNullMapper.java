/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformationIsNull;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameIsNull;

public class RealtimeTransformationIsNullMapper
		extends TypedRealtimeValueFragmentFactory<Boolean, RealtimeTransformationIsNull> {

	public RealtimeTransformationIsNullMapper() {
		super(RealtimeTransformationIsNull.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(RealtimeTransformationIsNull value)
			throws MappingException, RpiException {

		FrameIsNull isNull = new FrameIsNull();
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, isNull.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inTransformation", isNull.getInValue()));
		return ret;
	}
}
