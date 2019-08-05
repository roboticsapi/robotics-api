/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListenerRegistration;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.SensorRealtimeException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mockclass.MockSensor;

public abstract class AbstractSensorTest extends AbstractRuntimeTest {
	private class SpecialMockSensor<T> extends MockSensor<T> {
		public SpecialMockSensor(RoboticsRuntime runtime) {
			super(runtime);
			setAvailable(false);
		}

		void addListenersMock(List<SensorListenerRegistration<?>> listeners) throws RoboticsException {
			Sensor.addListeners(listeners);
		}

		RoboticsRuntime selectRuntimeMock(Sensor<?>... sensors) {
			return Sensor.selectRuntime(sensors);
		}

		@Override
		public List<SensorRealtimeException> defineSensorExceptions() {
			List<SensorRealtimeException> sreList = new ArrayList<SensorRealtimeException>();

			sreList.add(new SensorRealtimeException(null, "MockSensorRealtimeException"));

			return sreList;
		}
	}

	@Test(timeout = 15000)
	public void testSplitListenersByRuntimeUsingMockMethodAddListeners() {
		Sensor<Double> mockDblSensor = new SpecialMockSensor<Double>(getRuntime());
		Sensor<Integer> mockIntSensor = new SpecialMockSensor<Integer>(getRuntime());

		assertNotNull(mockDblSensor.getRuntime());
		assertNotNull(mockIntSensor.getRuntime());

		List<SensorListenerRegistration<?>> slrList = new ArrayList<SensorListenerRegistration<?>>();

		slrList.add(new SensorListenerRegistration<Double>(mockDblSensor, null));
		slrList.add(new SensorListenerRegistration<Integer>(mockIntSensor, null));

		try {
			(new SpecialMockSensor<Double>(getRuntime())).addListenersMock(slrList);
		} catch (RoboticsException e) {
			fail("Exception while adding Listener list. Test will be aborted.");
		}
	}

	@Test(timeout = 15000, expected = IllegalArgumentException.class)
	public void testSelectRuntimeWithMockMethodAndNullSensorExpectingException() {
		SpecialMockSensor<Double> mockSensor = new SpecialMockSensor<Double>(getRuntime());
		SpecialMockSensor<Double> mockAnotherSensor = null;

		mockSensor.selectRuntimeMock(mockAnotherSensor);
	}

	@Test(timeout = 15000, expected = IllegalArgumentException.class)
	public void testSelectRuntimeWithTwoMockSensorsWithDifferentRuntimesExpectingException() {
		Sensor<Double> mockDblSensor = new SpecialMockSensor<Double>(new SoftRobotRuntime());
		Sensor<Integer> mockIntSensor = new SpecialMockSensor<Integer>(new SoftRobotRuntime());

		(new SpecialMockSensor<Double>(getRuntime())).selectRuntimeMock(mockDblSensor, mockIntSensor);
	}

	@Test(timeout = 15000)
	public void testDefineSensorExceptionWithMockException() throws CommandException {
		assertNotNull(
				(new SpecialMockSensor<Double>(getRuntime())).defineSensorException(SensorRealtimeException.class));
	}

	@Test(timeout = 15000, expected = CommandException.class)
	public void testDefineSensorExceptionWithMockExceptionExpectingException() throws CommandException {
		class MockException extends SensorRealtimeException {
			private static final long serialVersionUID = 1L;

			public MockException(Sensor<?> sensor) {
				super(sensor);
			}
		}

		(new SpecialMockSensor<Double>(getRuntime())).defineSensorException(MockException.class);
	}

	@Test(timeout = 15000)
	public void testGetExceptionTypesWithMockSensorRealtimeExceptionList() {
		assertFalse((new SpecialMockSensor<Double>(getRuntime())).getExceptionTypes().isEmpty());
	}
}
