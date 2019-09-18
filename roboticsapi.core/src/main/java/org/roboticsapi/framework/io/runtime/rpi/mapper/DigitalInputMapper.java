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
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;
import org.roboticsapi.framework.io.runtime.rpi.DigitalInputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.primitives.InBool;

public class DigitalInputMapper
		extends TypedRealtimeValueFragmentFactory<Boolean, DigitalInputGenericDriver.DigitalInputRealtimeBoolean> {

	public DigitalInputMapper() {
		super(DigitalInputGenericDriver.DigitalInputRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(DigitalInputGenericDriver.DigitalInputRealtimeBoolean value)
			throws MappingException, RpiException {

		return new RealtimeBooleanFragment(value,
				new InBool(value.getDriver().getRpiDeviceName(), value.getDriver().getNumber()).getOutIO());
	}

}
