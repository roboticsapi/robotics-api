/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.roboticsapi.core.Device;
import org.roboticsapi.mockclass.MockDeviceImpl;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.TeachingInfo;

public class TeachingInfoTest {
	private final Device testDevice = new MockDeviceImpl();
	private final Frame testMotionCenter = new Frame();
	private final double[] testHintParameters = new double[] { 0.5, 1.5, 2.5 };

	private final TeachingInfo testTeachingInfo = new TeachingInfo(testDevice, testMotionCenter, testHintParameters);

	@Test
	public void testGetDeviceExpectingTestDevice() {
		assertEquals(testDevice, testTeachingInfo.getDevice());
	}

	@Test
	public void testGetMotionCenterExpectingTestMotionCenter() {
		assertEquals(testMotionCenter, testTeachingInfo.getMotionCenter());
	}

	@Test
	public void testGetHintParametersExpectingTestHintParameters() {
		for (int i = 0; i < testHintParameters.length; i++) {
			assertEquals(testHintParameters[i], testTeachingInfo.getHintParameters()[i], 0.001);
		}
	}
}
