/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.util;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.RoboticsObject;

public class Configuration {

	private final String name;
	private final String className;
	private final Map<String, String> properties;

	public Configuration(String name, Class<? extends RoboticsObject> type) {
		this(name, type.getCanonicalName());
	}

	public Configuration(String name, Class<? extends RoboticsObject> type, Map<String, String> properties) {
		this(name, type.getCanonicalName(), properties);
	}

	public Configuration(String name, String className) {
		this(name, className, new HashMap<String, String>());
	}

	public Configuration(String name, String className, Map<String, String> properties) {
		super();
		this.name = name;
		this.className = className;
		this.properties = properties;
	}

	/**
	 * Retrieves the name of the configured object from the underlying property map.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the class name of the configured object.
	 * 
	 * @return the class name
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Retrieves the properties of the configured object.
	 * 
	 * @return a <code>Map<String, String></code> with configuration properties
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	@Override
	public String toString() {
		return String.format("Configuration [name=%s, class=%s]", name, className);
	}

}
