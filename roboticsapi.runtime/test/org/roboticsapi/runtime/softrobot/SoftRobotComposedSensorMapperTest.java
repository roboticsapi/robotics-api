/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.softrobot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.generic.AbstractRuntimeTest;
import org.roboticsapi.runtime.generic.RuntimeSetup;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.UnimplementedMappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflow;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.ComposedSensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleArraySensorMapperResult;
import org.roboticsapi.runtime.rpi.OutPort;

public class SoftRobotComposedSensorMapperTest extends AbstractRuntimeTest {

	class DummyType {
		public Boolean booleanValue = false;
		public Double doubleValue = 0d;
		public Double[] doubleArrayValue = new Double[] {};
		public Double[] doubleArrayValue2 = new Double[] {};
	}

	class DummyComposedSensor extends Sensor<DummyType> {

		private final BooleanSensor b;
		private final DoubleSensor d;
		private final DoubleArraySensor da;
		private final DoubleArraySensor da2;

		public DummyComposedSensor(RoboticsRuntime runtime, BooleanSensor b, DoubleSensor d, DoubleArraySensor da,
				DoubleArraySensor da2) {
			super(runtime);
			this.b = b;
			this.d = d;
			this.da = da;
			this.da2 = da2;
		}

		@Override
		public boolean isAvailable() {
			return true;
		}

		@Override
		protected DummyType getDefaultValue() {
			return new DummyType();
		}

		public BooleanSensor getBooleanSensor() {
			return b;
		}

		public DoubleSensor getDoubleSensor() {
			return d;
		}

		public DoubleArraySensor getDoubleArraySensor() {
			return da;
		}

		public DoubleArraySensor getDoubleArraySensor2() {
			return da2;
		}

		public DoubleArraySensor getFirstArraySensor() {
			return new DummyComposedFirstArraySensor(this);
		}

	}

	class DummyComposedFirstArraySensor extends DoubleArraySensor {

		private final DummyComposedSensor sensor;

		public DummyComposedFirstArraySensor(DummyComposedSensor sensor) {
			super(sensor.getRuntime(), sensor.getDoubleArraySensor().getSize());
			this.sensor = sensor;
		}

		@Override
		public boolean isAvailable() {
			return getSensor().isAvailable();
		}

		public DummyComposedSensor getSensor() {
			return sensor;
		}
	}

	class DoubleArrayDataflowOne extends DoubleArrayDataflow {
		public DoubleArrayDataflowOne(int size) {
			super(size);
		}
	}

	class DoubleArrayDataflowTwo extends DoubleArrayDataflow {
		public DoubleArrayDataflowTwo(int size) {
			super(size);
		}
	}

	class DummyComposedSensorMapper implements SensorMapper<SoftRobotRuntime, DummyType, DummyComposedSensor> {

		@Override
		public SensorMapperResult<DummyType> map(SoftRobotRuntime runtime, DummyComposedSensor sensor,
				SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
			NetFragment fragment = new NetFragment("DummyComposedSensor");

			SensorMapperResult<Boolean> mappedBooleanSensor = runtime.getMapperRegistry().mapSensor(getRuntime(),
					sensor.getBooleanSensor(), fragment, context);

			SensorMapperResult<Double> mappedDoubleSensor = runtime.getMapperRegistry().mapSensor(getRuntime(),
					sensor.getDoubleSensor(), fragment, context);

			SensorMapperResult<Double[]> mappedDoubleArraySensor = runtime.getMapperRegistry().mapSensor(getRuntime(),
					sensor.getDoubleArraySensor(), fragment, context);

			SensorMapperResult<Double[]> mappedDoubleArraySensor2 = runtime.getMapperRegistry().mapSensor(getRuntime(),
					sensor.getDoubleArraySensor2(), fragment, context);

			List<OutPort> innerPorts = new ArrayList<OutPort>();
			innerPorts.addAll(mappedBooleanSensor.getSensorPort().getPorts());
			innerPorts.addAll(mappedDoubleSensor.getSensorPort().getPorts());
			innerPorts.addAll(mappedDoubleArraySensor2.getSensorPort().getPorts());
			innerPorts.addAll(mappedDoubleArraySensor.getSensorPort().getPorts());
			ComposedDataflow type = new ComposedDataflow();
			type.addDataflow(mappedBooleanSensor.getSensorPort().getType());
			type.addDataflow(mappedDoubleSensor.getSensorPort().getType());
			type.addDataflow(new DoubleArrayDataflowTwo(sensor.getDoubleArraySensor2().getSize()));
			type.addDataflow(new DoubleArrayDataflowOne(sensor.getDoubleArraySensor().getSize()));
			DataflowOutPort sensorPort = fragment.addOutPort(type, true, innerPorts);

			return new ComposedSensorMapperResult<SoftRobotComposedSensorMapperTest.DummyType>(fragment, sensorPort) {

				@Override
				public void assign(Command command, PersistContext<DummyType> target) throws MappingException {
					throw new UnimplementedMappingException();

				}

				@Override
				protected DummyType composeValue(Object[] values) {
					DummyType dummyType = new DummyType();
					dummyType.booleanValue = (Boolean) values[0];
					dummyType.doubleValue = (Double) values[1];
					dummyType.doubleArrayValue2 = (Double[]) values[2];
					dummyType.doubleArrayValue = (Double[]) values[3];
					return dummyType;
				}
			};
		}
	}

	class DummyComposedFirstArraySensorMapper
			implements SensorMapper<SoftRobotRuntime, Double[], DummyComposedFirstArraySensor> {

		@Override
		public SensorMapperResult<Double[]> map(SoftRobotRuntime runtime, DummyComposedFirstArraySensor sensor,
				SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

			NetFragment fragment = new NetFragment("");

			DummyComposedSensor innerSensor = sensor.getSensor();
			SensorMapperResult<DummyType> mapSensor = runtime.getMapperRegistry().mapSensor(runtime, innerSensor,
					fragment, context);

			List<OutPort> sensorPorts = mapSensor.getSensorPort()
					.getPorts(new DoubleArrayDataflowOne(sensor.getSize()));

			DataflowOutPort sensorPort = fragment.addOutPort(new DoubleArrayDataflow(sensor.getSize()), true,
					sensorPorts);

			return new DoubleArraySensorMapperResult(fragment, sensorPort);
		}

	}

	DummyType testValue = null;
	private BooleanFromJavaSensor bSensor;
	private DoubleFromJavaSensor dSensor;
	private DoubleFromJavaSensor daSensor11;
	private DoubleFromJavaSensor daSensor12;
	private DoubleFromJavaSensor daSensor21;
	private DoubleFromJavaSensor daSensor22;
	private DoubleArraySensor daSensor;
	private DoubleArraySensor daSensor2;
	private DummyComposedSensor cSensor;
	private Semaphore sem;
	private SensorListener<SoftRobotComposedSensorMapperTest.DummyType> listener;

	@Override
	public RuntimeSetup getRuntimeSetup() {
		return new SoftRobotRuntimeSetup();
	}

	@Before
	public void setup() throws InterruptedException, RoboticsException {
		((SoftRobotRuntime) getRuntime()).getMapperRegistry().registerSensorMapper(SoftRobotRuntime.class,
				DummyComposedSensor.class, new DummyComposedSensorMapper());

		((SoftRobotRuntime) getRuntime()).getMapperRegistry().registerSensorMapper(SoftRobotRuntime.class,
				DummyComposedFirstArraySensor.class, new DummyComposedFirstArraySensorMapper());

		bSensor = new BooleanFromJavaSensor(true);
		dSensor = new DoubleFromJavaSensor(1);
		daSensor11 = new DoubleFromJavaSensor(1);
		daSensor12 = new DoubleFromJavaSensor(2);
		daSensor = DoubleArraySensor.fromSensors(daSensor11, daSensor12);
		daSensor21 = new DoubleFromJavaSensor(3);
		daSensor22 = new DoubleFromJavaSensor(4);
		daSensor2 = DoubleArraySensor.fromSensors(daSensor21, daSensor22);

		cSensor = new DummyComposedSensor(getRuntime(), bSensor, dSensor, daSensor, daSensor2);

		sem = new Semaphore(1);
		sem.acquire();
		listener = new SensorListener<SoftRobotComposedSensorMapperTest.DummyType>() {

			@Override
			public void onValueChanged(DummyType newValue) {
				testValue = newValue;
				sem.release();
			}
		};
		cSensor.addListener(listener);
	}

	@After
	public void teardown() throws RoboticsException {
		cSensor.removeListener(listener);
	}

	@Test(timeout = 1500)
	public void testComposedSensorDeliversInitialValueToListener() throws RoboticsException, InterruptedException {

		sem.acquire();

		Assert.assertNotNull(testValue);
		Assert.assertEquals(true, testValue.booleanValue);
		Assert.assertEquals(1, testValue.doubleValue, 0.0001);
		Assert.assertEquals(1, testValue.doubleArrayValue[0], 0.0001);
		Assert.assertEquals(2, testValue.doubleArrayValue[1], 0.0001);
		Assert.assertEquals(3, testValue.doubleArrayValue2[0], 0.0001);
		Assert.assertEquals(4, testValue.doubleArrayValue2[1], 0.0001);
	}

	@Test(timeout = 2500)
	public void testComposedSensorDeliversCompleteUpdateToListener() throws RoboticsException, InterruptedException {

		bSensor.setValue(false);
		dSensor.setValue(2);
		daSensor11.setValue(3);
		daSensor12.setValue(4);
		daSensor21.setValue(5);
		daSensor22.setValue(6);

		Thread.sleep(1000);

		Assert.assertNotNull(testValue);
		Assert.assertEquals(false, testValue.booleanValue);
		Assert.assertEquals(2, testValue.doubleValue, 0.0001);
		Assert.assertEquals(3, testValue.doubleArrayValue[0], 0.0001);
		Assert.assertEquals(4, testValue.doubleArrayValue[1], 0.0001);
		Assert.assertEquals(5, testValue.doubleArrayValue2[0], 0.0001);
		Assert.assertEquals(6, testValue.doubleArrayValue2[1], 0.0001);
	}

	@Test(timeout = 3500)
	public void testComposedSensorDeliversPartialUpdatesToListener() throws RoboticsException, InterruptedException {

		bSensor.setValue(false);
		Thread.sleep(1000);

		Assert.assertNotNull(testValue);
		Assert.assertEquals(false, testValue.booleanValue);
		Assert.assertEquals(1, testValue.doubleValue, 0.0001);
		Assert.assertEquals(1, testValue.doubleArrayValue[0], 0.0001);
		Assert.assertEquals(2, testValue.doubleArrayValue[1], 0.0001);
		Assert.assertEquals(3, testValue.doubleArrayValue2[0], 0.0001);
		Assert.assertEquals(4, testValue.doubleArrayValue2[1], 0.0001);

		dSensor.setValue(2);
		Thread.sleep(1000);

		Assert.assertNotNull(testValue);
		Assert.assertEquals(false, testValue.booleanValue);
		Assert.assertEquals(2, testValue.doubleValue, 0.0001);
		Assert.assertEquals(1, testValue.doubleArrayValue[0], 0.0001);
		Assert.assertEquals(2, testValue.doubleArrayValue[1], 0.0001);
		Assert.assertEquals(3, testValue.doubleArrayValue2[0], 0.0001);
		Assert.assertEquals(4, testValue.doubleArrayValue2[1], 0.0001);
	}

	@Test(timeout = 1500)
	public void testDataflowBasedComponentSensorDeliversCorrectComponent() throws SensorReadException {
		Double[] value1 = cSensor.getFirstArraySensor().getCurrentValue();

		Assert.assertEquals(1, value1[0], 0.0001);
		Assert.assertEquals(2, value1[1], 0.0001);

		daSensor11.setValue(3);
		daSensor12.setValue(5);

		value1 = cSensor.getFirstArraySensor().getCurrentValue();

		Assert.assertEquals(3, value1[0], 0.0001);
		Assert.assertEquals(5, value1[1], 0.0001);
	}

}
