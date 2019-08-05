/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.roboticsapi.core.exception.ConfigurationException;

public class ConfigurationExceptionTest {
	@Test
	public void testConstructorWithTwoStringArgumentsSimpleTest() {
		assertNotNull(new ConfigurationException("TestKey", "TestConfigurationException"));
	}

	@Test
	public void testConstructorWithTwoStringAndExceptionArgumentsSimpleTest() {
		assertNotNull(new ConfigurationException("TestKey", "TestConfigurationException", new Exception()));
	}

	@Test
	public void testGetKeyWithTestKeySimpleTest() {
		ConfigurationException ce = new ConfigurationException("TestKey", "TestConfigurationException");

		assertEquals("TestKey", ce.getKey());
	}
}
