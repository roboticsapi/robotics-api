/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiCommandHandle;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.PersistedRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationInterNetcommIn;

public class PersistedRealtimeRotationMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, PersistedRealtimeRotation> {

	public PersistedRealtimeRotationMapper() {
		super(PersistedRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(PersistedRealtimeRotation value)
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

		RotationInterNetcommIn netcomm = new RotationInterNetcommIn("", name, key);
		return new RealtimeRotationFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated());
	}
}
