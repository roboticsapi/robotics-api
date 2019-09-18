/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.AddedRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.AddedRealtimeVectorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.AngleFromRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.AxisFromRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.ConditionalRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.ConditionalRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.ConstantRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.ConstantRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.ConstantRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.ConstantRealtimeVectorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.CrossProductRealtimeVectorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.InvertedRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.InvertedRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.InvertedRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.MergedRealtimeTransformationArrayMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.MultipliedRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.MultipliedRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.OrientationAdaptedRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.OrientationAdaptedRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.PersistedRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.PersistedRealtimeTransformationArrayMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.PersistedRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.PersistedRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.PersistedRealtimeVectorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.PivotAdaptedRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeRotationComponentMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeRotationFromABCMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeRotationFromAxisAngleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeRotationFromQuaternionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeRotationFromTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTransformationAtTimeMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTransformationFromArrayMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTransformationFromComponentsMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTransformationFromJavaMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTransformationIsNullMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTwistAtTimeMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeTwistFromComponentsMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeVectorAtTimeMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeVectorComponentMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RealtimeVectorFromComponentsMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RotVelVectorFromRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.RotatedRealtimeVectorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.SlidingAverageRealtimeRotationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.TransVelVectorFromRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.TransformationFromRealtimeTwistMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.TransformedRealtimeVectorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.TwistFromRealtimeTransformationMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.world.mapper.VectorFromRealtimeTransformationMapper;

public class WorldExtension extends RpiExtension {

	@Override
	protected void registerMappers(MapperRegistry mr) {
		mr.registerRealtimeValueMapper(new AddedRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new AddedRealtimeVectorMapper());
		mr.registerRealtimeValueMapper(new AngleFromRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new AxisFromRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new ConditionalRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new ConditionalRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeVectorMapper());
		mr.registerRealtimeValueMapper(new CrossProductRealtimeVectorMapper());
		mr.registerRealtimeValueMapper(new InvertedRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new InvertedRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new InvertedRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new MergedRealtimeTransformationArrayMapper());
		mr.registerRealtimeValueMapper(new MultipliedRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new MultipliedRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new OrientationAdaptedRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new OrientationAdaptedRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new PivotAdaptedRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new RealtimeRotationComponentMapper());
		mr.registerRealtimeValueMapper(new RealtimeRotationFromABCMapper());
		mr.registerRealtimeValueMapper(new RealtimeRotationFromAxisAngleMapper());
		mr.registerRealtimeValueMapper(new RealtimeRotationFromQuaternionMapper());
		mr.registerRealtimeValueMapper(new RealtimeRotationFromTransformationMapper());
		mr.registerRealtimeValueMapper(new RealtimeTransformationAtTimeMapper());
		mr.registerRealtimeValueMapper(new RealtimeTwistAtTimeMapper());
		mr.registerRealtimeValueMapper(new RealtimeVectorAtTimeMapper());
		mr.registerRealtimeValueMapper(new RealtimeTransformationIsNullMapper());
		mr.registerRealtimeValueMapper(new RealtimeTransformationFromArrayMapper());
		mr.registerRealtimeValueMapper(new RealtimeTransformationFromComponentsMapper());
		mr.registerRealtimeValueMapper(new RealtimeTransformationFromJavaMapper());
		mr.registerRealtimeValueMapper(new RealtimeTwistFromComponentsMapper());
		mr.registerRealtimeValueMapper(new RealtimeVectorComponentMapper());
		mr.registerRealtimeValueMapper(new RealtimeVectorFromComponentsMapper());
		mr.registerRealtimeValueMapper(new RotVelVectorFromRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new RotatedRealtimeVectorMapper());
		mr.registerRealtimeValueMapper(new SlidingAverageRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new TransformedRealtimeVectorMapper());
		mr.registerRealtimeValueMapper(new TransVelVectorFromRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new VectorFromRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new TwistFromRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new TransformationFromRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new PersistedRealtimeRotationMapper());
		mr.registerRealtimeValueMapper(new PersistedRealtimeTransformationArrayMapper());
		mr.registerRealtimeValueMapper(new PersistedRealtimeTransformationMapper());
		mr.registerRealtimeValueMapper(new PersistedRealtimeTwistMapper());
		mr.registerRealtimeValueMapper(new PersistedRealtimeVectorMapper());
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// TODO: Remove mappers?
	}

}
