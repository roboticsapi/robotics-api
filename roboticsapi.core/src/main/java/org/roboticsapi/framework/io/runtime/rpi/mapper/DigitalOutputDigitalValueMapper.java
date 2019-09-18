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
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;
import org.roboticsapi.framework.io.runtime.rpi.DigitalOutputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.DigitalOutputGenericDriver.DigitalOutputRealtimeBoolean;
import org.roboticsapi.framework.io.runtime.rpi.primitives.OutBool;
import org.roboticsapi.framework.io.runtime.rpi.primitives.OutBoolSensor;

public class DigitalOutputDigitalValueMapper
		extends TypedRealtimeValueFragmentFactory<Boolean, DigitalOutputGenericDriver.DigitalOutputRealtimeBoolean>
		implements ActuatorDriverMapper<DigitalOutputGenericDriver, DigitalValueActionResult> {

	public DigitalOutputDigitalValueMapper() {
		super(DigitalOutputGenericDriver.DigitalOutputRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(DigitalOutputRealtimeBoolean value)
			throws MappingException, RpiException {

		return new RealtimeBooleanFragment(value,
				new OutBoolSensor(value.getDriver().getRpiDeviceName(), value.getDriver().getNumber()).getOutIO());
	}

	@Override
	public RealtimeValueConsumerFragment map(DigitalOutputGenericDriver actuatorDriver,
			DigitalValueActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		OutBool outBool = new OutBool(actuatorDriver.getRpiDeviceName(), actuatorDriver.getNumber());
		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, RealtimeBoolean.TRUE, outBool);
		ret.addDependency(actionResult.getDigitalValue(), ret.addInPort("inValue", outBool.getInIO()));
		ret.addException(ConcurrentAccessException.class, RealtimeBoolean.FALSE);
		ret.addException(ActuatorNotOperationalException.class, RealtimeBoolean.FALSE);
		return ret;
	}

}
