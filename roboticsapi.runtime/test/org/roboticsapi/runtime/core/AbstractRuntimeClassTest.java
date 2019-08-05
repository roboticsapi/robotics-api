/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.SensorListenerRegistration;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.AbstractRuntime;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.runtime.mockclass.MockAbstractRuntime;

public class AbstractRuntimeClassTest {
	private AbstractRuntime mockRuntime = null;
	private SensorListener<Double> mockSensorListener = null;

	@Before
	public void setup() {
		mockRuntime = new MockAbstractRuntime();
		mockSensorListener = new SensorListener<Double>() {
			@Override
			public void onValueChanged(Double newValue) {
			}
		};
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

	@Test(expected = RoboticsException.class)
	public void testAddSensorListenersWithInvalidRuntimeExpectingException() throws RoboticsException {
		DoubleFromJavaSensor testSensor = new DoubleFromJavaSensor(0);

		SensorListenerRegistration<Double> reg = new SensorListenerRegistration<Double>(testSensor, mockSensorListener);

		List<SensorListenerRegistration<?>> regList = new ArrayList<SensorListenerRegistration<?>>();
		regList.add(reg);

		mockRuntime.addSensorListeners(regList);
	}

	@Test(expected = RoboticsException.class)
	public void testRemoveSensorListenersExpectingRoboticsException() throws RoboticsException {
		// Method removeSensorListeners(...) expects a List of
		// SensorListenerRegistration.
		// To create the expected RoboticsException, the List may not be empty.
		// A SensorListenerRegistration object expects a Sensor<T> and a
		// SensorListener<T>.
		SensorListener<Double> testListener = mockSensorListener;

		// A different Sensor Runtime causes a RoboticsException:
		SensorListenerRegistration<Double> testReg = new SensorListenerRegistration<Double>(new DoubleFromJavaSensor(1),
				testListener);

		List<SensorListenerRegistration<?>> testListeners = new ArrayList<SensorListenerRegistration<?>>();

		testListeners.add(testReg);

		mockRuntime.removeSensorListeners(testListeners);
	}

}
