/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.MapperInterface;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public interface ActuatorDriverMapper<R extends RoboticsRuntime, AD extends ActuatorDriver, AR extends ActionResult>
		extends MapperInterface {
	ActuatorDriverMapperResult map(R runtime, AD actuatorDriver, AR actionResult, DeviceParameterBag parameters,
			DeviceMappingPorts ports) throws MappingException, RPIException;
}
