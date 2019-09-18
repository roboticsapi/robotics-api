/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public interface ActuatorDriverMapper<AD extends ActuatorDriver, AR extends ActionResult> {
	RealtimeValueConsumerFragment map(AD actuatorDriver, AR actionResult, DeviceParameterBag parameters,
			MapperRegistry registry, RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException;
}
