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
import org.roboticsapi.runtime.world.fragment.AdaptVelocityPivotPointFragment;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.PivotAdaptedVelocitySensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class PivotAdaptedVelocitySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Twist, PivotAdaptedVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, PivotAdaptedVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		VelocitySensor old = sensor.getOtherSensor();
		SensorMapperResult<Twist> other = runtime.getMapperRegistry().mapSensor(runtime, old, null, context);

		Point newPivot = sensor.getPivotPoint();
		Point oldPivot = old.getPivotPoint();
		Orientation ori = old.getOrientation();

		Frame newPivotFrame = newPivot.getReferenceFrame().plus(newPivot.getVector());
		Frame oldPivotFrame = oldPivot.getReferenceFrame().plus(oldPivot.getVector());

		Frame oriFrame = ori.getReferenceFrame().plus(new Vector(), ori.getRotation());

		SensorMapperResult<Transformation> transDiff = runtime.getMapperRegistry().mapSensor(runtime,
				oldPivotFrame.getRelationSensor(newPivotFrame), null, context);

		SensorMapperResult<Transformation> oriRot = runtime.getMapperRegistry().mapSensor(runtime,
				oriFrame.getRelationSensor(oldPivotFrame), null, context);

		AdaptVelocityPivotPointFragment fragment = new AdaptVelocityPivotPointFragment("Pivot point adaptation",
				other.getSensorPort(), transDiff.getSensorPort(), oriRot.getSensorPort(), newPivot);

		fragment.add(other.getNetFragment());
		fragment.add(transDiff.getNetFragment());
		fragment.add(oriRot.getNetFragment());

		return new VelocitySensorMapperResult(fragment, fragment.getPivotAdaptedVelocityPort());

	}

}
