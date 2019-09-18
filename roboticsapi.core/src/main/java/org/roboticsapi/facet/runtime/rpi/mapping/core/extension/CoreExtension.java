/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.AbsRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.AcosRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.AddedRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.AddedRealtimeIntegerMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.AndRealtimeBooleanMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.AsinRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.Atan2RealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ConditionalRealtimeBooleanMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ConditionalRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ConsistentTimeRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ConstantRealtimeBooleanMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ConstantRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ConstantRealtimeIntegerMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.CosRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.DataAgeForTimeRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.DataAgeRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.DividedRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.DoubleArrayFromRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.DoubleFromRealtimeDoubleArrayMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ExceptionStateMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.ExponentiallySmootedRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.FlipFlopRealtimeBooleanMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.GreaterDoubleRealtimeComparatorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.IntegerFromJavaSensorMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.MultipliedRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.MultipliedRealtimeIntegerMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.NegatedRealtimeBooleanMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.NegatedRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.OrRealtimeBooleanMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeBooleanAtTimeMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeBooleanFromJavaMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeBooleanIsNullMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeDoubleArrayFromJavaMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeDoubleFromJavaMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeDoubleFromRealtimeIntegerMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.SelfMappingRealtimeValueMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.SinRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.SlidingAverageRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.SquareRootRealtimeDoubleMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.TanRealtimeDoubleMapper;

public class CoreExtension extends RpiExtension {

	@Override
	protected void registerMappers(MapperRegistry mr) {
		mr.registerRealtimeValueMapper(new SelfMappingRealtimeValueMapper());
		mr.registerRealtimeValueMapper(new AddedRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new AddedRealtimeIntegerMapper());
		mr.registerRealtimeValueMapper(new Atan2RealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new RealtimeBooleanFromJavaMapper());
		mr.registerRealtimeValueMapper(new ConditionalRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeBooleanMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeIntegerMapper());
		mr.registerRealtimeValueMapper(new DividedRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new DoubleArrayFromRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new RealtimeDoubleFromJavaMapper());
		mr.registerRealtimeValueMapper(new RealtimeDoubleArrayFromJavaMapper());
		mr.registerRealtimeValueMapper(new RealtimeDoubleFromRealtimeIntegerMapper());
		mr.registerRealtimeValueMapper(new DoubleFromRealtimeDoubleArrayMapper());
		mr.registerRealtimeValueMapper(new ExponentiallySmootedRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new FlipFlopRealtimeBooleanMapper());
		mr.registerRealtimeValueMapper(new RealtimeBooleanIsNullMapper());
		mr.registerRealtimeValueMapper(new GreaterDoubleRealtimeComparatorMapper());
		mr.registerRealtimeValueMapper(new IntegerFromJavaSensorMapper());
		mr.registerRealtimeValueMapper(new MultipliedRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new MultipliedRealtimeIntegerMapper());
		mr.registerRealtimeValueMapper(new NegatedRealtimeBooleanMapper());
		mr.registerRealtimeValueMapper(new NegatedRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new OrRealtimeBooleanMapper());
		mr.registerRealtimeValueMapper(new AndRealtimeBooleanMapper());
		mr.registerRealtimeValueMapper(new ConditionalRealtimeBooleanMapper());
		mr.registerRealtimeValueMapper(new RealtimeBooleanAtTimeMapper());
		mr.registerRealtimeValueMapper(new DataAgeRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new DataAgeForTimeRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new ConsistentTimeRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new SlidingAverageRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new SquareRootRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new AcosRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new AsinRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new CosRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new SinRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new TanRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new AbsRealtimeDoubleMapper());
		mr.registerRealtimeValueMapper(new ExceptionStateMapper());
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// TODO: Remove mappers?
	}

}
