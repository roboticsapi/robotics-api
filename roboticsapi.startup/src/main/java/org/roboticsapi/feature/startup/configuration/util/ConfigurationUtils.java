/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.util.RAPILogger;

/**
 * This class contains various methods for manipulating configuration properties
 * of {@link RoboticsObject}s.
 *
 * @see ConfigurationProperty
 */
public class ConfigurationUtils {

	public static boolean containsAllProperties(Class<? extends RoboticsObject> clazz, Map<String, String> properties) {
		return containsKeys(getPropertyKeys(clazz, true), properties);
	}

	public static boolean containsAllProperties(RoboticsObject object, Map<String, String> properties) {
		return containsAllProperties(object.getClass(), properties);
	}

	public static boolean containsRequiredProperties(Class<? extends RoboticsObject> clazz,
			Map<String, String> properties) {
		return containsKeys(getPropertyKeys(clazz, false), properties);
	}

	public static boolean containsRequiredProperties(RoboticsObject object, Map<String, String> properties) {
		return containsRequiredProperties(object.getClass(), properties);
	}

	public static Map<String, String> getProperties(Class<? extends RoboticsObject> clazz) {
		return getProperties(clazz, null);
	}

	public static Map<String, String> getProperties(RoboticsObject object) {
		return getProperties(object.getClass(), object);
	}

	/**
	 * Retrieves a list of all configuration property keys for a given
	 * {@link RoboticsObject}.
	 *
	 * @param targetClass     the class of the {@link RoboticsObject} to inspect
	 * @param includeOptional flag indicating if optional properties should be
	 *                        included in the list.
	 * @return a list of all configuration property keys
	 */
	public static List<String> getPropertyKeys(Class<? extends RoboticsObject> targetClass, boolean includeOptional) {
		List<String> ret = new ArrayList<String>();

		Method[] methods = targetClass.getMethods();

		for (Method method : methods) {
			if (isSetter(method, !includeOptional)) {
				String key = getKey(method);
				if (key != null) {
					if (isArray(method)) {
						key += "[]";
					}
					ret.add(key);
				}
			}
		}
		return ret;
	}

	public static <T extends RoboticsRuntime> boolean matches(T o1, T o2) throws IllegalConfigurationException {
		// TODO implement...

		// first check if they are equal...
		if (o1.equals(o2)) {
			return true;
		}
		// check if they have the same type
		Class<? extends RoboticsRuntime> class1 = o1.getClass();
		Class<? extends RoboticsRuntime> class2 = o2.getClass();

		if (!class1.equals(class2)) {
			return false;
		}
		// finally compare their identification properties...
		return compareIdentificationProperties(o1, o2);
	}

	/**
	 * Test whether two devices are "physically the same".
	 *
	 * @param o1 the first device to compare.
	 * @param o2 the second device to compare
	 * @return {@code true} if this object is physically the same as the other
	 *         object; {@code false} otherwise.
	 * @throws IllegalConfigurationException
	 */
	public static <T extends Device> boolean matches(T o1, T o2) throws IllegalConfigurationException {
		// TODO implement...

		// first check if they are equal...
		if (o1.equals(o2)) {
			return true;
		}
		// check if they have the same type
		Class<?> class1 = o1.getClass();
		Class<?> class2 = o2.getClass();

		if (!class1.equals(class2)) {
			return false;
		}

		// finally compare their identification properties...
		return compareIdentificationProperties(o1, o2);
	}

	private static <T extends RoboticsObject> boolean compareIdentificationProperties(T o1, T o2)
			throws IllegalConfigurationException {
		List<String> keys = getIdentificationPropertyKeys(o1.getClass());

		for (String key : keys) {
			if (!compareProperty(key, o1, o2)) {
				return false;
			}
		}
		return true;
	}

	private static <T extends RoboticsObject> boolean compareProperty(String propertyKey, T o1, T o2)
			throws IllegalConfigurationException {
		Object result1 = getPropertyValue(propertyKey, o1);
		Object result2 = getPropertyValue(propertyKey, o2);

		// first check if it is null
		if (result1 == null) {
			return result2 == null;
		}

		// check if it is a device (they should match)
		// if(result1 instanceof Device) {
		// Device d1 = (Device) result1;
		// Device d2 = (Device) result2;
		//
		// return matches(d1, d2);
		// }

		return result1.equals(result2);
	}

	public static <T extends RoboticsObject> Object getPropertyValue(String propertyKey, T o)
			throws IllegalConfigurationException {
		Method getter = getGetter(o.getClass(), propertyKey);

		if (getter == null) {
			throw new IllegalConfigurationException(propertyKey, "Cannot find getter of given property!");
		}

		try {
			Class<?>[] parameterTypes = getter.getParameterTypes();

			if (parameterTypes.length == 0) {
				return getter.invoke(o);
			}

			if (parameterTypes.length == 1) {
				int index = getArrayIndex(propertyKey);
				return getter.invoke(o, index);
			}

			throw new IllegalConfigurationException(propertyKey,
					"Cannot get property value due to illegal getter method!");
		} catch (IllegalConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalConfigurationException(propertyKey, e);
		}
	}

	private static List<String> getIdentificationPropertyKeys(Class<? extends RoboticsObject> targetClass) {
		List<String> ret = new ArrayList<String>();
		Method[] methods = targetClass.getMethods();

		for (Method method : methods) {
			if (method.getName().toLowerCase().startsWith("set")) {
				ConfigurationProperty annotation = method.getAnnotation(ConfigurationProperty.class);

				if (annotation != null && annotation.KeyProperty()) {
					String key = method.getName().substring(3);
					ret.add(key);
				}
			}
		}
		return ret;
	}

	/**
	 * Retrieves a list of all configuration property keys for a given
	 * {@link RoboticsObject}.
	 *
	 * @param object          the {@link RoboticsObject} to inspect
	 * @param includeOptional flag indicating if optional properties should be
	 *                        included in the list.
	 * @return a list of all configuration property keys
	 */
	public static List<String> getPropertyKeys(RoboticsObject object, boolean includeOptional) {
		return getPropertyKeys(object.getClass(), includeOptional);
	}

	/**
	 * Retrieves the type of a configuration property.
	 *
	 * @param object the {@link RoboticsObject} to inspect
	 * @param key    key of the configuration property
	 * @return the type of the configuration property
	 * @throws IllegalConfigurationException if the property does not exist.
	 */
	public static Class<?> getPropertyType(RoboticsObject object, String key) throws IllegalConfigurationException {
		return getPropertyType(object.getClass(), key);
	}

	/**
	 * Retrieves the type of a configuration property.
	 *
	 * @param targetClass the class of the {@link RoboticsObject} to inspect
	 * @param key         key of the configuration property
	 * @return the type of the configuration property
	 * @throws IllegalConfigurationException if the property does not exist.
	 */
	public static Class<?> getPropertyType(Class<? extends RoboticsObject> targetClass, String key)
			throws IllegalConfigurationException {
		Method getter = getGetter(targetClass, key);

		if (getter == null) {
			throw new IllegalConfigurationException(key, "The property " + key + " does not exist");
		}
		return getter.getReturnType();
	}

	public static List<String> getRequiredPropertyKeys(RoboticsObject object) {
		return getPropertyKeys(object, false);
	}

	public static List<String> getRequiredPropertyKeys(Class<? extends RoboticsObject> clazz) {
		return getPropertyKeys(clazz, false);
	}

	/**
	 * Checks whether a configuration property is required.
	 *
	 * @param object the {@link RoboticsObject} to check
	 * @param key    key of the configuration property to check
	 * @return {@code true} if the property is required; {@code false} otherwise.
	 * @throws IllegalConfigurationException if the property does not exist.
	 */
	public static boolean isPropertyRequired(RoboticsObject object, String key) throws IllegalConfigurationException {
		return isPropertyRequired(object.getClass(), key);
	}

	/**
	 * Checks whether a configuration property is required.
	 *
	 * @param targetClass the class if the {@link RoboticsObject} to check
	 * @param key         key of the configuration property to check
	 * @return {@code true} if the property is required; {@code false} otherwise.
	 * @throws IllegalConfigurationException if the property does not exist.
	 */
	public static boolean isPropertyRequired(Class<? extends RoboticsObject> targetClass, String key)
			throws IllegalConfigurationException {
		Method setter = getSetter(targetClass, key);

		if (setter == null) {
			throw new IllegalConfigurationException(key, "The property " + key + " does not exist");
		}
		return !setter.getAnnotation(ConfigurationProperty.class).Optional();
	}

	public static void setProperty(RoboticsObject object, String key, String value, List<RoboticsObject> objects)
			throws IllegalConfigurationException {
		Method setter = getSetter(object.getClass(), key);

		if (setter != null) {
			setValue(object, setter, key, value, objects);
		} else {
			RAPILogger.getLogger(ConfigurationUtils.class).warning("Configuration property '" + key + "' of type '"
					+ object.getClass().getName() + "' does not exist.");
		}
	}

	public static void setProperty(RoboticsObject object, String key, Object value)
			throws IllegalConfigurationException {
		Method setter = getSetter(object.getClass(), key);

		if (setter != null) {
			setValue(object, setter, key, value);
		} else {
			RAPILogger.getLogger(ConfigurationUtils.class).warning("Configuration property '" + key + "' of type '"
					+ object.getClass().getName() + "' does not exist.");
		}
		// TODO: Throw exception when property is unknown
	}

	public static void setPropertyFromString(RoboticsObject object, String key, String value)
			throws IllegalConfigurationException {
		Method setter = getSetter(object.getClass(), key);
		if (setter != null) {
			setValue(object, setter, key, value, new ArrayList<RoboticsObject>());
		} else {
			RAPILogger.getLogger(ConfigurationUtils.class).warning("Configuration property '" + key + "' of type '"
					+ object.getClass().getName() + "' does not exist.");
		}
	}

	/**
	 * Sets the configuration properties of a {@link RoboticsObject}.
	 *
	 * @param object        the robotics object
	 * @param properties    a map containing the properties as strings.
	 * @param objects       a list of already built {@link RoboticsObject}s for
	 *                      setting {@link RoboticsRuntime}s and other
	 *                      {@link Device}s as configuration properties
	 * @param forceRequired flag indicating if required configuration properties
	 *                      need to be set.
	 * @throws IllegalConfigurationException if an error occurs during setting the
	 *                                       properties.
	 */
	public static void setProperties(RoboticsObject object, Map<String, String> properties,
			List<RoboticsObject> objects, boolean forceRequired, List<String> futureObjects)
			throws IllegalConfigurationException {
		setProperties(object, properties, objects, forceRequired, true, futureObjects);
	}

	/**
	 * Sets the configuration properties of a {@link RoboticsObject}.
	 *
	 * @param object        the robotics object
	 * @param properties    a map containing the properties as objects.
	 * @param forceRequired flag indicating if required configuration properties
	 *                      need to be set.
	 * @throws IllegalConfigurationException if an error occurs during setting the
	 *                                       properties.
	 */
	public static void setProperties(RoboticsObject object, Map<String, Object> properties, boolean forceRequired)
			throws IllegalConfigurationException {
		setProperties(object, properties, null, forceRequired, false, new ArrayList<String>());
	}

	private static boolean containsKeys(List<String> requiredKeys, Map<String, ? extends Object> properties) {
		for (String key : requiredKeys) {
			if (!properties.containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	private static Map<String, String> getProperties(Class<? extends RoboticsObject> clazz, RoboticsObject object) {
		Map<String, String> ret = new HashMap<String, String>();

		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (isSetter(method, false)) {
				String key = method.getName().substring(3);
				key = key.substring(0, 1).toLowerCase() + key.substring(1);
				String value = null;
				// TODO: handle arrays
				if (object != null) {
					try {
						Method getter = getGetter(clazz, key);

						if (getter != null) {
							Object v = getter.invoke(object);

							if (v != null) {
								if (v instanceof RoboticsObject) {
									value = ((RoboticsObject) v).getName();
								} else {
									value = v.toString();
								}
							}
						}
					} catch (SecurityException e) {
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					}
				}
				ret.put(key, value);
			}
		}
		return ret;
	}

	private static boolean isArray(Method method) {
		return method.getParameterTypes().length == 2;
	}

	private static String getKey(Method method) {
		try {
			StringBuilder name = new StringBuilder(method.getName());
			name = name.delete(0, 3);
			String s = name.substring(0, 1).toLowerCase();
			return name.replace(0, 1, s).toString();
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}

	private static void setProperties(RoboticsObject object, Map<String, ? extends Object> properties,
			List<RoboticsObject> objects, boolean forceRequired, boolean string, List<String> futureObjects)
			throws IllegalConfigurationException {
		Method[] methods = object.getClass().getMethods();

		for (Method method : methods) {
			ConfigurationProperty annotation = method.getAnnotation(ConfigurationProperty.class);

			if (annotation != null) {
				try {
					String key = getKey(method);

					if (key == null) {
						continue;
					}

					if (isArray(method)) {

						for (String pkey : properties.keySet()) {
							if (pkey.startsWith(key + "[")) {
								if (string) {
									setValue(object, method, pkey, (String) properties.get(pkey), objects);
								} else {
									setValue(object, method, pkey, properties.get(pkey));
								}
							}
						}

					} else {

						Object value = properties.get(key);

						if (value == null) {
							throw new IllegalConfigurationException(key,
									"No value found for required property '" + key + "'");
						}

						if (string) {
							setValue(object, method, key, (String) value, objects);
						} else {
							setValue(object, method, key, value);
						}
					}

				} catch (MissingObjectRefException m) {
					if (futureObjects.contains(m.getObjectName()) && forceRequired) {
						throw m;
					} else if (annotation.Optional() || !forceRequired) {
						continue;
					} else {
						throw m;
					}
				} catch (IllegalConfigurationException e) {
					if (annotation.Optional() || !forceRequired) {
						continue;
					} else {
						throw e;
					}
				}
			}
		}
	}

	private static void setValue(RoboticsObject object, Method setter, String key, Object value)
			throws IllegalConfigurationException {
		try {
			if (isArray(setter)) {
				int index = getArrayIndex(key);
				setter.invoke(object, index, value);
			} else {
				setter.invoke(object, value);
			}
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace(); // TODO: remove
			throw new IllegalConfigurationException(key,
					"Could not set property " + key + " for object of type " + object.getClass().getSimpleName() + ".",
					e);
		} catch (Exception e) {
			throw new IllegalConfigurationException(key,
					"Could not set property " + key + " for object of type " + object.getClass().getSimpleName() + ".",
					e);
		}
	}

	private static int getArrayIndex(String key) throws IllegalConfigurationException {
		int indexPos = key.indexOf("[");
		if (indexPos == -1) {
			throw new IllegalConfigurationException(key, "Property is an array.");
		}
		String rest = key.substring(indexPos + 1);
		int endPos = rest.indexOf("]");
		if (endPos == -1) {
			throw new IllegalConfigurationException(key, "Property is an array.");
		}
		return Integer.parseInt(rest.substring(0, endPos));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setValue(RoboticsObject object, Method setter, String key, String value,
			List<RoboticsObject> objects) throws IllegalConfigurationException {
		try {
			Class<?> type = isArray(setter) ? setter.getParameterTypes()[1] : setter.getParameterTypes()[0];

			if (type == String.class) {
				setter.invoke(object, value);
			} else if (type == Integer.class || type == int.class) {
				try {
					setValue(object, setter, key, Integer.parseInt(value));
				} catch (NumberFormatException e) {
					throw new IllegalConfigurationException(key, "Could not set property '" + key
							+ " for object of type " + object.getClass().getSimpleName() + "'. Integer expected!");
				}
			} else if (type == Double.class || type == double.class) {
				try {
					setValue(object, setter, key, Double.parseDouble(value));
				} catch (NumberFormatException e) {
					throw new IllegalConfigurationException(key, "Could not set property '" + key
							+ " for object of type " + object.getClass().getSimpleName() + "'. Double expected!");
				}
			} else if (type == Boolean.class || type == boolean.class) {
				setValue(object, setter, key, Boolean.parseBoolean(value));
			} else if (type.isEnum()) {
				setValue(object, setter, key, Enum.valueOf((Class<? extends Enum>) type, value));
			} else if (RoboticsObject.class.isAssignableFrom(type)) {
				RoboticsObject ref = find(value, objects);

				if (ref == null) {
					throw new MissingObjectRefException(key, value);
				}
				setValue(object, setter, key, ref);
			} else {
				throw new IllegalConfigurationException(key, "Invalid parameter type: " + type.getCanonicalName());
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalConfigurationException(key,
					"Could not set property " + key + " for object of type " + object.getClass().getSimpleName() + ".",
					e);
		} catch (IllegalAccessException e) {
			throw new IllegalConfigurationException(key,
					"Could not set property " + key + " for object of type " + object.getClass().getSimpleName() + ".",
					e);
		} catch (InvocationTargetException e) {
			throw new IllegalConfigurationException(key,
					"Could not set property " + key + " for object of type " + object.getClass().getSimpleName() + ".",
					e);
		}
	}

	public static <T extends RoboticsObject> T find(String name, Class<T> type, List<RoboticsObject> objects) {
		if (objects == null) {
			return null;
		}

		for (RoboticsObject object : objects) {
			if (object.getName() == null) {
				continue;
			}
			if (!type.isInstance(object)) {
				continue;
			}
			if (object.getName().equals(name)) {
				return type.cast(object);
			}
		}
		return null;
	}

	public static RoboticsObject find(String name, List<RoboticsObject> objects) {
		if (objects == null) {
			return null;
		}

		for (RoboticsObject object : objects) {
			if (object.getName() == null) {
				continue;
			}
			if (object.getName().equals(name)) {
				return object;
			}
		}
		return null;
	}

	private static Method getGetter(Class<? extends RoboticsObject> type, String key) {
		Method[] methods = type.getMethods();
		Method bestGetter = null;
		for (Method method : methods) {
			if (isGetter(method, key)) {
				if (bestGetter == null || bestGetter.getReturnType().isAssignableFrom(method.getReturnType())) {
					bestGetter = method;
				}
			}
		}
		return bestGetter;
	}

	private static boolean isGetter(Method method, String key) {
		if (key.indexOf("[") != -1) {
			key = key.substring(0, key.indexOf("["));
			if (method.getParameterTypes().length != 1) {
				return false;
			}
		}
		return method.getName().toLowerCase().equals("get" + key.toLowerCase());
	}

	private static Method getSetter(Class<? extends RoboticsObject> type, String key) {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			if (isSetter(method, key)) {
				return method;
			}
		}
		return null;
	}

	private static boolean isSetter(Method method, boolean required) {
		ConfigurationProperty annotation = method.getAnnotation(ConfigurationProperty.class);

		return annotation != null && method.getName().toLowerCase().startsWith("set")
				&& (required ? !annotation.Optional() : true);
	}

	private static boolean isSetter(Method method, String key) {
		if (!method.isAnnotationPresent(ConfigurationProperty.class)) {
			return false;
		}
		if (key.indexOf("[") != -1) {
			key = key.substring(0, key.indexOf("["));
			if (method.getParameterTypes().length != 2) {
				return false;
			}
		}
		return method.getName().toLowerCase().equals("set" + key.toLowerCase());
	}

}
