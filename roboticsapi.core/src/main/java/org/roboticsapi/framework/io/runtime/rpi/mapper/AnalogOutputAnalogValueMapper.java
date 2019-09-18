/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.io.runtime.rpi.AnalogOutputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.AnalogOutputGenericDriver.AnalogOutputRealtimeDouble;
import org.roboticsapi.framework.io.runtime.rpi.primitives.OutReal;
import org.roboticsapi.framework.io.runtime.rpi.primitives.OutRealSensor;

public class AnalogOutputAnalogValueMapper
		extends TypedRealtimeValueFragmentFactory<Double, AnalogOutputGenericDriver.AnalogOutputRealtimeDouble>
		implements ActuatorDriverMapper<AnalogOutputGenericDriver, AnalogValueActionResult> {

	public AnalogOutputAnalogValueMapper() {
		super(AnalogOutputGenericDriver.AnalogOutputRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(AnalogOutputRealtimeDouble value)
			throws MappingException, RpiException {

		return new RealtimeDoubleFragment(value,
				new OutRealSensor(value.getDriver().getRpiDeviceName(), value.getDriver().getNumber()).getOutIO());
	}

	@Override
	public RealtimeValueConsumerFragment map(AnalogOutputGenericDriver actuatorDriver,
			AnalogValueActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		OutReal outReal = new OutReal(actuatorDriver.getRpiDeviceName(), actuatorDriver.getNumber());
		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, RealtimeBoolean.TRUE, outReal);
		ret.addDependency(actionResult.getAnalogValue(), ret.addInPort("inValue", outReal.getInIO()));
		ret.addException(ConcurrentAccessException.class, RealtimeBoolean.FALSE);
		ret.addException(ActuatorNotOperationalException.class, RealtimeBoolean.FALSE);
		return ret;
	}

}
