/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.fragment.AdaptVelocityOrientationFragment;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.OrientationAdaptedVelocitySensor;

public class OrientationAdaptedVelocitySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Twist, OrientationAdaptedVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, OrientationAdaptedVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		SensorMapperResult<Twist> other = runtime.getMapperRegistry().mapSensor(runtime, sensor.getOtherSensor(), null,
				context);

		Frame newOrientationFrame = sensor.getNewOrientation().getReferenceFrame().plus(new Vector(),
				sensor.getNewOrientation().getRotation());
		Frame oldOrientationFrame = sensor.getOtherSensor().getOrientation().getReferenceFrame().plus(new Vector(),
				sensor.getOtherSensor().getOrientation().getRotation());

		SensorMapperResult<Transformation> transRot = runtime.getMapperRegistry().mapSensor(runtime,
				newOrientationFrame.getRelationSensor(oldOrientationFrame), null, context);

		AdaptVelocityOrientationFragment fragment = new AdaptVelocityOrientationFragment("Orientation adaptation",
				other.getSensorPort(), transRot.getSensorPort(), sensor.getNewOrientation());

		fragment.add(other.getNetFragment());
		fragment.add(transRot.getNetFragment());

		return new VelocitySensorMapperResult(fragment, fragment.getOrientationAdaptedVelocityPort());
	}

}
