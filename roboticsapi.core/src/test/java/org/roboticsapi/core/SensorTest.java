/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.mockclass.MockRealtimeValue;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.mock.MockRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.WritableRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class SensorTest {
	private class SpecialMockSensor<T> extends MockRealtimeValue<T> {
		public SpecialMockSensor(RoboticsRuntime runtime) {
			super(runtime);
			setAvailable(false);
		}

		public void addInnerSensorsMock(RealtimeValue<?>... sensors) {
			this.addInnerValues(sensors);
		}

		public boolean areAvailableMock(RealtimeValue<?>... sensors) {
			return RealtimeValue.areAvailable(sensors);
		}

		public boolean classEqualMock(Object other) {
			return classEqual(other);
		}
	}

	RoboticsRuntime runtime = new TestRuntime();

	@Test
	public void testBooleanFromJavaSensorNot() {
		WritableRealtimeBoolean sensor = RealtimeBoolean.createWritable(false);

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
		WritableRealtimeBoolean sensor = RealtimeBoolean.createWritable(false);
		try {
			testBooleanFromJavaSensorNotListenerValue = false;
			sensor.addListener(new RealtimeValueListener<Boolean>() {
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
		WritableRealtimeBoolean sensor = RealtimeBoolean.createWritable(false);
		try {
			testBooleanFromJavaSensorNotListenerValue = true;
			sensor.not().addListener(new RealtimeValueListener<Boolean>() {
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
		WritableRealtimeBoolean sensor = RealtimeBoolean.createWritable(false);
		WritableRealtimeBoolean sensor2 = RealtimeBoolean.createWritable(false);
		WritableRealtimeBoolean sensor3 = RealtimeBoolean.createWritable(false);

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
		WritableRealtimeBoolean sensor = RealtimeBoolean.createWritable(false);
		WritableRealtimeBoolean sensor2 = RealtimeBoolean.createWritable(false);
		WritableRealtimeBoolean sensor3 = RealtimeBoolean.createWritable(false);

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
			Assert.assertFalse(RealtimeBoolean.createAnd(sensor, sensor2, sensor3).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(false);
			sensor3.setValue(true);
			Assert.assertFalse(RealtimeBoolean.createAnd(sensor2, sensor3).getCurrentValue());

			sensor.setValue(false);
			sensor2.setValue(true);
			sensor3.setValue(false);
			Assert.assertFalse(RealtimeBoolean.createAnd(sensor2, sensor3).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(false);
			sensor3.setValue(false);
			Assert.assertFalse(RealtimeBoolean.createAnd(sensor2, sensor3).getCurrentValue());

			sensor.setValue(true);
			sensor2.setValue(true);
			sensor3.setValue(true);
			Assert.assertTrue(RealtimeBoolean.createAnd(sensor2, sensor3).getCurrentValue());

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

	@Test(expected = RealtimeValueReadException.class)
	public void testGetCurrentValueWithNullRuntimeExpectingException() throws RealtimeValueReadException {
		RealtimeValue<Integer> mockSensor = new SpecialMockSensor<Integer>(null);

		mockSensor.getCurrentValue();
	}

	@Test
	public void testAreAvailableWithNonAvailableSensorExpectingFalse() {
		SpecialMockSensor<Integer> testSensor = new SpecialMockSensor<Integer>(runtime);

		assertFalse(testSensor.areAvailableMock(new SpecialMockSensor<Integer>(runtime)));
	}

	@Test
	public void testCreateListenerRegistration() {
		RealtimeValue<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		RealtimeValueListener<Integer> listener = new RealtimeValueListener<Integer>() {
			@Override
			public void onValueChanged(Integer newValue) {
				System.out.println("newValue of method onValueChanged: " + newValue);
			}
		};

		assertNotNull(mockSensor.createListenerRegistration(listener));
	}

	@Test
	public void testGetSensorDataAgeSensor() {
		RealtimeValue<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		assertNotNull(mockSensor.getDataAge());
	}

	@Test(expected = RoboticsException.class)
	public void testPersistWithTwoDifferentRuntimesExpectingException() throws RoboticsException {
		RealtimeValue<Integer> mockSensor = new SpecialMockSensor<Integer>(runtime);

		mockSensor.persist(null);
	}

	@Test
	public void testAreAvailableWithNotAvailableSensor() {
		SpecialMockSensor<Double> testSensor = new SpecialMockSensor<Double>(null);

		assertFalse(testSensor.isAvailable());

		assertFalse(testSensor.areAvailableMock(testSensor));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddInnerSensorsWithTwoNullSensorsExpectingException() {
		RealtimeValue<Double> testSensor1 = null, testSensor2 = null;

		SpecialMockSensor<Double> mockSensor = new SpecialMockSensor<Double>(null);

		mockSensor.addInnerSensorsMock(testSensor1, testSensor2);
	}

	@Test
	public void testStaticMethodAreAvailableWithSensorListExpectingFalse() {
		MockRealtimeBoolean testBooleanSensor = new MockRealtimeBoolean();
		testBooleanSensor.setAvailable(false);

		RealtimeBoolean testOrBooleanSensor = RealtimeBoolean.createOr(testBooleanSensor);

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
		RealtimeDouble testDoubleSensor = RealtimeDouble.createWritable(0);

		assertFalse(testIntegerSensor.classEqualMock(testDoubleSensor));
	}
}
