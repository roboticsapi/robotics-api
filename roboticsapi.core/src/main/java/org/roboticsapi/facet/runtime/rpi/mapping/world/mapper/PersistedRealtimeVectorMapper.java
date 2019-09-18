/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiCommandHandle;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.PersistedRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.VectorInterNetcommIn;

public class PersistedRealtimeVectorMapper extends TypedRealtimeValueFragmentFactory<Vector, PersistedRealtimeVector> {

	public PersistedRealtimeVectorMapper() {
		super(PersistedRealtimeVector.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(PersistedRealtimeVector value)
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

		VectorInterNetcommIn netcomm = new VectorInterNetcommIn("", name, key);
		return new RealtimeVectorFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated());
	}
}
