/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.extension.RoboticsObjectBuilder;

/**
 * Factory for {@link RoboticsRuntime}s and {@link Device}s.
 */
public final class RoboticsFactory {

	/**
	 * Builds a {@link RoboticsObject} from a given {@link Configuration}.
	 * <p>
	 * This includes:
	 * <ul>
	 * <li>finding an appropriate {@link RoboticsObjectBuilder} from the given list
	 * of {@link RoboticsObjectBuilder}s,
	 * <li>building the {@link RoboticsObject},
	 * <li>and setting its properties.
	 * </ul>
	 * <p>
	 *
	 * @param configuration the configuration
	 * @param objects       a list of already built {@link RoboticsObject}s to
	 *                      facilitate setting configuration properties which
	 *                      contain other {@link RoboticsObject}s.
	 * @param providers     a list of {@link RoboticsObjectBuilder}s
	 * @return the built robotics object
	 * @throws IllegalConfigurationException if a problem occurred during creation
	 *                                       of the robotics object
	 */
	public static RoboticsObject build(Configuration configuration, List<RoboticsObject> objects,
			Set<RoboticsObjectBuilder> builders, List<String> futureObjects) throws IllegalConfigurationException {
		RoboticsObjectBuilder builder = findBuilder(configuration, builders);
		return buildInternal(configuration, objects, builder, true, futureObjects);
	}

	/**
	 * Builds a {@link Device} from a given {@link Configuration}.
	 * <p>
	 * This includes:
	 * <ul>
	 * <li>building the {@link RoboticsObject},
	 * <li>and setting its properties,
	 * </ul>
	 * <p>
	 *
	 * @param configuration the configuration
	 * @param objects       a list of already built {@link RoboticsObject}s to
	 *                      facilitate setting configuration properties which
	 *                      contain other {@link RoboticsObject}s.
	 * @param providers     a {@link RoboticsObjectBuilder}
	 * @return the built robotics object
	 * @throws IllegalConfigurationException if a problem occurred during creation
	 *                                       of the robotics object
	 */
	public static RoboticsObject build(Configuration configuration, List<RoboticsObject> objects,
			RoboticsObjectBuilder builder, List<String> futureObjects) throws IllegalConfigurationException {
		return buildInternal(configuration, objects, builder, true, futureObjects);
	}

	/**
	 * Builds {@link RoboticsObject}s, i.e. {@link RoboticsRuntime}s and
	 * {@link Device}s, from a given list of {@link Configuration}s.
	 *
	 * @param configurations a list of configurations
	 * @param objects        a list of already built {@link RoboticsObject}s to
	 *                       facilitate setting configuration properties which
	 *                       contain other {@link RoboticsObject}s. Successfully
	 *                       built objects are automatically added to this list.
	 * @param builders       a list of {@link RoboticsObjectBuilder}s
	 * @param hook           a {@link RoboticsFactoryHook}
	 * @throws MultipleIllegalConfigurationsException if problems occurred during
	 *                                                creation of the robotics
	 *                                                objects
	 */
	public static void build(List<Configuration> configurations, List<RoboticsObject> objects,
			Set<RoboticsObjectBuilder> builders, RoboticsFactoryHook hook)
			throws MultipleIllegalConfigurationsException {
		// check if we have configurations to build...
		if (configurations == null || configurations.isEmpty()) {
			return;
		}
		// initialize list of objects if null
		if (objects == null) {
			objects = new ArrayList<RoboticsObject>();
		}

		List<String> futureObjects = new ArrayList<String>();
		for (Configuration configuration : configurations) {
			futureObjects.add(configuration.getName());
		}

		List<String> objectNames = new ArrayList<String>();
		for (RoboticsObject object : objects) {
			objectNames.add(object.getName());
		}

		List<IllegalConfigurationException> problems = new ArrayList<IllegalConfigurationException>();

		while (true) {
			boolean progress = false;
			List<Configuration> failedConfigurations = new ArrayList<Configuration>();
			problems.clear();

			for (Configuration configuration : configurations) {
				try {
					// try to build the device
					RoboticsObject o = build(configuration, objects, builders, futureObjects);
					// notify hook
					if (hook != null) {
						hook.onBuilt(o, configuration);
					}
					// add it to the list
					objects.add(o);
					// signal progress
					progress = true;

				} catch (IllegalConfigurationException e) {

					// special treatment for ChildObjectRequiredException
					// occuring in onBuilt (RoboticsObject#initialize)
					if (e.getCause() != null && e.getCause() instanceof ChildObjectsRequiredException) {

						// find the requested object
						ChildObjectsRequiredException requestedObjects = (ChildObjectsRequiredException) e.getCause();

						for (String childProperty : requestedObjects.getKeys()) {

							// if we haven't processed the requested key yet
							if (configuration.getProperties().get(childProperty) == null) {

								Configuration newConfig = requestedObjects.getConfiguration(childProperty);

								String name = newConfig.getName();
								while (futureObjects.contains(name) || objectNames.contains(name)) {
									name += "_";
								}
								if (!name.equals(newConfig.getName())) {
									newConfig = new Configuration(name, newConfig.getClassName(),
											newConfig.getProperties());
								}

								// add the requested configuration to the
								// to-build-list (and future objects) and have
								// it
								// assigned to the given key in the current
								// object
								configuration.getProperties().put(childProperty, newConfig.getName());

								futureObjects.add(newConfig.getName());
								failedConfigurations.add(newConfig);
								progress = true;
							}
						}
					}

					e.setConfiguration(configuration);
					problems.add(e);
					failedConfigurations.add(configuration);
				}
			}
			configurations = failedConfigurations;

			if (failedConfigurations.size() == 0) {
				return;
			}
			if (!progress) {
				break;
			}
		}
		throw new MultipleIllegalConfigurationsException(problems);
	}

	private static RoboticsObject buildInternal(Configuration configuration, List<RoboticsObject> objects,
			RoboticsObjectBuilder builder, boolean forceRequired, List<String> futureObjects)
			throws IllegalConfigurationException {
		String name = configuration.getName();
		String className = configuration.getClassName();

		// check the device builder
		if (builder == null) {
			IllegalConfigurationException exception = new IllegalConfigurationException("",
					"No valid builder for robotics object '" + name + "' of type '" + className + "' present.");
			exception.setConfiguration(configuration);
			throw exception;
		}
		// build & check the device
		RoboticsObject builtObject = builder.build(className);

		if (builtObject == null) {
			IllegalConfigurationException exception = new IllegalConfigurationException("",
					"Cannot build robotics object '" + name + "' by given builder: " + builder.getClass().getName());
			exception.setConfiguration(configuration);
			throw exception;
		}
		// set its properties
		try {
			Map<String, String> properties = configuration.getProperties();
			properties.put("name", name);
			ConfigurationUtils.setProperties(builtObject, properties, objects, forceRequired, futureObjects);
		} catch (IllegalConfigurationException exception) {
			exception.setConfiguration(configuration);
			throw exception;
		}
		return builtObject;
	}

	public static RoboticsObjectBuilder findBuilder(Configuration configuration, Set<RoboticsObjectBuilder> builders) {
		return findBuilder(configuration.getClassName(), builders);
	}

	private static RoboticsObjectBuilder findBuilder(String className, Set<RoboticsObjectBuilder> builders) {
		for (RoboticsObjectBuilder builder : builders) {
			if (builder.canBuild(className)) {
				return builder;
			}
		}
		return null;
	}

}
