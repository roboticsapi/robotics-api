/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.AbstractRuntime;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.RealtimeValueListenerRegistration;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.mockclass.MockSensorListenerImpl;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.runtime.mockclass.MockAbstractRuntime;

public class AbstractRuntimeClassTest {
	private AbstractRuntime mockRuntime = null;
	private RealtimeValueListener<Double> mockSensorListener = null;

	@Before
	public void setup() {
		mockRuntime = new MockAbstractRuntime();
		mockSensorListener = new MockSensorListenerImpl<Double>();
	}

	@After
	public void teardown() {
		mockRuntime = null;
		mockSensorListener = null;
	}

	@Test
	public void testAddCommandFilterAndRemoveCommandFilterSimpleTest() {
		CommandFilter testFilter = new CommandFilter() {
			@Override
			public boolean filter(Command command) {
				// Auto-generated method stub
				return false;
			}

			@Override
			public void process(Command command) {
				// empty implementation
			}
		};

		mockRuntime.addCommandFilter(testFilter);

		mockRuntime.removeCommandFilter(testFilter);
	}

	// @Test
	// public void testGetSensorValueExpectingDoubleValueOne()
	// throws RoboticsException {
	// Sensor<Double> testSensor = new DoubleFromJavaSensor(1);
	//
	// Double testValue = mockRuntime.getSensorValue(testSensor);
	// assertNotNull(testValue);
	//
	// // assertEquals(1, testValue, 0.001);
	// }

	@Test(expected = RoboticsException.class)
	public void testRemoveSensorListenersExpectingRoboticsException() throws RoboticsException {
		// Method removeSensorListeners(...) expects a List of
		// SensorListenerRegistration.
		// To create the expected RoboticsException, the List may not be empty.
		// A SensorListenerRegistration object expects a Sensor<T> and a
		// SensorListener<T>.
		RealtimeValueListener<Double> testListener = new MockSensorListenerImpl<Double>();

		// A different Sensor Runtime causes a RoboticsException:
		RealtimeValueListenerRegistration<Double> testReg = new RealtimeValueListenerRegistration<Double>(
				RealtimeDouble.createWritable(1), testListener);

		List<RealtimeValueListenerRegistration<?>> testListeners = new ArrayList<RealtimeValueListenerRegistration<?>>();

		testListeners.add(testReg);

		mockRuntime.removeRealtimeValueListeners(testListeners);
	}

	// // TODO: finish test
	// @Test
	// public void
	// testPrivateMethodUpdateObserverCommandTestPrintStackTraceIfRoboticsExceptionCaught()
	// {
	// // updateObserverCommand() is used in the constructor
	// }
	//
	// // TODO: finish test
	// @Test
	// public void
	// testOverrideMethodGetSensorValueWithSensorInListExpectingLastValueOfList()
	// throws RoboticsException {
	// // create SensorListenerRegistration list:
	// MockDoubleSensor testSensor = new MockDoubleSensor(mockRuntime);
	// testSensor.setAvailable(true);
	// SensorListener<Double> testListener = new
	// MockSensorListenerImpl<Double>();
	// SensorListenerRegistration<Double> testReg = new
	// SensorListenerRegistration<Double>(
	// testSensor, testListener);
	// List<SensorListenerRegistration<?>> testList = new
	// ArrayList<SensorListenerRegistration<?>>();
	// testList.add(testReg);
	//
	// // add testList:
	// mockRuntime.addSensorListeners(testList);
	//
	// // get sensorValue:
	// mockRuntime.getSensorValue(testSensor);
	// }
}
