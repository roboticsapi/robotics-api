/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.world.PersistedRotationSensor;
import org.roboticsapi.runtime.world.PersistedTransformationArraySensor;
import org.roboticsapi.runtime.world.PersistedTransformationSensor;
import org.roboticsapi.runtime.world.PersistedVectorSensor;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflowSensor;
import org.roboticsapi.runtime.world.mapper.AddedVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.AngleFromRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.AxisFromRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.ConstantRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.ConstantTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.ConstantVectorSensorMapper;
import org.roboticsapi.runtime.world.mapper.ConstantVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.DirectionSensorMapper;
import org.roboticsapi.runtime.world.mapper.FromArrayTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.InvertedRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.InvertedTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.InvertedVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.MergedTransformationArraySensorMapper;
import org.roboticsapi.runtime.world.mapper.MultipliedRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.MultipliedTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.OrientationAdaptedVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.OrientationSensorMapper;
import org.roboticsapi.runtime.world.mapper.PersistedRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.PersistedTransformationArraySensorMapper;
import org.roboticsapi.runtime.world.mapper.PersistedTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.PersistedVectorSensorMapper;
import org.roboticsapi.runtime.world.mapper.PivotAdaptedVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.PointSensorMapper;
import org.roboticsapi.runtime.world.mapper.ReferenceAdaptedPointSensorMapper;
import org.roboticsapi.runtime.world.mapper.ReinterpretedVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.RelationSensorMapper;
import org.roboticsapi.runtime.world.mapper.RotVelFromVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.RotationComponentSensorMapper;
import org.roboticsapi.runtime.world.mapper.RotationFromABCSensorMapper;
import org.roboticsapi.runtime.world.mapper.RotationFromAxisAngleSensorMapper;
import org.roboticsapi.runtime.world.mapper.RotationFromQuaternionSensorMapper;
import org.roboticsapi.runtime.world.mapper.RotationFromTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.SlidingAverageRotationSensorMapper;
import org.roboticsapi.runtime.world.mapper.TransVelFromVelocitySensorMapper;
import org.roboticsapi.runtime.world.mapper.TransformationAtTimeSensorMapper;
import org.roboticsapi.runtime.world.mapper.TransformationConditionalSensorMapper;
import org.roboticsapi.runtime.world.mapper.TransformationDataflowSensorMapper;
import org.roboticsapi.runtime.world.mapper.TransformationFromComponentsSensorMapper;
import org.roboticsapi.runtime.world.mapper.VectorComponentSensorMapper;
import org.roboticsapi.runtime.world.mapper.VectorFromComponentsSensorMapper;
import org.roboticsapi.runtime.world.mapper.VectorFromTransformationSensorMapper;
import org.roboticsapi.runtime.world.mapper.VelocityFromComponentsSensorMapper;
import org.roboticsapi.world.sensor.AddedVelocitySensor;
import org.roboticsapi.world.sensor.AngleFromRotationSensor;
import org.roboticsapi.world.sensor.AxisFromRotationSensor;
import org.roboticsapi.world.sensor.ConstantRotationSensor;
import org.roboticsapi.world.sensor.ConstantTransformationSensor;
import org.roboticsapi.world.sensor.ConstantVectorSensor;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.FromArrayTransformationSensor;
import org.roboticsapi.world.sensor.InvertedRotationSensor;
import org.roboticsapi.world.sensor.InvertedTransformationSensor;
import org.roboticsapi.world.sensor.InvertedVelocitySensor;
import org.roboticsapi.world.sensor.MergedTransformationArraySensor;
import org.roboticsapi.world.sensor.MultipliedRotationSensor;
import org.roboticsapi.world.sensor.MultipliedTransformationSensor;
import org.roboticsapi.world.sensor.OrientationAdaptedVelocitySensor;
import org.roboticsapi.world.sensor.OrientationSensor;
import org.roboticsapi.world.sensor.PivotAdaptedVelocitySensor;
import org.roboticsapi.world.sensor.PointSensor;
import org.roboticsapi.world.sensor.ReinterpretedVelocitySensor;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.RotVelVectorFromVelocitySensor;
import org.roboticsapi.world.sensor.RotationComponentSensor;
import org.roboticsapi.world.sensor.RotationFromABCSensor;
import org.roboticsapi.world.sensor.RotationFromAxisAngleSensor;
import org.roboticsapi.world.sensor.RotationFromQuaternionSensor;
import org.roboticsapi.world.sensor.RotationFromTransformationSensor;
import org.roboticsapi.world.sensor.SlidingAverageRotationSensor;
import org.roboticsapi.world.sensor.TransVelVectorFromVelocitySensor;
import org.roboticsapi.world.sensor.TransformationAtTimeSensor;
import org.roboticsapi.world.sensor.TransformationConditionalSensor;
import org.roboticsapi.world.sensor.TransformationFromComponentsSensor;
import org.roboticsapi.world.sensor.TransformedVectorSensor;
import org.roboticsapi.world.sensor.VectorComponentSensor;
import org.roboticsapi.world.sensor.VectorFromComponentsSensor;
import org.roboticsapi.world.sensor.VectorFromTransformationSensor;
import org.roboticsapi.world.sensor.VelocityFromComponentsSensor;

public class RPIWorldExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject object) {
		if (!(object instanceof AbstractMapperRuntime)) {
			return;
		}

		AbstractMapperRuntime runtime = (AbstractMapperRuntime) object;

		MapperRegistry mapperregistry = runtime.getMapperRegistry();
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, AddedVelocitySensor.class,
				new AddedVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, ConstantTransformationSensor.class,
				new ConstantTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, ConstantVectorSensor.class,
				new ConstantVectorSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, ConstantVelocitySensor.class,
				new ConstantVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, DirectionSensor.class,
				new DirectionSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, InvertedTransformationSensor.class,
				new InvertedTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, InvertedVelocitySensor.class,
				new InvertedVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, MultipliedTransformationSensor.class,
				new MultipliedTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, TransformationDataflowSensor.class,
				new TransformationDataflowSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, TransformationConditionalSensor.class,
				new TransformationConditionalSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, OrientationAdaptedVelocitySensor.class,
				new OrientationAdaptedVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, OrientationSensor.class,
				new OrientationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, PivotAdaptedVelocitySensor.class,
				new PivotAdaptedVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, PointSensor.class, new PointSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, TransformedVectorSensor.class,
				new ReferenceAdaptedPointSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, ReinterpretedVelocitySensor.class,
				new ReinterpretedVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RelationSensor.class,
				new RelationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RotationComponentSensor.class,
				new RotationComponentSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, ConstantRotationSensor.class,
				new ConstantRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RotationFromABCSensor.class,
				new RotationFromABCSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RotationFromAxisAngleSensor.class,
				new RotationFromAxisAngleSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RotationFromQuaternionSensor.class,
				new RotationFromQuaternionSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RotationFromTransformationSensor.class,
				new RotationFromTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, TransformationFromComponentsSensor.class,
				new TransformationFromComponentsSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, MultipliedRotationSensor.class,
				new MultipliedRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, VectorComponentSensor.class,
				new VectorComponentSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, VectorFromComponentsSensor.class,
				new VectorFromComponentsSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, VectorFromTransformationSensor.class,
				new VectorFromTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, TransVelVectorFromVelocitySensor.class,
				new TransVelFromVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, RotVelVectorFromVelocitySensor.class,
				new RotVelFromVelocitySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, VelocityFromComponentsSensor.class,
				new VelocityFromComponentsSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, AngleFromRotationSensor.class,
				new AngleFromRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, AxisFromRotationSensor.class,
				new AxisFromRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, InvertedRotationSensor.class,
				new InvertedRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, TransformationAtTimeSensor.class,
				new TransformationAtTimeSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, PersistedRotationSensor.class,
				new PersistedRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, PersistedVectorSensor.class,
				new PersistedVectorSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, PersistedTransformationSensor.class,
				new PersistedTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, PersistedTransformationArraySensor.class,
				new PersistedTransformationArraySensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, SlidingAverageRotationSensor.class,
				new SlidingAverageRotationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, FromArrayTransformationSensor.class,
				new FromArrayTransformationSensorMapper());
		mapperregistry.registerSensorMapper(AbstractMapperRuntime.class, MergedTransformationArraySensor.class,
				new MergedTransformationArraySensorMapper());
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		// TODO: Remove mappers?
	}

}
