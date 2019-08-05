/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.AbsDoubleSensor;
import org.roboticsapi.core.sensor.BinaryFunctionDoubleSensor;
import org.roboticsapi.core.sensor.BooleanAtTimeSensor;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.ComparatorBooleanSensor;
import org.roboticsapi.core.sensor.ConstantBooleanSensor;
import org.roboticsapi.core.sensor.ConstantDoubleSensor;
import org.roboticsapi.core.sensor.DeviceBasedBooleanSensor;
import org.roboticsapi.core.sensor.DeviceBasedDoubleArraySensor;
import org.roboticsapi.core.sensor.DeviceBasedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleArrayFromDoubleSensor;
import org.roboticsapi.core.sensor.DoubleArrayFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleFromDoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.IntegerFromJavaSensor;
import org.roboticsapi.core.sensor.IntegerSensor;
import org.roboticsapi.core.sensor.NegatedBooleanSensor;
import org.roboticsapi.core.sensor.OrBooleanSensor;
import org.roboticsapi.core.sensor.SensorDataAgeSensor;
import org.roboticsapi.runtime.mockclass.MockBooleanSensor;
import org.roboticsapi.runtime.mockclass.MockComparatorBooleanSensor;
import org.roboticsapi.runtime.mockclass.MockDeviceDriverImpl;
import org.roboticsapi.runtime.mockclass.MockDoubleArraySensor;
import org.roboticsapi.runtime.mockclass.MockDoubleSensor;
import org.roboticsapi.runtime.mockclass.MockIntegerSensor;
import org.roboticsapi.runtime.mockclass.MockRoboticsRuntime;
import org.roboticsapi.runtime.mockclass.MockRotationSensor;
import org.roboticsapi.runtime.mockclass.MockSensor;
import org.roboticsapi.runtime.mockclass.MockTransformationArraySensor;
import org.roboticsapi.runtime.mockclass.MockVectorSensor;
import org.roboticsapi.runtime.mockclass.MockVelocitySensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.OrientationSensor;
import org.roboticsapi.world.sensor.PointSensor;
import org.roboticsapi.world.sensor.RotationComponentSensor;
import org.roboticsapi.world.sensor.RotationComponentSensor.RotationComponent;
import org.roboticsapi.world.sensor.RotationSensor;
import org.roboticsapi.world.sensor.TransformationArraySensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VectorSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

// TODO: This test case would be good as parameterized test
// (compare test.core.sensor.SensorMethodIsAvailableParameterizedTest):
public abstract class AbstractSensorMethodGetCheapValueTest extends AbstractRuntimeTest {
	// Tests for class Sensor<T>:

	@Test(timeout = 2000)
	public void testGetCheapValueOfClassSensorBooleanExpectingNull() {
		Sensor<Boolean> testBooleanSensor = new MockSensor<Boolean>(getRuntime());

		assertNull(testBooleanSensor.getCheapValue());
	}

	@Test(timeout = 2000)
	public void testGetCheapValueOfClassSensorIntegerExpectingNull() {
		Sensor<Integer> testIntegerSensor = new MockSensor<Integer>(getRuntime());

		assertNull(testIntegerSensor.getCheapValue());
	}

	@Test(timeout = 2000)
	public void testGetCheapValueOfClassSensorDoubleExpectingNull() {
		Sensor<Double> testDoubleSensor = new MockSensor<Double>(getRuntime());

		assertNull(testDoubleSensor.getCheapValue());
	}

	// Tests for class BooleanSensor:

	@Test(timeout = 2000)
	public void testInheritedMethodGetCheapValueOfSubclassBooleanSensorExpectingNull() {
		BooleanSensor testBooleanSensor = new MockBooleanSensor(getRuntime());

		assertNull(testBooleanSensor.getCheapValue());
	}

	// Tests for class DirectionSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassDirectionSensorExpectingNull() {
		VectorSensor testVectorSensor = new MockVectorSensor(getRuntime());
		Orientation testOrientation = null;

		DirectionSensor testDirectionSensor = new DirectionSensor(testVectorSensor, testOrientation);

		assertNull(testDirectionSensor.getCheapValue());
	}

	// Tests for class DoubleArraySensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleArraySensorExpectingNull() {
		final int SIZE = 2;
		DoubleArraySensor testDoubleArraySensor = new MockDoubleArraySensor(getRuntime(), SIZE);

		assertNull(testDoubleArraySensor.getCheapValue());
	}

	// Tests for class DoubleSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleSensorExpectingNull() {
		DoubleSensor testDoubleSensor = new MockDoubleSensor(getRuntime());

		assertNull(testDoubleSensor.getCheapValue());
	}

	// Tests for class IntegerSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassIntegerSensorExpectingNull() {
		IntegerSensor testIntegerSensor = new MockIntegerSensor(getRuntime());

		assertNull(testIntegerSensor.getCheapValue());
	}

	// Tests for class OrientationSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassOrientationSensorExpectingNull() {
		RotationSensor testRotationSensor = new MockRotationSensor(getRuntime());
		Frame testFrame = null;

		OrientationSensor testOrientationSensor = new OrientationSensor(testRotationSensor, testFrame);

		assertNull(testOrientationSensor.getCheapValue());
	}

	// Tests for class PointSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassPointSensorExpectingNull() {
		VectorSensor testVectorSensor = new MockVectorSensor(getRuntime());
		Frame testFrame = null;

		PointSensor testPointSensor = new PointSensor(testVectorSensor, testFrame);

		assertNull(testPointSensor.getCheapValue());
	}

	// Tests for class RotationSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationSensorExpectingNull() {
		RotationSensor testRotationSensor = new MockRotationSensor(getRuntime());

		assertNull(testRotationSensor.getCheapValue());
	}

	// Tests for class TransformationArraySensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassTransformationArraySensorExpectingNull() {
		final int SIZE = 2;
		TransformationArraySensor testTransformationArraySensor = new MockTransformationArraySensor(getRuntime(), SIZE);

		assertNull(testTransformationArraySensor.getCheapValue());
	}

	// Tests for class TransformationSensor:

	private class SpecialMockTransformationSensor extends TransformationSensor {
		public SpecialMockTransformationSensor(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		public boolean isAvailable() {
			return true;
		}
	}

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassTransformationSensorExpectingNull() {
		TransformationSensor testTransformationSensor = new SpecialMockTransformationSensor(getRuntime());

		assertNull(testTransformationSensor.getCheapValue());
	}

	// Tests for class VectorSensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassVectorSensorExpectingNull() {
		VectorSensor testVectorSensor = new MockVectorSensor(getRuntime());

		assertNull(testVectorSensor.getCheapValue());
	}

	// Tests for class VelocitySensor:

	@Test(timeout = 3000)
	public void testInheritedMethodGetCheapValueOfSubclassVelocitySensorExpectingNull() {
		Frame testFrame1 = null;
		Frame testFrame2 = null;
		Point testPoint = null;
		Orientation testOrientation = null;

		VelocitySensor testVelocitySensor = new MockVelocitySensor(getRuntime(), testFrame1, testFrame2, testPoint,
				testOrientation);

		assertNull(testVelocitySensor.getCheapValue());
	}

	// Tests for class BooleanAtTimeSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassBooleanAtTimeSensorExpectingNull() {
		BooleanSensor testBooleanSensor = new MockBooleanSensor(getRuntime());
		DoubleSensor testDoubleSensor = new MockDoubleSensor(getRuntime());
		final double MAX_AGE = 10;

		BooleanAtTimeSensor testBooleanAtTimeSensor = new BooleanAtTimeSensor(testBooleanSensor, testDoubleSensor,
				MAX_AGE);

		assertNull(testBooleanAtTimeSensor.getCheapValue());
	}

	// Tests for class BooleanFromJavaSensor:

	@Test(timeout = 2000)
	public void testInheritedMethodGetCheapValueOfSubclassBooleanFromJavaSensorSetFalseExpectingFalse() {
		BooleanFromJavaSensor testBooleanFromJavaSensorFalse = new BooleanFromJavaSensor(false);

		assertFalse(testBooleanFromJavaSensorFalse.getCheapValue());
	}

	@Test(timeout = 2000)
	public void testInheritedMethodGetCheapValueOfSubclassBooleanFromJavaSensorSetTrueExpectingTrue() {
		BooleanFromJavaSensor testBooleanFromJavaSensorTrue = new BooleanFromJavaSensor(true);

		assertTrue(testBooleanFromJavaSensorTrue.getCheapValue());
	}

	// Tests for class ComparatorBooleanSensor<T>:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorBooleanWithEqualValuesExpectingTrue() {
		Sensor<Boolean> testBooleanSensorLeft = new BooleanFromJavaSensor(true);
		Sensor<Boolean> testBooleanSensorRight = new BooleanFromJavaSensor(true);

		ComparatorBooleanSensor<Boolean> testBooleanComparatorBooleanSensor = new MockComparatorBooleanSensor<Boolean>(
				testBooleanSensorLeft, testBooleanSensorRight);

		assertTrue(testBooleanComparatorBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorBooleanWithDifferentValuesExpectingFalse() {
		Sensor<Boolean> testBooleanSensorLeft = new BooleanFromJavaSensor(false);
		Sensor<Boolean> testBooleanSensorRight = new BooleanFromJavaSensor(true);

		ComparatorBooleanSensor<Boolean> testBooleanComparatorBooleanSensor = new MockComparatorBooleanSensor<Boolean>(
				testBooleanSensorLeft, testBooleanSensorRight);

		assertFalse(testBooleanComparatorBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorDoubleWithEqualValuesExpectingTrue() {
		Sensor<Double> testDoubleSensorLeft = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorRight = new DoubleFromJavaSensor(1.5);

		ComparatorBooleanSensor<Double> testBooleanComparatorDoubleSensor = new MockComparatorBooleanSensor<Double>(
				testDoubleSensorLeft, testDoubleSensorRight);

		assertTrue(testBooleanComparatorDoubleSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorDoubleWithDifferentValuesExpectingFalse() {
		Sensor<Double> testDoubleSensorLeft = new DoubleFromJavaSensor(1.5);
		Sensor<Double> testDoubleSensorRight = new DoubleFromJavaSensor(-0.3);

		ComparatorBooleanSensor<Double> testBooleanComparatorDoubleSensor = new MockComparatorBooleanSensor<Double>(
				testDoubleSensorLeft, testDoubleSensorRight);

		assertFalse(testBooleanComparatorDoubleSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorIntegerWithEqualValuesExpectingTrue() {
		Sensor<Integer> testIntegerSensorLeft = new IntegerFromJavaSensor(2);
		Sensor<Integer> testIntegerSensorRight = new IntegerFromJavaSensor(2);

		ComparatorBooleanSensor<Integer> testBooleanComparatorIntegerSensor = new MockComparatorBooleanSensor<Integer>(
				testIntegerSensorLeft, testIntegerSensorRight);

		assertTrue(testBooleanComparatorIntegerSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorIntegerWithDifferentValuesExpectingFalse() {
		Sensor<Integer> testIntegerSensorLeft = new IntegerFromJavaSensor(2);
		Sensor<Integer> testIntegerSensorRight = new IntegerFromJavaSensor(-1);

		ComparatorBooleanSensor<Integer> testBooleanComparatorIntegerSensor = new MockComparatorBooleanSensor<Integer>(
				testIntegerSensorLeft, testIntegerSensorRight);

		assertFalse(testBooleanComparatorIntegerSensor.getCheapValue());
	}

	// Tests for class ConstantBooleanSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassConstantBooleanSensorWithTrueValueExpectingTrue() {
		ConstantBooleanSensor testConstantBooleanSensorTrue = new ConstantBooleanSensor(true);

		assertTrue(testConstantBooleanSensorTrue.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassConstantBooleanSensorWithFalseValueExpectingTrue() {
		ConstantBooleanSensor testConstantBooleanSensorFalse = new ConstantBooleanSensor(false);

		assertFalse(testConstantBooleanSensorFalse.getCheapValue());
	}

	// Tests for class DeviceBasedBooleanSensor<T>:

	private class MockDeviceBasedBooleanSensor<T extends DeviceDriver> extends DeviceBasedBooleanSensor<T> {
		public MockDeviceBasedBooleanSensor(T parent) {
			super(parent);
		}
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDeviceBasedBooleanSensorWithTestDeviceDriverExpectingNull() {
		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(getRuntime());

		DeviceBasedBooleanSensor<DeviceDriver> testDeviceBasedBooleanSensor = new MockDeviceBasedBooleanSensor<DeviceDriver>(
				testDeviceDriver);

		assertNull(testDeviceBasedBooleanSensor.getCheapValue());
	}

	// Tests for class NegatedBooleanSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassNegatedBooleanSensorWithFalseTestSensorExpectingTrue() {
		BooleanSensor testBooleanSensorFalse = new BooleanFromJavaSensor(false);

		NegatedBooleanSensor testNegatedBooleanSensor = new NegatedBooleanSensor(testBooleanSensorFalse);

		assertTrue(testNegatedBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassNegatedBooleanSensorWithTrueTestSensorExpectingFalse() {
		BooleanSensor testBooleanSensorTrue = new BooleanFromJavaSensor(true);

		NegatedBooleanSensor testNegatedBooleanSensor = new NegatedBooleanSensor(testBooleanSensorTrue);

		assertFalse(testNegatedBooleanSensor.getCheapValue());
	}

	// Tests for class OrBooleanSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassOrBooleanSensorWithOneTrueTestSensorExpectingTrue() {
		BooleanSensor testBooleanSensorTrue = new BooleanFromJavaSensor(true);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensorTrue);

		assertTrue(testOrBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassOrBooleanSensorWithOneFalseTestSensorExpectingFalse() {
		BooleanSensor testBooleanSensorFalse = new BooleanFromJavaSensor(false);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensorFalse);

		assertFalse(testOrBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassOrBooleanSensorWithTwoTrueTestSensorsExpectingTrue() {
		BooleanSensor testBooleanSensorTrue1 = new BooleanFromJavaSensor(true);
		BooleanSensor testBooleanSensorTrue2 = new BooleanFromJavaSensor(true);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensorTrue1, testBooleanSensorTrue2);

		assertTrue(testOrBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassOrBooleanSensorWithOneTrueAndOneFalseTestSensorExpectingTrue() {
		BooleanSensor testBooleanSensorTrue = new BooleanFromJavaSensor(true);
		BooleanSensor testBooleanSensorFalse = new BooleanFromJavaSensor(false);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensorTrue, testBooleanSensorFalse);

		assertTrue(testOrBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassOrBooleanSensorWithOneFalseAndOneTrueTestSensorExpectingTrue() {
		BooleanSensor testBooleanSensorFalse = new BooleanFromJavaSensor(false);
		BooleanSensor testBooleanSensorTrue = new BooleanFromJavaSensor(true);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensorFalse, testBooleanSensorTrue);

		assertTrue(testOrBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassOrBooleanSensorWithTwoFalseTestSensorsExpectingFalse() {
		BooleanSensor testBooleanSensorFalse1 = new BooleanFromJavaSensor(false);
		BooleanSensor testBooleanSensorFalse2 = new BooleanFromJavaSensor(false);

		OrBooleanSensor testOrBooleanSensor = new OrBooleanSensor(testBooleanSensorFalse1, testBooleanSensorFalse2);

		assertFalse(testOrBooleanSensor.getCheapValue());
	}

	// Tests for class DeviceBasedDoubleArraySensor<T>:

	private class MockDeviceBasedDoubleArraySensor<T extends DeviceDriver> extends DeviceBasedDoubleArraySensor<T> {
		public MockDeviceBasedDoubleArraySensor(T driver, int size) {
			super(driver, size);
		}

	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDeviceBasedDoubleArraySensorWithTestDeviceDriverExpectingNull() {
		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(null);

		DeviceBasedDoubleArraySensor<DeviceDriver> testDeviceBasedDoubleArraySensor = new MockDeviceBasedDoubleArraySensor<DeviceDriver>(
				testDeviceDriver, 2);

		assertNull(testDeviceBasedDoubleArraySensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleArrayFromDoubleSensorWithOneInnerSensorExpectingNotNull() {
		DoubleSensor[] testDoubleSensors = new DoubleSensor[1];
		testDoubleSensors[0] = new DoubleFromJavaSensor(1.5);

		DoubleArrayFromDoubleSensor testDoubleArrayFromDoubleSensor = new DoubleArrayFromDoubleSensor(
				testDoubleSensors);

		assertNotNull(testDoubleArrayFromDoubleSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleArrayFromDoubleSensorWithTwoInnerSensorsExpectingNotNull() {
		DoubleSensor[] testDoubleSensors = new DoubleSensor[2];
		testDoubleSensors[0] = new DoubleFromJavaSensor(1.5);
		testDoubleSensors[1] = new DoubleFromJavaSensor(-0.3);

		DoubleArrayFromDoubleSensor testDoubleArrayFromDoubleSensor = new DoubleArrayFromDoubleSensor(
				testDoubleSensors);

		assertNotNull(testDoubleArrayFromDoubleSensor.getCheapValue());
	}

	// Tests for class DoubleArrayFromJavaSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleArrayFromJavaSensorWithDoubleArrayWithOneElementExpectingTestDoubleArray() {
		final Double[] TEST_DOUBLE = new Double[] { 1.5 };

		DoubleArrayFromJavaSensor testDoubleArrayFromJavaSensor = new DoubleArrayFromJavaSensor(TEST_DOUBLE);

		assertSame(TEST_DOUBLE, testDoubleArrayFromJavaSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleArrayFromJavaSensorWithDoubleArrayWithTwoElementExpectingTestDoubleArray() {
		final Double[] TEST_DOUBLES = new Double[] { 1.5, -0.3 };

		DoubleArrayFromJavaSensor testDoubleArrayFromJavaSensor = new DoubleArrayFromJavaSensor(TEST_DOUBLES);

		assertSame(TEST_DOUBLES, testDoubleArrayFromJavaSensor.getCheapValue());
	}

	// Tests for class BinaryFunctionDoubleSensor:

	private class MockBinaryFunctionDoubleSensor extends BinaryFunctionDoubleSensor {
		public MockBinaryFunctionDoubleSensor(DoubleSensor sensor1, DoubleSensor sensor2) {
			super(sensor1, sensor2);
		}

		@Override
		public Double computeCheapValue(double value1, double value2) {
			return value1 + value2;
		}
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassBinaryFunctionDoubleSensorWithNullCheapValueOfFirstSensorExpectingNull() {
		DoubleSensor testDoubleSensor1 = new MockDoubleSensor(null);
		DoubleSensor testDoubleSensor2 = new DoubleFromJavaSensor(2);

		BinaryFunctionDoubleSensor testBinaryFunctionDoubleSensor = new MockBinaryFunctionDoubleSensor(
				testDoubleSensor1, testDoubleSensor2);

		assertNull(testBinaryFunctionDoubleSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassBinaryFunctionDoubleSensorWithNullCheapValueOfSecondSensorExpectingNull() {
		DoubleSensor testDoubleSensor1 = new DoubleFromJavaSensor(1);
		DoubleSensor testDoubleSensor2 = new MockDoubleSensor(null);

		BinaryFunctionDoubleSensor testBinaryFunctionDoubleSensor = new MockBinaryFunctionDoubleSensor(
				testDoubleSensor1, testDoubleSensor2);

		assertNull(testBinaryFunctionDoubleSensor.getCheapValue());
	}

	// Tests for class ConstantDoubleSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassConstantDoubleSensorWithTestDoubleValueExpectingTestValue() {
		final double TEST_DOUBLE = 2.6;

		ConstantDoubleSensor testConstantDoubleSensor = new ConstantDoubleSensor(TEST_DOUBLE);

		assertEquals(TEST_DOUBLE, testConstantDoubleSensor.getCheapValue(), 0.001);
	}

	// Tests for class DeviceBasedDoubleSensor<T>:

	private class MockDeviceBasedDoubleSensor<T extends DeviceDriver> extends DeviceBasedDoubleSensor<T> {
		public MockDeviceBasedDoubleSensor(T driver) {
			super(driver);
		}
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDeviceBasedDoubleSensorWithTestDeviceDriverExpectingNull() {
		DeviceDriver testDeviceDriver = new MockDeviceDriverImpl(null);

		DeviceBasedDoubleSensor<DeviceDriver> testDeviceBasedDoubleSensor = new MockDeviceBasedDoubleSensor<DeviceDriver>(
				testDeviceDriver);

		assertNull(testDeviceBasedDoubleSensor.getCheapValue());
	}

	// Tests for class DoubleFromJavaSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassDoubleFromJavaSensorWithTestDoubleValueExpectingTestValue() {
		final double TEST_DOUBLE = 3.4;

		DoubleFromJavaSensor testDoubleFromJavaSensor = new DoubleFromJavaSensor(TEST_DOUBLE);

		assertEquals(TEST_DOUBLE, testDoubleFromJavaSensor.getCheapValue(), 0.001);
	}

	// Tests for class ExponentiallySmootedDoubleSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithTestRotationSensorWithNullRoboticsRuntimeExpectingNull() {
		RotationSensor testRotationSensor = new MockRotationSensor(null);
		RotationComponent testRotationComponent = RotationComponent.A;

		RotationComponentSensor testRotationComponentSensor = new RotationComponentSensor(testRotationSensor,
				testRotationComponent);

		assertNull(testRotationComponentSensor.getCheapValue());
	}

	private class SpecialMockRotationSensor extends MockRotationSensor {
		private final Vector vector1 = new Vector(1, 0, 0);
		private final Vector vector2 = new Vector(0, Math.cos(Math.PI), -Math.sin(Math.PI));
		private final Vector vector3 = new Vector(0, Math.sin(Math.PI), Math.cos(Math.PI));

		public final Rotation rotation = new Rotation(vector1, vector2, vector3);

		public SpecialMockRotationSensor(RoboticsRuntime runtime) {
			super(runtime);
		}

		@Override
		protected Rotation calculateCheapValue() {
			return rotation;
		}
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentAExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorA = new RotationComponentSensor(testRotationSensor,
				RotationComponent.A);

		assertEquals(testRotationSensor.rotation.getA(), testRotationComponentSensorA.getCheapValue().doubleValue(),
				0.001);
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentBExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorB = new RotationComponentSensor(testRotationSensor,
				RotationComponent.B);

		assertEquals(testRotationSensor.rotation.getB(), testRotationComponentSensorB.getCheapValue().doubleValue(),
				0.001);
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentCExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorC = new RotationComponentSensor(testRotationSensor,
				RotationComponent.C);

		assertEquals(testRotationSensor.rotation.getC(), testRotationComponentSensorC.getCheapValue().doubleValue(),
				0.001);
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentQuaternionXExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorQuaternionX = new RotationComponentSensor(testRotationSensor,
				RotationComponent.QuaternionX);

		assertEquals(testRotationSensor.rotation.getQuaternion().getX(),
				testRotationComponentSensorQuaternionX.getCheapValue().doubleValue(), 0.001);
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentQuaternionYExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorQuaternionY = new RotationComponentSensor(testRotationSensor,
				RotationComponent.QuaternionY);

		assertEquals(testRotationSensor.rotation.getQuaternion().getY(),
				testRotationComponentSensorQuaternionY.getCheapValue().doubleValue(), 0.001);
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentQuaternionZExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorQuaternionZ = new RotationComponentSensor(testRotationSensor,
				RotationComponent.QuaternionZ);

		assertEquals(testRotationSensor.rotation.getQuaternion().getZ(),
				testRotationComponentSensorQuaternionZ.getCheapValue().doubleValue(), 0.001);
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassRotationComponentSensorWithMockRotationSensorAndRotationComponentQuaternionWExpecting___() {
		SpecialMockRotationSensor testRotationSensor = new SpecialMockRotationSensor(null);

		RotationComponentSensor testRotationComponentSensorQuaternionW = new RotationComponentSensor(testRotationSensor,
				RotationComponent.QuaternionW);

		assertEquals(testRotationSensor.rotation.getQuaternion().getW(),
				testRotationComponentSensorQuaternionW.getCheapValue().doubleValue(), 0.001);
	}

	// Tests for class SensorDataAgeSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassSensorDataAgeSensorWithMockSensorExpectingNull() {
		Sensor<Object> testSensor = new MockSensor<Object>(new MockRoboticsRuntime());

		SensorDataAgeSensor testSensorDataAgeSensor = new SensorDataAgeSensor(testSensor);

		assertNull(testSensorDataAgeSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassSensorDataAgeSensorWithTestDoubleFromJavaSensorExpectingNull() {
		DoubleFromJavaSensor testDoubleFromJavaSensor = new DoubleFromJavaSensor(6.4);

		SensorDataAgeSensor testSensorDataAgeSensor = new SensorDataAgeSensor(testDoubleFromJavaSensor);

		assertNull(testSensorDataAgeSensor.getCheapValue());
	}

	// Tests for class ComparatorBooleanSensor<T>:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorIntegerWithInnerLeftSensorHasNullValueExpectingNull() {
		Sensor<Integer> leftSensor = new MockSensor<Integer>(null);
		Sensor<Integer> rightSensor = new IntegerFromJavaSensor(2);

		ComparatorBooleanSensor<Integer> testComparatorBooleanSensor = new MockComparatorBooleanSensor<Integer>(
				leftSensor, rightSensor);

		assertNull(testComparatorBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorIntegerWithInnerRightSensorHasNullValueExpectingNull() {
		Sensor<Integer> leftSensor = new IntegerFromJavaSensor(1);
		Sensor<Integer> rightSensor = new MockSensor<Integer>(null);

		ComparatorBooleanSensor<Integer> testComparatorBooleanSensor = new MockComparatorBooleanSensor<Integer>(
				leftSensor, rightSensor);

		assertNull(testComparatorBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorIntegerWithTwoEqualInnerSensorsExpectingTrue() {
		Sensor<Integer> leftSensor = new IntegerFromJavaSensor(1);
		Sensor<Integer> rightSensor = new IntegerFromJavaSensor(1);

		ComparatorBooleanSensor<Integer> testComparatorBooleanSensor = new MockComparatorBooleanSensor<Integer>(
				leftSensor, rightSensor);

		assertTrue(testComparatorBooleanSensor.getCheapValue());
	}

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassComparatorBooleanSensorIntegerWithTwoDifferentInnerSensorsExpectingFalse() {
		Sensor<Integer> leftSensor = new IntegerFromJavaSensor(1);
		Sensor<Integer> rightSensor = new IntegerFromJavaSensor(2);

		ComparatorBooleanSensor<Integer> testComparatorBooleanSensor = new MockComparatorBooleanSensor<Integer>(
				leftSensor, rightSensor);

		assertFalse(testComparatorBooleanSensor.getCheapValue());
	}

	// Tests for class AbsDoubleSensor:

	@Test(timeout = 4000)
	public void testInheritedMethodGetCheapValueOfSubclassAbsDoubleSensorWithTestValuesExpectingAbsValues() {
		final Double[] TEST_DOUBLE_VALUES = new Double[] { Double.MIN_VALUE, -5.2, 0d, 6.3, Double.MAX_VALUE,
				Double.NaN };

		DoubleFromJavaSensor javaSensor = new DoubleFromJavaSensor(0);

		AbsDoubleSensor absSensor = new AbsDoubleSensor(javaSensor);

		for (int i = 0; i < TEST_DOUBLE_VALUES.length; i++) {
			javaSensor.setValue(TEST_DOUBLE_VALUES[i]);

			assertEquals(Math.abs(TEST_DOUBLE_VALUES[i]), absSensor.getCheapValue(), 0.001);
		}
	}

	// Test for subclass DoubleFromDoubleArraySensor:

	@Test(timeout = 10000)
	public void testInheritedMethodGetCheapValueOfClassDoubleFromDoubleArraySensorWithArraySizeFourExpectingCheapValuesEqualTestValues()
			throws RoboticsException {
		int testSize = 4;
		DoubleSensor[] singleSensors = new DoubleSensor[testSize];

		PersistContext<Double> persist;
		DoubleSensor javaSensor;

		for (int i = 0; i < testSize; i++) {
			javaSensor = new DoubleFromJavaSensor(i);
			persist = javaSensor.persist(getRuntime());
			singleSensors[i] = (DoubleSensor) persist.getSensor();
			persist.unpersist();
		}

		DoubleArraySensor arraySensor = DoubleArraySensor.fromSensors(singleSensors);

		DoubleFromDoubleArraySensor testSensor = null;

		for (int i = 0; i < testSize; i++) {
			testSensor = new DoubleFromDoubleArraySensor(arraySensor, i);

			assertEquals(singleSensors[i].getCheapValue(), testSensor.getCheapValue());
		}
	}
}
