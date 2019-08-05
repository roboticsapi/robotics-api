/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.OrBooleanSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.mockclass.MockBooleanSensor;
import org.roboticsapi.mockclass.MockSensor;
import org.roboticsapi.mockclass.TestRuntime;

public class SensorTest {
	private class SpecialMockSensor<T> extends MockSensor<T> {
		public SpecialMockSensor(RoboticsRuntime runtime) {
			super(runtime);
			setAvailable(false);
		}

		public void addInnerSensorsMock(Sensor<?>... sensors) {
			this.addInnerSensors(sensors);
		}

		public boolean areAvailableMock(Sensor<?>... sensors) {
			return Sensor.areAvailable(sensors);
		}

		public boolean classEqualMock(Object other) {
			return classEqual(other);
		}
	}

	RoboticsRuntime runtime = new TestRuntime();

	@Test
	public void testBooleanFromJavaSensorNot() {
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);

		sensor.setValue(true);

		try {
			Assert.assertFalse(sensor.not().getCurrentValue());
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	boolean testBooleanFromJavaSensorNotListenerValue;

	@Test
	public void testBooleanFromJavaSensorListener() {
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);
		try {
			testBooleanFromJavaSensorNotListenerValue = false;
			sensor.addListener(new SensorListener<Boolean>() {
				@Override
				public void onValueChanged(Boolean newValue) {
					testBooleanFromJavaSensorNotListenerValue = newValue;
				}
			});
			sensor.setValue(true);
			Assert.assertTrue(testBooleanFromJavaSensorNotListenerValue);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testBooleanFromJavaSensorNotListener() {
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);
		try {
			testBooleanFromJavaSensorNotListenerValue = true;
			sensor.not().addListener(new SensorListener<Boolean>() {
				@Override
				public void onValueChanged(Boolean newValue) {
					testBooleanFromJavaSensorNotListenerValue = newValue;
				}
			});
			sensor.setValue(true);
			Assert.assertFalse(testBooleanFromJavaSensorNotListenerValue);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testBooleanFromJavaSensorOr() {
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);
		BooleanFromJavaSensor sensor2 = new BooleanFromJavaSensor(false);
		BooleanFromJavaSensor sensor3 = new BooleanFromJavaSensor(false);

		try {
			sensor.setValue(false);
			sensor2.setValue(false);
			Assert.assertFalse(sensor.or(sensor2).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(false);
			Assert.assertTrue(sensor.or(sensor2).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(true);
			Assert.assertTrue(sensor.or(sensor2).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(true);
			Assert.assertTrue(sensor.or(sensor2).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(false);
			sensor3.setValue(false);
			Assert.assertFalse(sensor.or(sensor2, sensor3).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(false);
			sensor3.setValue(true);
			Assert.assertTrue(sensor.or(sensor2, sensor3).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(true);
			sensor3.setValue(false);
			Assert.assertTrue(sensor.or(sensor2, sensor3).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(false);
			sensor3.setValue(false);
			Assert.assertTrue(sensor.or(sensor2, sensor3).getCurrentValue());

		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testBooleanFromJavaSensorAnd() {
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);
		BooleanFromJavaSensor sensor2 = new BooleanFromJavaSensor(false);
		BooleanFromJavaSensor sensor3 = new BooleanFromJavaSensor(false);

		try {
			sensor.setValue(false);
			sensor2.setValue(false);
			Assert.assertFalse(sensor.and(sensor2).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(false);
			Assert.assertFalse(sensor.and(sensor2).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(true);
			Assert.assertFalse(sensor.and(sensor2).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(true);
			Assert.assertTrue(sensor.and(sensor2).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(false);
			sensor3.setValue(false);
			Assert.assertFalse(sensor.and(sensor2, sensor3).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(false);
			sensor3.setValue(true);
			Assert.assertFalse(sensor.and(sensor2, sensor3).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(true);
			sensor3.setValue(false);
			Assert.assertFalse(sensor.and(sensor2, sensor3).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(false);
			sensor3.setValue(false);
			Assert.assertFalse(sensor.and(sensor2, sensor3).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(true);
			sensor3.setValue(true);
			Assert.assertTrue(sensor.and(sensor2, sensor3).getCurrentValue());

		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testAreAvailableWithVarargsExpectingFalse() {
		SpecialMockSensor<Integer> testSensor = new SpecialMockSensor<Integer>(runtime);

		assertFalse(testSensor.areAvailableMock(testSensor));
	}

	@Test
	public void testAreAvailableWithMockSensor() {
		SpecialMockSensor<Integer> testSensor = new SpecialMockSensor<Integer>(runtime);

		assertTrue(testSensor.areAvailableMock());

		assertFalse(testSensor.areAvailableMock(testSensor));
	}

	@Test(expected = SensorReadException.class)
	public void testGetCurrentValueWithNullRuntimeExpectingException() throws SensorReadException {
		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(null);

		mockSensor.getCurrentValue();
	}

	@Test(expected = SensorReadException.class)
	public void testGetCurrentValueWithErrorInSensorValueExpectingException() throws SensorReadException {
		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		mockSensor.getCurrentValue();
	}

	@Test
	public void testAreAvailableWithNonAvailableSensorExpectingFalse() {
		SpecialMockSensor<Integer> testSensor = new SpecialMockSensor<Integer>(runtime);

		assertFalse(testSensor.areAvailableMock(new SpecialMockSensor<Integer>(runtime)));
	}

	@Test
	public void testCreateListenerRegistration() {
		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		SensorListener<Integer> listener = new SensorListener<Integer>() {
			@Override
			public void onValueChanged(Integer newValue) {
				System.out.println("newValue of method onValueChanged: " + newValue);
			}
		};

		assertNotNull(mockSensor.createListenerRegistration(listener));
	}

	@Test
	public void testGetSensorDataAgeSensor() {
		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		assertNotNull(mockSensor.getSensorDataAgeSensor());
	}

	@Test
	public void testDefineSensorExceptions() {
		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		assertNotNull(mockSensor.defineSensorExceptions());
	}

	@Test(expected = RoboticsException.class)
	public void testPersistWithTwoDifferentRuntimesExpectingException() throws RoboticsException {
		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		mockSensor.persist(null);
	}

	@Test
	public void testPersistWithTwiceUsedTestRuntime() throws RoboticsException {
		class MockRuntime extends TestRuntime {
			@Override
			public CommandHandle loadCommand(Command command) throws RoboticsException {
				return new CommandHandle() {
					@Override
					public boolean start() throws CommandException {
						return false;
					}

					@Override
					public boolean scheduleAfter(CommandHandle executeAfter) throws CommandException {
						return false;
					}

					@Override
					public boolean abort() throws CommandException {
						return false;
					}

					@Override
					public boolean cancel() throws CommandException {
						return false;
					}

					@Override
					public void waitComplete() throws CommandException {
					}

					@Override
					public CommandStatus getStatus() throws CommandException {
						return null;
					}

					@Override
					public void addStatusListener(CommandStatusListener listener) throws CommandException {
					}

					@Override
					public void throwException(CommandException ex) {
					}

					@Override
					public CommandException getOccurredException() {
						return null;
					}

					@Override
					public void startThread(Runnable runnable) {
					}
				};
			}
		}

		RoboticsRuntime runtime = new MockRuntime();

		Sensor<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		mockSensor.persist(runtime);
	}

	@Test
	public void testAreAvailableWithNotAvailableSensor() {
		SpecialMockSensor<Double> testSensor = new SpecialMockSensor<Double>(null);

		assertFalse(testSensor.isAvailable());

		assertFalse(testSensor.areAvailableMock(testSensor));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddInnerSensorsWithTwoNullSensorsExpectingException() {
		Sensor<Double> testSensor1 = null, testSensor2 = null;

		SpecialMockSensor<Double> mockSensor = new SpecialMockSensor<Double>(null);

		mockSensor.addInnerSensorsMock(testSensor1, testSensor2);
	}

	@Test
	public void testStaticMethodAreAvailableWithSensorListExpectingFalse() {
		MockBooleanSensor testBooleanSensor = new MockBooleanSensor(null);
		testBooleanSensor.setAvailable(false);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensor);

		assertFalse(testOrBooleanSensor.isAvailable());
	}

	@Test
	public void testMethodNamedClassEqualWithTwoObjectsOfSameClassExpectingTrue() {
		SpecialMockSensor<Integer> testSensor1 = new SpecialMockSensor<Integer>(null);
		SpecialMockSensor<Integer> testSensor2 = new SpecialMockSensor<Integer>(null);

		assertTrue(testSensor1.classEqualMock(testSensor2));
		assertTrue(testSensor2.classEqualMock(testSensor1));
	}

	@Test
	public void testMethodNamedClassEqualWithTwoObjectsOfDifferentClassesExpectingTrue() {
		SpecialMockSensor<Integer> testIntegerSensor = new SpecialMockSensor<Integer>(null);
		DoubleSensor testDoubleSensor = new DoubleFromJavaSensor(0);

		assertFalse(testIntegerSensor.classEqualMock(testDoubleSensor));
	}
}
