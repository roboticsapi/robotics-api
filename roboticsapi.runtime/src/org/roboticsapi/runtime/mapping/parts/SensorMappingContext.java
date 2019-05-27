/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;

public class SensorMappingContext {
	Map<Sensor<?>, SensorMapperResult<?>> knownSensors = new HashMap<Sensor<?>, SensorMapperResult<?>>();

	public <T> void addSensorResult(Sensor<T> sensor, SensorMapperResult<T> result) {
		knownSensors.put(sensor, result);
	}

	@SuppressWarnings("unchecked")
	public <T> SensorMapperResult<T> getSensorResult(Sensor<T> sensor) {
		return (SensorMapperResult<T>) knownSensors.get(sensor);
	}
}
