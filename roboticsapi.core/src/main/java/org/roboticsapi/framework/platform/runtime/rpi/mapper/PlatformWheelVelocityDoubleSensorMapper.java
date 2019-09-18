/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.runtime.rpi.mapper;

import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.platform.runtime.rpi.PlatformWheelVelocityRealtimeDouble;
import org.roboticsapi.framework.platform.runtime.rpi.primitives.WheelMonitor;

public class PlatformWheelVelocityDoubleSensorMapper
		extends TypedRealtimeValueFragmentFactory<Double, PlatformWheelVelocityRealtimeDouble> {

	public PlatformWheelVelocityDoubleSensorMapper() {
		super(PlatformWheelVelocityRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(PlatformWheelVelocityRealtimeDouble value)
			throws MappingException, RpiException {
		return new RealtimeDoubleFragment(value,
				new WheelMonitor(value.getDriver().getRpiDeviceName(), value.getWheelNumber()).getOutVel());
	}
}
