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
import org.roboticsapi.facet.runtime.rpi.mapping.world.PersistedRealtimeTransformationArray;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationArrayFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameArrayInterNetcommIn;

public class PersistedRealtimeTransformationArrayMapper
		extends TypedRealtimeValueFragmentFactory<Transformation[], PersistedRealtimeTransformationArray> {

	public PersistedRealtimeTransformationArrayMapper() {
		super(PersistedRealtimeTransformationArray.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation[]> createFragment(PersistedRealtimeTransformationArray value)
			throws MappingException, RpiException {
		CommandHandle handle = value.getCommand().getCommandHandle();
		String name = "";
		String key = value.getKey().getName();
		if (handle instanceof RpiCommandHandle) {
			NetHandle net = ((RpiCommandHandle) handle).getNetHandle();
			name = net.getName();
		}
		if (name.isEmpty()) {
			throw new MappingException("Source net not found.");
		}

		FrameArrayInterNetcommIn netcomm = new FrameArrayInterNetcommIn("", name, key);
		return new RealtimeTransformationArrayFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated());
	}
}
