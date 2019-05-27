/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class AbstractRoboticsObject implements RoboticsObject {

	private String name;
	private boolean initialized = false;
	private RoboticsContext context;

	private final Map<String, RoboticsObject> automaticObjects = new LinkedHashMap<String, RoboticsObject>();
	private final List<InitializationListener> initListeners = new ArrayList<InitializationListener>();

	@Override
	public final boolean isInitialized() {
		return this.initialized;
	}

	protected final void immutableWhenInitialized() {
		if (isInitialized()) {
			throw new IllegalStateException("The configuration cannot be changed while the object is initialized.");
		}
	}

	/**
	 * Sets the name of this object.
	 *
	 * @param name the new name
	 */
	@Override
	@ConfigurationProperty
	public final void setName(String name) {
		immutableWhenInitialized();
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	// FIXME: MUST BE private!
	public final RoboticsContext getContext() {
		return context;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getName() + "'";
	}

	protected final void initialize(RoboticsContext context) throws InitializationException {
		if (context == null) {
			throw new IllegalArgumentException("Robotics context must not be null.");
		}

		if (isInitialized()) {
			throw new InitializationException("Robotics object is already initialized.");
		}

		try {
			// setup all automatic configuration properties
			doAutomaticConfiguration(context);
			// check if all configuration properties are okay
			validateConfigurationProperties();
		} catch (InitializationException e) {
			cleanupAutomaticConfiguration(context);
			throw e;
		}

		try {
			this.context = context;
			beforeInitialization();
			initialized = true;
			afterInitialization();

			notifyListenersOnInitialized();
		} catch (RoboticsException e) {
			this.context = null;
			this.initialized = false;

			throw new IllegalStateException("Unexpected error during initialization occurred.", e);
		}
	}

	private void doAutomaticConfiguration(RoboticsContext context) throws ConfigurationException {
		fillAutomaticConfigurationProperties(automaticObjects);

		for (Map.Entry<String, RoboticsObject> object : automaticObjects.entrySet()) {
			try {
				if (object.getValue() != null) {
					context.initialize(object.getValue());
				}
			} catch (InitializationException e) {
				throw new ConfigurationException(object.getKey(), "Could not fill automatic property", e);
			}
		}
	}

	/**
	 * Fills automatic configuration properties during initialization process. Empty
	 * implementation that can be overridden by subclasses.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #initialize(InitializationContext)
	 */
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		// empty implementation
	}

	private void cleanupAutomaticConfiguration(RoboticsContext context) {
		ArrayList<RoboticsObject> list = new ArrayList<RoboticsObject>(automaticObjects.values());
		Collections.reverse(list);

		for (RoboticsObject object : list) {
			try {
				if (object != null && object.isInitialized()) {
					context.uninitialize(object);
				}
			} catch (InitializationException e) {
			}
		}

		clearAutomaticConfigurationProperties(automaticObjects);
		automaticObjects.clear();
	}

	/**
	 * Clears automatic configuration properties during (un)initialization process.
	 * Empty implementation that can be overridden by subclasses.
	 *
	 * @see #initialize(InitializationContext)
	 * @see #uninitialize()
	 */
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		// empty implementation
	}

	protected void validateConfigurationProperties() throws ConfigurationException {
		// empty implementation
	}

	protected void beforeInitialization() throws RoboticsException {
		// empty default implementation
	}

	protected void afterInitialization() throws RoboticsException {
		// empty default implementation
	}

	protected final void uninitialize() throws InitializationException {
		if (!isInitialized()) {
			throw new InitializationException("Online object is already uninitialized.");
		}
		validateBeforeUninitialization();

		try {
			beforeUninitialization();
			initialized = false;
			afterUninitialization();

			cleanupAutomaticConfiguration(context);
		} catch (RoboticsException e) {
			throw new IllegalStateException("Unexpected error during uninitialization occurred.", e);
		} finally {
			initialized = false;
			context = null;
			notifyListenersOnUninitialized();
		}
	}

	protected void validateBeforeUninitialization() throws InitializationException {
		// empty implementation
	}

	protected void beforeUninitialization() throws RoboticsException {
		// empty implementation
	}

	protected void afterUninitialization() throws RoboticsException {
		// empty default implementation
	}

	private void notifyListenersOnInitialized() {
		for (InitializationListener l : new ArrayList<InitializationListener>(this.initListeners)) {
			l.onInitialized(this);
		}
	}

	private void notifyListenersOnUninitialized() {
		for (InitializationListener l : new ArrayList<InitializationListener>(this.initListeners)) {
			l.onUninitialized(this);
		}
	}

	@Override
	public final void addInitializationListener(InitializationListener l) {
		if (!initListeners.contains(l)) {
			initListeners.add(l);
		}
	}

	@Override
	public final void removeInitializationListener(InitializationListener l) {
		initListeners.remove(l);
	}

	protected static final void checkInOpenInterval(String key, int i, int min, int max) throws ConfigurationException {
		if (i <= min) {
			throw new ConfigurationException(key,
					"The value of property '" + key + "' is less than the minimal value of " + min);
		}

		if (i >= max) {
			throw new ConfigurationException(key,
					"The value of property '" + key + "' is greater than the maximum value of " + max);
		}
	}

	protected static final void checkInClosedInterval(String key, int i, int min, int max)
			throws ConfigurationException {
		if (i < min) {
			throw new ConfigurationException(key,
					"The value of property '" + key + "' is less than or equal the minimal value of " + min);
		}

		if (i > max) {
			throw new ConfigurationException(key,
					"The value of property '" + key + "' is greater than or equal the maximum value of " + max);
		}
	}

	protected static final void checkNotNullAndInitialized(String key, RoboticsObject o) throws ConfigurationException {
		checkNotNull(key, o);
		if (!o.isInitialized()) {
			throw new ConfigurationException(key, "Object '" + key + "' has to be initialized");
		}
	}

	protected static final void checkInitializedIfNotNull(String key, RoboticsObject o) throws ConfigurationException {
		if (o == null) {
			// TODO Replace return with throw new ...Exception(...)?
			return;
		}
		if (!o.isInitialized()) {
			throw new ConfigurationException(key, "Object '" + key + "' has to be initialized");
		}
	}

	protected static final void checkNotNull(String key, Object property) throws ConfigurationException {
		if (property == null) {
			throw new ConfigurationException(key, "Property '" + key + "' has to be set");
		}
	}

	protected static final void checkNotNull(String key, List<? extends RoboticsObject> objects)
			throws ConfigurationException {
		checkNotNullAndInitialized(key, objects, false);
	}

	protected static final void checkNotNullAndInitialized(String key, List<? extends RoboticsObject> objects)
			throws ConfigurationException {
		checkNotNullAndInitialized(key, objects, true);
	}

	private static final void checkNotNullAndInitialized(String key, List<? extends RoboticsObject> objects,
			boolean checkForInitialized) throws ConfigurationException {
		int i = 0;

		for (RoboticsObject o : objects) {
			if (checkForInitialized) {
				checkNotNullAndInitialized(toID(key, i), o);
			} else {
				checkNotNull(toID(key, i), o);
			}
			i++;
		}
	}

	protected static final void checkNoParent(String key, Entity e) throws ConfigurationException {
		if (e.getParent() != null) {
			throw new ConfigurationException(key, "Entity '" + key + "' must not have a parent.");
		}
	}

	protected final String name(String key) {
		return getName() + " " + key.trim();
	}

	protected final String name(String key, int index) {
		return name(key) + " " + index;
	}

	/**
	 * Static helper method for filling an automatic configuration property.
	 *
	 * @param name           the name of the property
	 * @param current        the current value of the property
	 * @param automatic      the automatically created value of the property
	 * @param createdObjects a map of automatically created objects
	 * @return the property
	 *
	 * @see #fillAutomaticConfigurationProperties(Map)
	 */
	protected final static <T extends RoboticsObject> T fill(String name, T current, T automatic,
			Map<String, RoboticsObject> createdObjects) {

		if (current == null || !current.isInitialized()) {
			createdObjects.put(name, automatic);
			return automatic;
		}
		return current;
	}

	protected final static <T extends RoboticsObject> void fill(String name, List<T> list, int index, T automatic,
			Map<String, RoboticsObject> createdObjects) {
		if (list.get(index) == null || !list.get(index).isInitialized()) {
			createdObjects.put(toID(name, index), automatic);
			list.set(index, automatic);
		}
	}

	private static String toID(String name, int index) {
		return name + "[" + index + "]";
	}

	protected final static <T extends RoboticsObject> T clear(String name, T current,
			Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey(name)) {
			return null;
		}
		return current;
	}

	protected final static void clear(String name, List<? extends RoboticsObject> list,
			Map<String, RoboticsObject> createdObjects) {
		for (int i = 0; i < list.size(); i++) {
			clear(name, list, i, createdObjects);
		}
	}

	protected final static void clear(String name, List<? extends RoboticsObject> list, int index,
			Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey(toID(name, index))) {
			list.set(index, null);
		}
	}

}