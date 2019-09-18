/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Test;

// only unit tests
public class DeviceParameterBagTest {
	private class MockDeviceParameterBag extends DeviceParameterBag {
		public final boolean isElementAssignableFromMock(Class<?> target) {
			return isElementAssignableFrom(target);
		}
	}

	@Test
	public void testIsElementAssignableFromWithMockClass() {
		class MockClass {
		}

		new MockDeviceParameterBag().isElementAssignableFromMock(MockClass.class);
	}
}
