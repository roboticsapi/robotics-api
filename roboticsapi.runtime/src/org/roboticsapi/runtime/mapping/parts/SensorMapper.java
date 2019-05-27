/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.runtime.mapping.MapperInterface;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;

public interface SensorMapper<R extends RoboticsRuntime, T, S extends Sensor<T>> extends MapperInterface {
	SensorMapperResult<T> map(R runtime, S sensor, SensorMappingPorts ports, SensorMappingContext context)
			throws MappingException;
}
