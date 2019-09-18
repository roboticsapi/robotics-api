/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.mapper;

import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.io.runtime.rpi.AnalogInputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.AnalogInputGenericDriver.AnalogInputRealtimeDouble;
import org.roboticsapi.framework.io.runtime.rpi.primitives.InReal;

public class AnalogInputMapper
		extends TypedRealtimeValueFragmentFactory<Double, AnalogInputGenericDriver.AnalogInputRealtimeDouble> {

	public AnalogInputMapper() {
		super(AnalogInputGenericDriver.AnalogInputRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(AnalogInputRealtimeDouble value)
			throws MappingException, RpiException {

		return new RealtimeDoubleFragment(value,
				new InReal(value.getDriver().getRpiDeviceName(), value.getDriver().getNumber()).getOutIO());
	}
}
