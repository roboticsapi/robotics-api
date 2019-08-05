/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.test.configuration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.startup.configuration.util.ConfigurationUtils;
import org.roboticsapi.startup.configuration.util.IllegalConfigurationException;

public class ConfigurationUtilsTest {

	public class TestRoboticsObject extends AbstractOnlineObject {

		String optionalProperty;

		public String getOptionalProperty() {
			return optionalProperty;
		}

		@ConfigurationProperty(Optional = true)
		public void setOptionalProperty(String property) {
			immutableWhenInitialized();
			this.optionalProperty = property;
		}

	}
	
	TestRoboticsObject object;

	public static class ComplexRoboticsObject extends AbstractOnlineObject {

		boolean booleanObject;
		int integerObject;
		double doubleObject;

		String stringValue;

		Object unknown;

		public boolean getBoolean() {
			return booleanObject;
		}

		@ConfigurationProperty(Optional = true)
		public void setBoolean(boolean b) {
			this.booleanObject = b;
		}

		public int getInteger() {
			return integerObject;
		}

		@ConfigurationProperty(Optional = true)
		public void setInteger(int i) {
			this.integerObject = i;
		}

		public double getDouble() {
			return doubleObject;
		}

		@ConfigurationProperty(Optional = true)
		public void setDouble(double d) {
			this.doubleObject = d;
		}

		public String getString() {
			return stringValue;
		}

		@ConfigurationProperty(Optional = true)
		public void setString(String stringValue) {
			this.stringValue = stringValue;
		}

		public Object getUnknown() {
			return unknown;
		}

		@ConfigurationProperty(Optional = true)
		public void setUnknown(Object unknown) {
			this.unknown = unknown;
		}

	}

	@Before
	public void setup() {
		object = new TestRoboticsObject();
		object.setName("testObject");
	}

	@Test
	public void testIsPropertyRequiredForRequiredProperty() {
		boolean result = false;

		try {
			result = ConfigurationUtils.isPropertyRequired(
					TestRoboticsObject.class, "name");
			Assert.assertTrue(result);

			result = ConfigurationUtils.isPropertyRequired(object, "name");
			Assert.assertTrue(result);
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testIsPropertyRequiredForOptionalProperty() {
		boolean result = false;

		try {
			result = ConfigurationUtils.isPropertyRequired(
					TestRoboticsObject.class, "optionalProperty");
			Assert.assertFalse(result);

			result = ConfigurationUtils.isPropertyRequired(object,
					"optionalProperty");
			Assert.assertFalse(result);
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testIsPropertyRequiredForUnknownProperty() {
		try {
			ConfigurationUtils.isPropertyRequired(TestRoboticsObject.class,
					"unknownProperty");
			Assert.fail("Should not know unknown property");
		} catch (IllegalConfigurationException e) {

		}

		try {
			ConfigurationUtils.isPropertyRequired(object, "unknownProperty");
			Assert.fail("Should not know unknown property");
		} catch (IllegalConfigurationException e) {

		}
	}

	@Test
	public void testSetPropertyCorrectType() {
		Assert.assertNotSame("newName", object.getName());

		try {
			ConfigurationUtils.setProperty(object, "name", "newName");
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}

		Assert.assertEquals("newName", object.getName());
	}

	@Test
	public void testSetPropertyWrongType() {
		try {
			ConfigurationUtils.setProperty(object, "name", new Object());
			Assert.fail("Should not set wrong type");
		} catch (IllegalConfigurationException e) {
		}
	}

	@Test
	public void testSetPropertyBoolean() {
		ComplexRoboticsObject cObject = new ComplexRoboticsObject();

		try {
			Assert.assertFalse(cObject.getBoolean());
			ConfigurationUtils.setProperty(cObject, "boolean", "true", null);
			Assert.assertTrue(cObject.getBoolean());
			ConfigurationUtils.setProperty(cObject, "boolean", "not a boolean",
					null);
			Assert.assertFalse(cObject.getBoolean());
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testSetPropertyInteger() {
		ComplexRoboticsObject cObject = new ComplexRoboticsObject();

		try {
			ConfigurationUtils.setProperty(cObject, "integer", "not a number",
					null);
			Assert.fail("Should not work");
		} catch (IllegalConfigurationException e) {
		}

		try {
			Assert.assertEquals(0, cObject.getInteger());
			ConfigurationUtils.setProperty(cObject, "integer", "1", null);
			Assert.assertEquals(1, cObject.getInteger());
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testSetPropertyDouble() {
		ComplexRoboticsObject cObject = new ComplexRoboticsObject();

		try {
			ConfigurationUtils.setProperty(cObject, "double", "not a number",
					null);
			Assert.fail("Should not work");
		} catch (IllegalConfigurationException e) {
		}

		try {
			Assert.assertEquals(0d, cObject.getDouble(), 0.001);
			ConfigurationUtils.setProperty(cObject, "double", "1.0", null);
			Assert.assertEquals(1d, cObject.getDouble(), 0.001);
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testSetPropertyString() {
		ComplexRoboticsObject cObject = new ComplexRoboticsObject();

		try {
			Assert.assertNull(cObject.getString());
			ConfigurationUtils.setProperty(cObject, "string", "a string", null);
			Assert.assertEquals("a string", cObject.getString());
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testSetPropertyUnknownType() {
		ComplexRoboticsObject cObject = new ComplexRoboticsObject();

		try {
			ConfigurationUtils.setProperty(cObject, "unknown", "some value",
					null);
			Assert.fail("Should not work");
		} catch (IllegalConfigurationException e) {
		}
	}

	@Test
	public void testContainsAllProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("name", "testObject");

		boolean result = ConfigurationUtils.containsAllProperties(
				TestRoboticsObject.class, properties);
		Assert.assertFalse(result);
		result = ConfigurationUtils.containsAllProperties(object, properties);
		Assert.assertFalse(result);

		properties.put("optionalProperty", "some value");

		result = ConfigurationUtils.containsAllProperties(
				TestRoboticsObject.class, properties);
		Assert.assertTrue(result);
		result = ConfigurationUtils.containsAllProperties(object, properties);
		Assert.assertTrue(result);

		properties.put("unknownProperty", "some other value");

		result = ConfigurationUtils.containsAllProperties(
				TestRoboticsObject.class, properties);
		Assert.assertTrue(result);
		result = ConfigurationUtils.containsAllProperties(object, properties);
		Assert.assertTrue(result);
	}

	@Test
	public void testContainsRequiredProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("unknownProperty", "some other value");

		boolean result = ConfigurationUtils.containsRequiredProperties(
				TestRoboticsObject.class, properties);
		Assert.assertFalse(result);
		result = ConfigurationUtils.containsRequiredProperties(object,
				properties);
		Assert.assertFalse(result);

		properties.put("name", "testObject");

		result = ConfigurationUtils.containsRequiredProperties(
				TestRoboticsObject.class, properties);
		Assert.assertTrue(result);
		result = ConfigurationUtils.containsRequiredProperties(object,
				properties);
		Assert.assertTrue(result);

		properties.put("optionalProperty", "some value");

		result = ConfigurationUtils.containsRequiredProperties(
				TestRoboticsObject.class, properties);
		Assert.assertTrue(result);
		result = ConfigurationUtils.containsRequiredProperties(object,
				properties);
		Assert.assertTrue(result);

	}

	@Test
	public void testGetRequiredPropertyKeys() {
		List<String> requiredProperties = ConfigurationUtils
				.getRequiredPropertyKeys(TestRoboticsObject.class);
		testAssertionsForListOfRequiredPropertyKeys(requiredProperties);

		requiredProperties = ConfigurationUtils.getRequiredPropertyKeys(object);
		testAssertionsForListOfRequiredPropertyKeys(requiredProperties);
	}

	private void testAssertionsForListOfRequiredPropertyKeys(
			List<String> requiredProperties) {
		Assert.assertNotNull(requiredProperties);
		Assert.assertEquals(1, requiredProperties.size());
		Assert.assertTrue(requiredProperties.contains("name"));
	}

	@Test
	public void testGetAllPropertyKeys() {
		List<String> allProperties = ConfigurationUtils.getPropertyKeys(
				TestRoboticsObject.class, true);
		testAssertionsForListOfAllPropertyKeys(allProperties);

		allProperties = ConfigurationUtils.getPropertyKeys(object, true);
		testAssertionsForListOfAllPropertyKeys(allProperties);
	}

	private void testAssertionsForListOfAllPropertyKeys(
			List<String> allProperties) {
		Assert.assertNotNull(allProperties);
		Assert.assertEquals(2, allProperties.size());
		Assert.assertTrue(allProperties.contains("name"));
		Assert.assertTrue(allProperties.contains("optionalProperty"));
	}

	@Test
	public void testGetPropertyType() {
		try {
			Class<?> propertyType = ConfigurationUtils.getPropertyType(
					TestRoboticsObject.class, "name");
			testAssertionsForPropertyType(propertyType);

			propertyType = ConfigurationUtils.getPropertyType(object, "name");
			testAssertionsForPropertyType(propertyType);
		} catch (IllegalConfigurationException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void testAssertionsForPropertyType(Class<?> propertyType) {
		Assert.assertNotNull(propertyType);
		Assert.assertEquals(String.class, propertyType);
	}

	@Test
	public void testGetPropertyTypeForUnknownProperty() {
		try {
			ConfigurationUtils.getPropertyType(TestRoboticsObject.class,
					"unknowProperty");
			Assert.fail("Unknown property key");
		} catch (IllegalConfigurationException e) {
		}

		try {
			ConfigurationUtils.getPropertyType(object, "unknowProperty");
			Assert.fail("Unknown property key");
		} catch (IllegalConfigurationException e) {
		}
	}

	@Test
	public void testGetPropertiesForRoboticsObject() {
		object.setOptionalProperty("optional");
		Map<String, String> properties = ConfigurationUtils
				.getProperties(object);

		Assert.assertNotNull(properties);
		Assert.assertEquals(2, properties.size());
		Assert.assertTrue(properties.containsKey("name"));
		Assert.assertEquals(object.getName(), properties.get("name"));
		Assert.assertTrue(properties.containsKey("optionalProperty"));
		Assert.assertEquals(object.getOptionalProperty(),
				properties.get("optionalProperty"));
	}

	@Test
	public void testGetPropertiesForRoboticsObjectClass() {
		Map<String, String> properties = ConfigurationUtils
				.getProperties(TestRoboticsObject.class);

		Assert.assertNotNull(properties);
		Assert.assertEquals(2, properties.size());
		Assert.assertTrue(properties.containsKey("name"));
		Assert.assertNull(properties.get("name"));
		Assert.assertTrue(properties.containsKey("optionalProperty"));
		Assert.assertNull(properties.get("optionalProperty"));
	}

}
