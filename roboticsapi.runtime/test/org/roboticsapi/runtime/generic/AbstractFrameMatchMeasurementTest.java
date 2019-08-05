/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.measurement.FrameMatchMeasurement;

public abstract class AbstractFrameMatchMeasurementTest extends AbstractRuntimeTest {
	private FrameMatchMeasurement testFrameMatchMeasurement = null;

	@Before
	public void setup() {
		testFrameMatchMeasurement = new FrameMatchMeasurement();
	}

	@After
	public void teardown() {
		testFrameMatchMeasurement = null;
	}

	@Test(timeout = 2000)
	public void testMethodSetFromAndGetFromWithTestFrameExpectingTheTestFrame() {
		assertNull(testFrameMatchMeasurement.getFrom());

		Frame testFrame = new Frame();

		testFrameMatchMeasurement.setFrom(testFrame);

		assertEquals(testFrame, testFrameMatchMeasurement.getFrom());
	}

	@Test(timeout = 2000)
	public void testMethodSetToAndGetToWithTestFrameExpectingTheTestFrame() {
		assertNull(testFrameMatchMeasurement.getTo());

		Frame testFrame = new Frame();

		testFrameMatchMeasurement.setTo(testFrame);

		assertEquals(testFrame, testFrameMatchMeasurement.getTo());
	}

	@Test(timeout = 2000)
	public void testMethodSetMaximumAgeAndGetMaximumAgeWithPositiveTestDoubleValueExpectingTheTestValue() {
		double testMaximumAge = 4.9;

		testFrameMatchMeasurement.setMaximumAge(testMaximumAge);

		assertEquals(testMaximumAge, testFrameMatchMeasurement.getMaximumAge(), 0.001);
	}

	@Test(timeout = 2000)
	public void testMethodSetMeasuredFrameAndMethodGetMeasuredFrameWithTestFrameExpectingTheTestFrame() {
		assertNull(testFrameMatchMeasurement.getMeasuredFrame());

		Frame testFrame = new Frame();

		testFrameMatchMeasurement.setMeasuredFrame(testFrame);

		assertEquals(testFrame, testFrameMatchMeasurement.getMeasuredFrame());
	}

	@Test(timeout = 2000)
	public void testMethodSetMeasurementFrameAndMethodGetMeasurementFrameWithTestFrameExpectingTheTestFrame() {
		assertNull(testFrameMatchMeasurement.getMeasurementFrame());

		Frame testFrame = new Frame();

		testFrameMatchMeasurement.setMeasurementFrame(testFrame);

		assertEquals(testFrame, testFrameMatchMeasurement.getMeasurementFrame());
	}

	@Test(timeout = 2000)
	public void testMethodSetRuntimeAndMethodGetRuntimeWithSoftRobotRuntimeExpectingTheSoftRobotRuntime() {
		assertNull(testFrameMatchMeasurement.getRuntime());

		testFrameMatchMeasurement.setRuntime(getRuntime());

		assertEquals(getRuntime(), testFrameMatchMeasurement.getRuntime());
	}

	@Test(timeout = 2000)
	public void testMethodSetSmoothLengthAndMethodGetSmoothLengthPositiveTestDoubleValueExpectingTheTestValue() {
		double testSmoothLength = 4.9;

		testFrameMatchMeasurement.setSmoothLength(testSmoothLength);

		assertEquals(testSmoothLength, testFrameMatchMeasurement.getSmoothLength(), 0.001);
	}

	private void initializeTestFrameMatchMeasurement(RoboticsContext testInitializationContext)
			throws InitializationException {

		Frame fromFrame = new Frame();
		Frame toFrame = new Frame();
		Frame measurementFrame = new Frame();
		Frame measuredFrame = new Frame();

		testInitializationContext.initialize(fromFrame);
		testInitializationContext.initialize(toFrame);
		testInitializationContext.initialize(measurementFrame);
		testInitializationContext.initialize(measuredFrame);

		testFrameMatchMeasurement.setFrom(fromFrame);
		testFrameMatchMeasurement.setTo(toFrame);
		testFrameMatchMeasurement.setMeasurementFrame(measurementFrame);
		testFrameMatchMeasurement.setMeasuredFrame(measuredFrame);

		testFrameMatchMeasurement.setRuntime(getRuntime());

		testInitializationContext.initialize(testFrameMatchMeasurement);
	}

	@Test(timeout = 3000)
	public void testOverrideProtectedMethodValidateConfigurationPropertiesByUsingInheritedMethodInitializedExpectingIsInitializedTrue()
			throws InitializationException {
		assertFalse(testFrameMatchMeasurement.isInitialized());

		RoboticsContext testInitializationContext = new RoboticsContextImpl("dummy");
		initializeTestFrameMatchMeasurement(testInitializationContext);

		assertTrue(testFrameMatchMeasurement.isInitialized());
	}

	@Test(timeout = 5000)
	public void testOverrideProtectedMethodUndoInitializationByUsingInheritedMethodUninitializeWithInitializedTestFrameMatchMeasurementExpectingIsInitializedFalse()
			throws InitializationException {
		assertFalse(testFrameMatchMeasurement.isInitialized());

		RoboticsContext testInitializationContext = new RoboticsContextImpl("dummy");
		initializeTestFrameMatchMeasurement(testInitializationContext);

		assertTrue(testFrameMatchMeasurement.isInitialized());

		testInitializationContext.uninitialize(testFrameMatchMeasurement);

		assertFalse(testFrameMatchMeasurement.isInitialized());
	}
}
