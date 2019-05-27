/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.extension;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.eventhandler.JavaExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.sensor.AbsDoubleSensor;
import org.roboticsapi.core.sensor.AddedDoubleSensor;
import org.roboticsapi.core.sensor.ArccosineDoubleSensor;
import org.roboticsapi.core.sensor.ArcsineDoubleSensor;
import org.roboticsapi.core.sensor.Atan2DoubleSensor;
import org.roboticsapi.core.sensor.BooleanAtTimeSensor;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.ConstantBooleanSensor;
import org.roboticsapi.core.sensor.ConstantDoubleSensor;
import org.roboticsapi.core.sensor.ConstantIntegerSensor;
import org.roboticsapi.core.sensor.CosineDoubleSensor;
import org.roboticsapi.core.sensor.DividedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleArrayFromDoubleSensor;
import org.roboticsapi.core.sensor.DoubleArrayFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleConditionalSensor;
import org.roboticsapi.core.sensor.DoubleEqualsSensor;
import org.roboticsapi.core.sensor.DoubleFromDoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleIsGreaterSensor;
import org.roboticsapi.core.sensor.ExponentiallySmootedDoubleSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegratedDoubleSensor;
import org.roboticsapi.core.sensor.ModuloDoubleSensor;
import org.roboticsapi.core.sensor.MultipliedDoubleSensor;
import org.roboticsapi.core.sensor.NegatedBooleanSensor;
import org.roboticsapi.core.sensor.NegatedDoubleSensor;
import org.roboticsapi.core.sensor.OrBooleanSensor;
import org.roboticsapi.core.sensor.PowerDoubleSensor;
import org.roboticsapi.core.sensor.SensorDataAgeSensor;
import org.roboticsapi.core.sensor.SineDoubleSensor;
import org.roboticsapi.core.sensor.SlidingAverageDoubleSensor;
import org.roboticsapi.core.sensor.SquareRootDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.PersistedBooleanSensor;
import org.roboticsapi.runtime.core.PersistedDoubleArraySensor;
import org.roboticsapi.runtime.core.PersistedDoubleSensor;
import org.roboticsapi.runtime.core.PersistedIntegerSensor;
import org.roboticsapi.runtime.core.mapper.AbsDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.AddedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.ArccosineDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.ArcsineDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.Atan2DoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.BooleanAtTimeSensorMapper;
import org.roboticsapi.runtime.core.mapper.BooleanFromJavaSensorMapper;
import org.roboticsapi.runtime.core.mapper.ConstantBooleanSensorMapper;
import org.roboticsapi.runtime.core.mapper.ConstantDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.ConstantIntSensorMapper;
import org.roboticsapi.runtime.core.mapper.CosineDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.DividedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleArrayFromDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleArrayFromJavaSensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleConditionalSensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleEqualsSensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleFromDoubleArraySensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleFromJavaSensorMapper;
import org.roboticsapi.runtime.core.mapper.DoubleIsGreaterSensorMapper;
import org.roboticsapi.runtime.core.mapper.ErrorIgnorerMapper;
import org.roboticsapi.runtime.core.mapper.ExponentiallySmoothedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.IntFromJavaSensorMapper;
import org.roboticsapi.runtime.core.mapper.IntegratedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.JavaExceptionThrowerMapper;
import org.roboticsapi.runtime.core.mapper.JavaExecutorMapper;
import org.roboticsapi.runtime.core.mapper.ModuloDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.MultipliedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.NegatedBooleanSensorMapper;
import org.roboticsapi.runtime.core.mapper.NegatedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.OrBooleanSensorMapper;
import org.roboticsapi.runtime.core.mapper.PersistedBooleanSensorMapper;
import org.roboticsapi.runtime.core.mapper.PersistedDoubleArraySensorMapper;
import org.roboticsapi.runtime.core.mapper.PersistedDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.PersistedIntegerSensorMapper;
import org.roboticsapi.runtime.core.mapper.PowerDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.RuntimeCommandMapper;
import org.roboticsapi.runtime.core.mapper.SensorDataAgeSensorMapper;
import org.roboticsapi.runtime.core.mapper.SineDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.SlidingAverageDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.SoftRobotTransactionMapper;
import org.roboticsapi.runtime.core.mapper.SquareRootDoubleSensorMapper;
import org.roboticsapi.runtime.core.mapper.TransactionCommandMapper;
import org.roboticsapi.runtime.core.mapper.WaitCommandMapper;
import org.roboticsapi.runtime.mapping.MapperRegistry;

public final class SoftRobotRuntimeExtension extends AbstractSoftRobotRoboticsBuilder {

	public SoftRobotRuntimeExtension() {
		super(SoftRobotRuntime.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "rpicore", "rpiworld" };
	}

	@Override
	protected void onRuntimeAvailable(SoftRobotRuntime runtime) {

		MapperRegistry mapperregistry = runtime.getMapperRegistry();

		// new mappers
		mapperregistry.registerTransactionMapper(SoftRobotRuntime.class, Command.class,
				new SoftRobotTransactionMapper());
		mapperregistry.registerCommandMapper(SoftRobotRuntime.class, TransactionCommand.class,
				new TransactionCommandMapper());
		mapperregistry.registerCommandMapper(SoftRobotRuntime.class, RuntimeCommand.class, new RuntimeCommandMapper());
		mapperregistry.registerCommandMapper(SoftRobotRuntime.class, WaitCommand.class, new WaitCommandMapper());

		mapperregistry.registerEventEffectMapper(SoftRobotRuntime.class, JavaExceptionThrower.class,
				new JavaExceptionThrowerMapper());
		mapperregistry.registerEventEffectMapper(SoftRobotRuntime.class, ExceptionIgnorer.class,
				new ErrorIgnorerMapper());
		mapperregistry.registerEventEffectMapper(SoftRobotRuntime.class, JavaExecutor.class, new JavaExecutorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ConstantBooleanSensor.class,
				new ConstantBooleanSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, NegatedBooleanSensor.class,
				new NegatedBooleanSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, OrBooleanSensor.class, new OrBooleanSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, BooleanAtTimeSensor.class,
				new BooleanAtTimeSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ConstantIntegerSensor.class,
				new ConstantIntSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, IntegerFromJavaSensor.class,
				new IntFromJavaSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ConstantDoubleSensor.class,
				new ConstantDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleConditionalSensor.class,
				new DoubleConditionalSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, Atan2DoubleSensor.class,
				new Atan2DoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ExponentiallySmootedDoubleSensor.class,
				new ExponentiallySmoothedDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SlidingAverageDoubleSensor.class,
				new SlidingAverageDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, NegatedDoubleSensor.class,
				new NegatedDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, AddedDoubleSensor.class,
				new AddedDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, IntegratedDoubleSensor.class,
				new IntegratedDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, MultipliedDoubleSensor.class,
				new MultipliedDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DividedDoubleSensor.class,
				new DividedDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ModuloDoubleSensor.class,
				new ModuloDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SquareRootDoubleSensor.class,
				new SquareRootDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PowerDoubleSensor.class,
				new PowerDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, AbsDoubleSensor.class, new AbsDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleIsGreaterSensor.class,
				new DoubleIsGreaterSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleEqualsSensor.class,
				new DoubleEqualsSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleFromJavaSensor.class,
				new DoubleFromJavaSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, BooleanFromJavaSensor.class,
				new BooleanFromJavaSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SensorDataAgeSensor.class,
				new SensorDataAgeSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleArrayFromJavaSensor.class,
				new DoubleArrayFromJavaSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ArccosineDoubleSensor.class,
				new ArccosineDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, ArcsineDoubleSensor.class,
				new ArcsineDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, CosineDoubleSensor.class,
				new CosineDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SineDoubleSensor.class,
				new SineDoubleSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleFromDoubleArraySensor.class,
				new DoubleFromDoubleArraySensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, DoubleArrayFromDoubleSensor.class,
				new DoubleArrayFromDoubleSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PersistedDoubleSensor.class,
				new PersistedDoubleSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PersistedDoubleArraySensor.class,
				new PersistedDoubleArraySensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PersistedIntegerSensor.class,
				new PersistedIntegerSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PersistedBooleanSensor.class,
				new PersistedBooleanSensorMapper());
		// FIXME: fix or delete this mapper
		// mapperregistry.registerSensorMapper(SoftRobotRuntime.class,
		// MultiSensor.class, new MultiSensorMapper());
	}

	@Override
	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {
		// TODO Auto-generated method stub

	}

}
