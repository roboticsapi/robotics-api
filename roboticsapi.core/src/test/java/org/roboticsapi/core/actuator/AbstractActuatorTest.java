/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.TestRuntime;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActuatorRealtimeBoolean;

public class AbstractActuatorTest {

	@Test
	public void testCheckParameterBoundsSucceedsWhenParameterRepectsBounds() {
		AbstractActuator test = mock(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmock2 = mock(DeviceParameters.class);

		when(dpmock2.respectsBounds(dpmock1)).thenReturn(true);

		test.addMaximumParameters(dpmock1);

		test.checkParameterBounds(dpmock2);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckParameterBoundsThrowsWhenParameterDoesNotRepectBounds() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmock2 = mock(DeviceParameters.class);

		when(dpmock2.respectsBounds(dpmock1)).thenReturn(false);

		test.addMaximumParameters(dpmock1);

		test.checkParameterBounds(dpmock2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckParameterBoundsThrowsEvenWhenParameterRepectsSomeBounds() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmock2 = mock(DeviceParameters.class);
		DeviceParameters dpmock3 = mock(DeviceParameters.class);

		when(dpmock3.respectsBounds(dpmock1)).thenReturn(true);
		when(dpmock3.respectsBounds(dpmock2)).thenReturn(false);

		test.addMaximumParameters(dpmock1);
		test.addMaximumParameters(dpmock2);

		test.checkParameterBounds(dpmock3);
	}

	@Test
	public void testCheckParameterBoundsDoesNotThrownWhenCalledWithoutArgument() {
		AbstractActuator test = spy(AbstractActuator.class);

		test.checkParameterBounds();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckParameterBoundsThrowsIllegalArgumentExceptionWhenCalledWithNullParameters() {
		AbstractActuator test = spy(AbstractActuator.class);

		test.checkParameterBounds((DeviceParameters) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckParameterBoundsThrowsIllegalArgumentExceptionWhenCalledWithNullParameterBag() {
		AbstractActuator test = spy(AbstractActuator.class);

		test.checkParameterBounds((DeviceParameterBag) null);
	}

	@Test
	public void testCheckParameterBoundsChecksAllParametersGiven() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmockmax = mock(DeviceParameters.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmock2 = mock(DeviceParameters.class);

		when(dpmock1.respectsBounds(dpmockmax)).thenReturn(true);
		when(dpmock2.respectsBounds(dpmockmax)).thenReturn(true);

		test.addMaximumParameters(dpmockmax);

		test.checkParameterBounds(dpmock1, dpmock2);

		verify(dpmock1).respectsBounds(dpmockmax);
		verify(dpmock2).respectsBounds(dpmockmax);
	}

	@Test
	public void testCheckParameterBoundsChecksDeviceParameterBagGiven() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmockmax = mock(DeviceParameters.class);

		test.addMaximumParameters(dpmockmax);

		when(dpmock1.respectsBounds(dpmockmax)).thenReturn(true);

		DeviceParameterBag parameters = new DeviceParameterBag();
		parameters = parameters.withParameters(dpmock1);
		test.checkParameterBounds(parameters);

		verify(dpmock1).respectsBounds(dpmockmax);
	}

	@Test
	public void testAddDefaultParametersAddsParametersAsDefault() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);

		test.addDefaultParameters(dpmock1);

		Assert.assertTrue(test.getDefaultParameters().getAll().contains(dpmock1));
	}

	@Test
	public void testAddDefaultParametersTwiceReplacesFirstValue() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmock2 = mock(DeviceParameters.class);

		test.addDefaultParameters(dpmock2);

		Assert.assertTrue(test.getDefaultParameters().getAll().contains(dpmock2));
		Assert.assertFalse(test.getDefaultParameters().getAll().contains(dpmock1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDefaultParametersThrowsOnIllegalValue() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);
		DeviceParameters dpmock2 = mock(DeviceParameters.class);

		when(dpmock2.respectsBounds(dpmock1)).thenReturn(false);

		test.addMaximumParameters(dpmock1);

		test.addDefaultParameters(dpmock2);
	}

	@Test
	public void testAddDefaultParametersValidatesParameters() {
		AbstractActuator test = spy(AbstractActuator.class);

		DeviceParameters dpmock1 = mock(DeviceParameters.class);

		test.addDefaultParameters(dpmock1);

		verify(test).validateParameters(dpmock1);
	}

	@Test
	public void testGetCompletedStateScopesResultToGivenCommandAndRuntime() {
		AbstractActuator test = spy(AbstractActuator.class);

		RoboticsRuntime mockr = new TestRuntime();
		WaitCommand mock = new WaitCommand(mockr) {

			@Override
			protected CommandHandle createHandle() throws RoboticsException {
				// TODO Auto-generated method stub
				return null;
			}
		};

		ActuatorRealtimeBoolean completedState = test.getCompletedState(mock);
		Assert.assertEquals(mock, completedState.getScope());
		Assert.assertEquals(mockr, completedState.getRuntime());

	}

	@Test
	public void testGetCompletedStateReturnsScopeFreeResultOnNull() {
		AbstractActuator test = mock(AbstractActuator.class);

		ActuatorRealtimeBoolean completedState = test.getCompletedState(null);
		Assert.assertNull(completedState.getScope());
		Assert.assertNull(completedState.getRuntime());
	}
}
