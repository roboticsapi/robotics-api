/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiCommandHandle;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.PersistedRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameInterNetcommIn;

public class PersistedRealtimeTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, PersistedRealtimeTransformation> {

	public PersistedRealtimeTransformationMapper() {
		super(PersistedRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(PersistedRealtimeTransformation value)
			throws MappingException, RpiException {
		CommandHandle handle = value.getCommand().getCommandHandle();
		String name = "";
		String key = value.getKey();
		if (handle instanceof RpiCommandHandle) {
			NetHandle net = ((RpiCommandHandle) handle).getNetHandle();
			name = net.getName();
		}
		if (name.isEmpty()) {
			throw new MappingException("Source net not found.");
		}

		FrameInterNetcommIn netcomm = new FrameInterNetcommIn("", name, key);
		return new RealtimeTransformationFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated());
	}
}
