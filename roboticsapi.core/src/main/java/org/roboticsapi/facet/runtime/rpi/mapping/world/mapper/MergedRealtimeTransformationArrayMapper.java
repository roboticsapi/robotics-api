/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.MergedRealtimeTransformationArray;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationArrayFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameArray;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameArraySet;

public class MergedRealtimeTransformationArrayMapper
		extends TypedRealtimeValueFragmentFactory<Transformation[], MergedRealtimeTransformationArray> {

	public MergedRealtimeTransformationArrayMapper() {
		super(MergedRealtimeTransformationArray.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation[]> createFragment(MergedRealtimeTransformationArray value)
			throws MappingException, RpiException {

		RealtimeTransformationArrayFragment ret = new RealtimeTransformationArrayFragment(value);
		FrameArray array = ret.add(new FrameArray(value.getSize()));
		OutPort out = array.getOutArray();

		for (int i = 0; i < value.getSize(); i++) {
			FrameArraySet set = ret.add(new FrameArraySet(value.getSize(), i));
			ret.connect(array.getOutArray(), set.getInArray());
			ret.addDependency(value.getTransformation(i), ret.addInPort("in" + i, set.getInValue()));
			out = set.getOutArray();
		}

		ret.defineResult(out);
		return ret;
	}
}
