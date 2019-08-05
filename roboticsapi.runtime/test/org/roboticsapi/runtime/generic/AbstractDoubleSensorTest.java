/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.SensorState;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.core.sensor.UnaryFunctionDoubleSensor;
import org.roboticsapi.runtime.mockclass.MockDoubleSensor;

public abstract class AbstractDoubleSensorTest extends AbstractRuntimeTest {
	// for test method testSlidingAverage():
	private class OriginalSmoothedTime {
		private final double originalValue;
		private double smoothedValue;
		private final long timeInMillis;

		public OriginalSmoothedTime(double originalValue, double smoothedValue, long timeInMillis) {
			this.originalValue = originalValue;
			setSmoothedValue(smoothedValue);
			this.timeInMillis = timeInMillis;
		}

		public void setSmoothedValue(double smoothedValue) {
			this.smoothedValue = smoothedValue;
		}

		public double getOriginalValue() {
			return originalValue;
		}

		public double getSmoothedValue() {
			return smoothedValue;
		}

		public long getTimeInMillis() {
			return timeInMillis;
		}
	}

	// for test method testAtan2():
	private class XYAtan2 implements Comparable<XYAtan2> {
		public final double x;
		public final double y;
		public final double atan2Value;

		public XYAtan2(double x, double y, double atan2Value) {
			this.x = x;
			this.y = y;
			this.atan2Value = atan2Value;
		}

		@Override
		public int compareTo(XYAtan2 that) {
			int xCompareResult = Double.valueOf(x).compareTo(Double.valueOf(that.x));

			if (xCompareResult != 0) {
				return xCompareResult;
			} else {
				return Double.valueOf(y).compareTo(Double.valueOf(that.y));
			}
		}
	}

	private DoubleSensor mockDoubleSensor;
	private Command mockWaitCmd;

	// for test method testSlidingAverage():
	private double lastSetValue = 0;

	@Before
	public void setup() {
		mockDoubleSensor = new MockDoubleSensor(getRuntime());
		mockWaitCmd = getRuntime().createWaitCommand();
	}

	@After
	public void teardown() {
		mockDoubleSensor = null;
		mockWaitCmd = null;
	}

	@Test(timeout = 3000)
	public void testDoubleAbsSensorCalculatesAbsoluteValue() throws SensorReadException {
		DoubleFromJavaSensor sensor = new DoubleFromJavaSensor(0);

		DoubleSensor absSensor = sensor.abs();

		Assert.assertEquals(0d, absSensor.getCurrentValue(), 0.0001);

		sensor.setValue(1);

		Assert.assertEquals(1d, absSensor.getCurrentValue(), 0.0001);

		sensor.setValue(-3.1415);

		Assert.assertEquals(3.1415, absSensor.getCurrentValue(), 0.0001);
	}

	double value = 0;
	boolean wasCalled = false;

	@Test(timeout = 10000)
	public void testIsGreaterWithDoubleValue() {
		SensorState testState = mockDoubleSensor.isGreater(-0.1);

		assertNotNull(testState);

		try {
			assertTrue(mockWaitCmd.validateState(testState));
		} catch (RoboticsException e) {
			fail("Exception while validating State. Test will be aborted.");
		}
	}

	@Test(timeout = 10000)
	public void testIsGreaterWithDoubleMockSensor() {
		SensorState testState = mockDoubleSensor.isGreater(mockDoubleSensor.minus(0.1));

		assertNotNull(testState);

		try {
			assertTrue(mockWaitCmd.validateState(testState));
		} catch (RoboticsException e) {
			fail("Exception while validating State. Test will be aborted.");
		}
	}

	@Test(timeout = 10000)
	public void testIsLessWithMockDoubleSensor() {
		SensorState testState = mockDoubleSensor.isLess(mockDoubleSensor.add(0.1));

		assertNotNull(testState);

		try {
			assertTrue(mockWaitCmd.validateState(testState));
		} catch (RoboticsException e) {
			fail("Exception while validating State. Test will be aborted.");
		}
	}

	@Test(timeout = 10000)
	public void testIsEqualWithDoubleValueAndEpsilon() {
		State testState = mockDoubleSensor.isEqual(0.1, 0.001);

		assertNotNull(testState);

		try {
			assertTrue(mockWaitCmd.validateState(testState));
		} catch (RoboticsException e) {
			fail("Exception while validating State. Test will be aborted.");
		}
	}

	@Test
	// (timeout = 10000)
	public void testGetDefaultValueWithMockDoubleSensorSimpleTest() {
		MockDoubleSensor mockSensor = new MockDoubleSensor(getRuntime());

		assertEquals(0, mockSensor.getDefaultValue(), 0.001);
	}

	@Test(timeout = 15000)
	public void testSlidingAverage() throws RoboticsException, InterruptedException {
		final int DURATION_IN_SECONDS = 2;
		final int SLEEPTIME_IN_MILLISECONDS = 1000;

		final int[] VALUES = new int[] { 3, 1, 4, 2 };
		final int VALUES_COUNT = VALUES.length;

		final DoubleFromJavaSensor testSensor = new DoubleFromJavaSensor(VALUES[0]);
		final DoubleSensor smoothedSensor = testSensor.slidingAverage(DURATION_IN_SECONDS);

		final PersistContext<Double> persistContext = smoothedSensor.persist(getRuntime());

		final long T0 = System.currentTimeMillis();

		final List<OriginalSmoothedTime> smoothedValues = new ArrayList<OriginalSmoothedTime>();

		SensorListener<Double> listener = new SensorListener<Double>() {
			@Override
			public void onValueChanged(Double newValue) {
				smoothedValues.add(new OriginalSmoothedTime(lastSetValue, newValue, System.currentTimeMillis() - T0));
			}
		};

		persistContext.getSensor().addListener(listener);

		// measure procedure:
		for (int i = 1; i < VALUES_COUNT; i++) {
			lastSetValue = VALUES[i];
			testSensor.setValue(lastSetValue);

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		persistContext.getSensor().removeListener(listener);
		persistContext.unpersist();

		// remove redundant smoothedValues:
		for (int i = 1; i < smoothedValues.size();) {
			if (smoothedValues.get(i).getTimeInMillis() == smoothedValues.get(i - 1).getTimeInMillis()) {
				double average = (smoothedValues.get(i).getSmoothedValue()
						+ smoothedValues.get(i - 1).getSmoothedValue()) / 2;
				smoothedValues.get(i).setSmoothedValue(average);

				smoothedValues.remove(i - 1);
			} else {
				i++;
			}
		}

		// calculate expectedValues:
		final int EXPECTED_VALUES_COUNT = smoothedValues.size();

		final OriginalSmoothedTime[] expectedValues = new OriginalSmoothedTime[EXPECTED_VALUES_COUNT];

		final int MILLISECONDS_TO_LOOK_INTO_THE_PAST = DURATION_IN_SECONDS * 1000;

		int testValueIndex = 0;
		while ((testValueIndex < EXPECTED_VALUES_COUNT)
				&& (smoothedValues.get(testValueIndex).getTimeInMillis() < MILLISECONDS_TO_LOOK_INTO_THE_PAST)) {
			double smoothedValue = smoothedValues.get(0).getOriginalValue();

			for (int i = 1; i <= testValueIndex; i++) {
				smoothedValue += smoothedValues.get(i).getOriginalValue();
			}

			expectedValues[testValueIndex] = new OriginalSmoothedTime(smoothedValue / (testValueIndex + 1),
					smoothedValues.get(testValueIndex).getSmoothedValue(),
					smoothedValues.get(testValueIndex).getTimeInMillis());

			testValueIndex++;
		}

		for (int i = testValueIndex; i < EXPECTED_VALUES_COUNT; i++) {
			final double EARLIEST_TIME_TO_LOOK = smoothedValues.get(i).getTimeInMillis()
					- MILLISECONDS_TO_LOOK_INTO_THE_PAST;

			double smoothedValue = smoothedValues.get(i).getOriginalValue();

			int j = i - 1;
			while (j > 0 && smoothedValues.get(j).getTimeInMillis() >= EARLIEST_TIME_TO_LOOK) {
				smoothedValue += smoothedValues.get(j).getOriginalValue();

				j--;
			}

			expectedValues[i] = new OriginalSmoothedTime(smoothedValue / (i - j),
					smoothedValues.get(i).getSmoothedValue(), smoothedValues.get(i).getTimeInMillis());
		}

		// proper tests:

		// test 1: are all testValues between the lower and upper bound (min and
		// max)?
		double min = VALUES[0];
		double max = VALUES[0];
		for (int i = 1; i < VALUES_COUNT; i++) {
			if (VALUES[i] < min) {
				min = VALUES[i];
			} else if (VALUES[i] > max) {
				max = VALUES[i];
			}
		}

		for (int i = 0; i < EXPECTED_VALUES_COUNT; i++) {
			if ((expectedValues[i].getOriginalValue() < min) || (expectedValues[i].getSmoothedValue() < min)
					|| (expectedValues[i].getOriginalValue() > max) || (expectedValues[i].getSmoothedValue() > max)) {
				fail("At least one test value is outside the bounds.");
			}
		}

		// test 2: are the expected values equal to the tested values with
		// tolerance?
		final double TOLERANCE = 0.1;

		// neglect the first values:
		final int START_INDEX = EXPECTED_VALUES_COUNT / 4;

		for (int i = START_INDEX; i < EXPECTED_VALUES_COUNT; i++) {
			double diff = Math.abs(expectedValues[i].getOriginalValue() - expectedValues[i].getSmoothedValue());

			if (diff > TOLERANCE) {
				fail("At least one smoothed value is not equal its expected value. " + "The actual difference was "
						+ diff);
			}
		}
	}

	@Test(timeout = 15000)
	public void testAtan2OnGettingTheSameOutputValuesForTheSameInputValues()
			throws SensorReadException, InterruptedException {
		final int SLEEPTIME_IN_MILLISECONDS = 300;

		// define test values:
		final int[] X_VALUES = new int[] { 2, 2, 0, 0, -1, -1 };
		final int[] Y_VALUES = new int[] { 2, 2, -1, -1, 0, 0 };

		if (X_VALUES.length != Y_VALUES.length) {
			fail("The lengths of X_VALUES and Y_VALUES are not equal. " + "Test will be aborted.");
		}

		final int VALUES_COUNT = X_VALUES.length;

		// define Sensors:
		DoubleFromJavaSensor xSensor = new DoubleFromJavaSensor(X_VALUES[0]);
		DoubleFromJavaSensor ySensor = new DoubleFromJavaSensor(Y_VALUES[0]);

		final DoubleSensor atan2Sensor = DoubleSensor.atan2(ySensor, xSensor);

		final List<XYAtan2> atan2Values = new ArrayList<XYAtan2>();

		// measure procedure:
		double lastSetXValue = 0;
		double lastSetYValue = 0;

		for (int i = 1; i < VALUES_COUNT; i++) {
			lastSetXValue = X_VALUES[i];
			xSensor.setValue(lastSetXValue);

			lastSetYValue = Y_VALUES[i];
			ySensor.setValue(lastSetYValue);

			atan2Values.add(new XYAtan2(lastSetXValue, lastSetYValue, atan2Sensor.getCurrentValue()));

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		Thread.sleep(1000);

		// result the same input values the same output value?
		final int TEST_VALUES_COUNT = atan2Values.size();
		for (int i = 1; i < TEST_VALUES_COUNT; i++) {
			final XYAtan2 atan2ValuesIPrev = atan2Values.get(i - 1);
			final XYAtan2 atan2ValuesI = atan2Values.get(i);
			if ((atan2ValuesIPrev.compareTo(atan2ValuesI) == 0)
					&& (atan2ValuesIPrev.atan2Value != atan2ValuesI.atan2Value)) {
				fail("The test Atan2Sensor returned different results for the "
						+ "same input values. The input values were x = " + atan2ValuesI.x + " and y = "
						+ atan2ValuesI.y + ", the output values were atan2 = " + atan2ValuesIPrev.atan2Value
						+ " and atan2 = " + atan2ValuesI.atan2Value + ".");

			}
		}
	}

	@Test(timeout = 15000)
	public void testAtan2OnGettingTheExpectedValuesWithSeveralInputValues()
			throws SensorReadException, InterruptedException {
		final int SLEEPTIME_IN_MILLISECONDS = 300;

		// define test values:
		final int[] X_VALUES = new int[] { 2, 2, 2, 0, 0, 0, -1, -1, -1, };
		final int[] Y_VALUES = new int[] { 2, 0, -1, 2, 0, -1, 2, 0, -1, };

		if (X_VALUES.length != Y_VALUES.length) {
			fail("The lengths of X_VALUES and Y_VALUES are not equal. " + "Test will be aborted.");
		}

		final int VALUES_COUNT = X_VALUES.length;

		// define Sensors and SensorListener:
		DoubleFromJavaSensor xSensor = new DoubleFromJavaSensor(X_VALUES[0]);
		DoubleFromJavaSensor ySensor = new DoubleFromJavaSensor(Y_VALUES[0]);

		final DoubleSensor atan2Sensor = DoubleSensor.atan2(ySensor, xSensor);

		final List<XYAtan2> atan2Values = new ArrayList<XYAtan2>();

		// measure procedure:
		double lastSetXValue = 0;
		double lastSetYValue = 0;

		for (int i = 1; i < VALUES_COUNT; i++) {
			lastSetXValue = X_VALUES[i];
			xSensor.setValue(lastSetXValue);

			lastSetYValue = Y_VALUES[i];
			ySensor.setValue(lastSetYValue);

			atan2Values.add(new XYAtan2(lastSetXValue, lastSetYValue, atan2Sensor.getCurrentValue()));

			Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
		}

		// test Double.NaN:
		lastSetXValue = Double.NaN;
		xSensor.setValue(lastSetXValue);

		lastSetYValue = Double.NaN;
		ySensor.setValue(lastSetYValue);

		atan2Values.add(new XYAtan2(lastSetXValue, lastSetYValue, atan2Sensor.getCurrentValue()));

		Thread.sleep(SLEEPTIME_IN_MILLISECONDS);

		Thread.sleep(1000);

		// matches the measured values with the expected values?
		final double TOLERANCE = 0.001;

		final int TEST_VALUES_COUNT = atan2Values.size();
		for (int i = 0; i < TEST_VALUES_COUNT; i++) {
			final XYAtan2 atan2ValueI = atan2Values.get(i);
			final double expectedAtan2ValueI = Math.atan2(atan2ValueI.y, atan2ValueI.x);
			final double diff = Math.abs(expectedAtan2ValueI - atan2ValueI.atan2Value);
			if (diff > TOLERANCE) {
				fail("the value of atan2(" + atan2ValueI.y + ", " + atan2ValueI.x + ") was expected to be "
						+ expectedAtan2ValueI + " but was " + atan2ValueI.atan2Value);
			}

		}
	}

	@Test(timeout = 10000)
	public void testCalculateCheapValueOfClassUnaryFunctionDoubleSensorReturnsNull() {
		class MockUnaryFunctionDoubleSensor<T> extends UnaryFunctionDoubleSensor<T> {
			public MockUnaryFunctionDoubleSensor(Sensor<T> inner) {
				super(inner);
			}

			@Override
			protected Double computeCheapValue(T value) {
				return null;
			}
		}

		Sensor<Double> innerSensor = new MockUnaryFunctionDoubleSensor<Double>(mockDoubleSensor);

		UnaryFunctionDoubleSensor<Double> testSensor = new MockUnaryFunctionDoubleSensor<Double>(innerSensor);

		assertNull(testSensor.getCheapValue());
	}

	@Test(timeout = 10000)
	public void testCalculateCheapValueOfClassUnaryFunctionDoubleSensorWithTestValue() {
		class MockUnaryFunctionDoubleSensor<T> extends UnaryFunctionDoubleSensor<T> {
			public MockUnaryFunctionDoubleSensor(Sensor<T> inner) {
				super(inner);
			}

			@Override
			protected Double computeCheapValue(T value) {
				if (value.getClass().equals(Double.class)) {
					return (Double) getInnerSensor().getCheapValue();
				} else {
					return null;
				}
			}
		}

		Sensor<Double> innerSensor = new MockUnaryFunctionDoubleSensor<Double>(new DoubleFromJavaSensor(2.5));

		UnaryFunctionDoubleSensor<Double> testSensor = new MockUnaryFunctionDoubleSensor<Double>(innerSensor);

		assertEquals(2.5, testSensor.getCheapValue(), 0.001);
	}

	@Test(timeout = 15000)
	public void testMethodNamedPowerWithTestValuesExpectingMeasuredValuesEqualTestValues()
			throws InterruptedException, RoboticsException {
		// define test values:
		final Double[] BASES = new Double[] { Double.MIN_VALUE, -4.5, 0d, 15.2, Double.MAX_VALUE, Double.NaN };
		final Double[] EXPONENTS = new Double[] { -2.5, 0d, 3.5, (double) Double.MIN_EXPONENT,
				(double) Double.MAX_EXPONENT, Double.NaN };

		DoubleSensor measuredSensor = null;
		DoubleFromJavaSensor javaMeasureSensor = new DoubleFromJavaSensor(0);
		DoubleFromJavaSensor javaExponentSensor = new DoubleFromJavaSensor(0);

		PersistContext<Double> testPersist = javaMeasureSensor.persist(getRuntime());

		String messageIfValuesAreNotEqual = null;
		Double expectedValue = 0d;
		Double measuredValue = 0d;

		// measure procedure:
		for (int i = 0; i < BASES.length; i++) {
			javaMeasureSensor.setValue(BASES[i]);

			for (int j = 0; j < EXPONENTS.length; j++) {
				measuredSensor = (DoubleSensor) testPersist.getSensor();
				javaExponentSensor.setValue(EXPONENTS[j]);
				Thread.sleep(50);

				expectedValue = Math.pow(BASES[i], EXPONENTS[j]);

				measuredValue = (measuredSensor.power(javaExponentSensor)).getCurrentValue();

				messageIfValuesAreNotEqual = BASES[i] + " ^ " + EXPONENTS[j] + " is expected to be " + expectedValue
						+ " but the sensor measured " + measuredValue;

				assertEquals(messageIfValuesAreNotEqual, expectedValue, measuredValue, 0.001);

				Thread.sleep(100);
			}
		}

		Thread.sleep(1000);

		testPersist.unpersist();
	}
}
