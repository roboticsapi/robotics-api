/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorDataAgeSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.core.sensor.SensorRealtimeException;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * Sensors deliver data that can be retrieved in the Java workflow or in a
 * real-time context during Command execution.
 *
 * @param <T> the generic type of data delivered by this Sensor.
 */
public abstract class Sensor<T> {

	private T cheapValue = null;

	protected RoboticsRuntime runtime;

	private List<SensorListener<T>> listeners = new ArrayList<SensorListener<T>>();
	private final List<Sensor<?>> innerSensors = new ArrayList<Sensor<?>>();

	@SuppressWarnings("rawtypes")
	private final SensorListener innerSensorListener = new SensorListener() {
		@Override
		public void onValueChanged(Object newValue) {
			notifyCheapValueChanged();
		}
	};

	/**
	 * Instantiates a new sensor with a given RoboticsRuntime.
	 *
	 * @param runtime the RoboticRuntime of this Sensor
	 */
	public Sensor(RoboticsRuntime runtime) {
		this.runtime = runtime;
	}

	/**
	 * Gets the RoboticsRuntime of this Sensor.
	 *
	 * @return the RoboticsRuntime
	 */
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	public abstract boolean isAvailable();

	protected static boolean areAvailable(Sensor<?>... sensors) {
		for (int i = 0; i < sensors.length; i++) {
			if (!sensors[i].isAvailable()) {
				return false;
			}
		}
		return true;
	}

	protected static boolean areAvailable(List<? extends Sensor<?>> sensors) {
		for (int i = 0; i < sensors.size(); i++) {
			if (!sensors.get(i).isAvailable()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieves the current value directly (if this is computationally cheap)
	 *
	 * @return the current value, or null if it cannot be computed cheaply
	 */
	public final T getCheapValue() {
		if (cheapValue != null) {
			return cheapValue;
		}
		return cheapValue = calculateCheapValue();
	}

	protected T calculateCheapValue() {
		return null;
	}

	/**
	 * Retrieves the current value of the sensor.
	 *
	 * This operation is expensive - use addListener() for regular updates of
	 * current sensor values.
	 *
	 * @return The current sensor value.
	 * @throws RoboticsException if an error occurs when communicating with the
	 *                           sensor.
	 */
	public final T getCurrentValue() throws SensorReadException {
		T cheapValue = getCheapValue();
		if (cheapValue != null) {
			return cheapValue;
		}

		if (getRuntime() == null) {
			throw new SensorReadException("Sensor has neither cheapValue nor runtime - check implementation.");
		}

		try {
			return getRuntime().getSensorValue(this);
		} catch (RoboticsException e) {
			throw new SensorReadException(e);
		}
	}

	/**
	 * Gets the default data value this Sensor.
	 *
	 * @return the default value
	 */
	protected abstract T getDefaultValue();

	/**
	 * Adds a SensorListener to this Sensor that is notified regularly about the
	 * current Sensor data value.
	 *
	 * Note that this operation is expensive - to register multiple SensorListeners
	 * for different Sensors, it is recommended to use the static Method
	 * Sensor.addListeners().
	 *
	 * @param listener the SensorListener to add
	 * @throws RoboticsException thrown if an error occurs when registering the
	 *                           listener with the sensor
	 */
	public synchronized void addListener(SensorListener<T> listener) throws RoboticsException {
		// call listeners of runtime-less sensors only once
		if (getRuntime() == null) {
			List<SensorListener<T>> listeners = new ArrayList<SensorListener<T>>(this.listeners);
			listeners.add(listener);
			this.listeners = listeners;
			listener.onValueChanged(getCurrentValue());
		} else {
			List<SensorListenerRegistration<?>> list = new ArrayList<SensorListenerRegistration<?>>();
			list.add(new SensorListenerRegistration<T>(this, listener));

			addListeners(list);
		}
	}

	protected void notifyCheapValueChanged() {
		cheapValue = null;
		for (SensorListener<T> listener : listeners) {
			listener.onValueChanged(getCheapValue());
		}
	}

	/**
	 * Removes a previously added SensorListener from this Sensor.
	 *
	 * Note that this operation is expensive - to remove multiple SensorListeners
	 * for different Sensors, it is recommended to use the static Method
	 * Sensor.removeListeners().
	 *
	 * @param listener the SensorListener to remove
	 * @throws RoboticsException thrown if an error occurs when remove the listener
	 *                           from the sensor
	 */
	public synchronized void removeListener(SensorListener<T> listener) throws RoboticsException {
		if (getRuntime() == null) {
			List<SensorListener<T>> listeners = new ArrayList<SensorListener<T>>(this.listeners);
			listeners.remove(listener);
			this.listeners = listeners;
		} else {
			List<SensorListenerRegistration<?>> list = new ArrayList<SensorListenerRegistration<?>>();
			list.add(new SensorListenerRegistration<T>(this, listener));

			removeListeners(list);
		}
	}

	/**
	 * Creates a SensorListenerRegistration of the given SensorListener for this
	 * Sensor, which can be used to register or remove multiple SensorListeners
	 * using addListeners() or removeListeners().
	 *
	 * @param listener the SensorListener
	 * @return the SensorListenerRegistration
	 */
	public SensorListenerRegistration<T> createListenerRegistration(SensorListener<T> listener) {
		return new SensorListenerRegistration<T>(this, listener);
	}

	/**
	 * Registers a series of SensorListeners for the given Sensors.
	 *
	 * @param listeners list of SensorListenerRegistrations, each specifying a
	 *                  Sensor and a SensorListener to be added
	 * @throws RoboticsException thrown if an error occurs when registering a
	 *                           listener with a sensor
	 */
	public static synchronized void addListeners(List<SensorListenerRegistration<?>> listeners)
			throws RoboticsException {

		List<SensorListenerRegistration<?>> regsWithoutRuntime = new ArrayList<SensorListenerRegistration<?>>();

		// splitting registrations by runtime
		Map<RoboticsRuntime, List<SensorListenerRegistration<?>>> runtimeMap = splitListenersByRuntime(listeners,
				regsWithoutRuntime);

		// register the listeners at each runtime separately
		for (Entry<RoboticsRuntime, List<SensorListenerRegistration<?>>> entry : runtimeMap.entrySet()) {
			entry.getKey().addSensorListeners(entry.getValue());
		}

		// for listeners without runtime once
		for (SensorListenerRegistration<?> reg : regsWithoutRuntime) {
			reg.registerListenerDirectly();
		}
	}

	/**
	 * Removes a list of (previously added) SensorListeners for Sensors.
	 *
	 * Note that only the Sensor and SensorListener objects need to be the same as
	 * added previously. The SensorListenerRegistration objects are only treated as
	 * containers, not respecting their identity.
	 *
	 * @param listeners list of SensorListenerRegistration, each specifying a Sensor
	 *                  and a SensorListener to be removed
	 * @throws RoboticsException thrown if SensorListener removal failed
	 */
	public static synchronized void removeListeners(List<SensorListenerRegistration<?>> listeners)
			throws RoboticsException {

		// splitting registrations by runtime
		Map<RoboticsRuntime, List<SensorListenerRegistration<?>>> runtimeMap = splitListenersByRuntime(listeners, null);

		// remove the listeners from each runtime separately
		for (Entry<RoboticsRuntime, List<SensorListenerRegistration<?>>> entry : runtimeMap.entrySet()) {
			entry.getKey().removeSensorListeners(entry.getValue());
		}
	}

	private static Map<RoboticsRuntime, List<SensorListenerRegistration<?>>> splitListenersByRuntime(
			List<SensorListenerRegistration<?>> listeners, List<SensorListenerRegistration<?>> outNoRuntime)
			throws SensorReadException {
		Map<RoboticsRuntime, List<SensorListenerRegistration<?>>> runtimeMap = new HashMap<RoboticsRuntime, List<SensorListenerRegistration<?>>>();

		for (SensorListenerRegistration<?> reg : listeners) {
			RoboticsRuntime sensorRuntime = reg.getSensor().getRuntime();
			if (sensorRuntime != null) { // we store the registration for this
				// runtime
				if (runtimeMap.containsKey(sensorRuntime)) {
					runtimeMap.get(sensorRuntime).add(reg);
				} else {
					List<SensorListenerRegistration<?>> list = new ArrayList<SensorListenerRegistration<?>>();
					list.add(reg);
					runtimeMap.put(sensorRuntime, list);
				}
			} else if (outNoRuntime != null) {
				outNoRuntime.add(reg);
			}
		}
		return runtimeMap;
	}

	@SuppressWarnings({ "unchecked" })
	protected final synchronized void addInnerSensors(Sensor<?>... sensors) {
		for (Sensor<?> sensor : sensors) {
			if (sensor == null) {
				throw new IllegalArgumentException("None of the sensors may be null.");
			}

			innerSensors.add(sensor);

			// if we have listeners, add a listener to all relevant inner
			// sensors
			if (sensor.getRuntime() == null) {
				try {
					sensor.addListener(innerSensorListener);
				} catch (RoboticsException e) {
				}
			}
		}
	}

	protected static RoboticsRuntime selectRuntime(Sensor<?>... sensors) {
		RoboticsRuntime ret = null;
		for (Sensor<?> sensor : sensors) {
			if (sensor == null) {
				throw new IllegalArgumentException("None of the sensors may be null.");
			}

			RoboticsRuntime runtime = sensor.getRuntime();

			if (ret == null) {
				ret = runtime;
			} else if (runtime != null && runtime != ret) {
				throw new IllegalArgumentException("RoboticsRuntimes of composed sensors must be equal");
			}

		}
		return ret;
	}

	public DoubleSensor getSensorDataAgeSensor() {
		return new SensorDataAgeSensor(this);
	}

	public List<SensorRealtimeException> defineSensorExceptions() {
		return new ArrayList<SensorRealtimeException>();
	}

	public <ET extends SensorRealtimeException> ET defineSensorException(Class<ET> type) throws CommandException {
		for (SensorRealtimeException ex : defineSensorExceptions()) {
			if (type.isAssignableFrom(ex.getClass())) {
				return type.cast(ex);
			}
		}
		throw new CommandException("Found no sensor exception of the given type: " + type.getName());
	}

	public List<Class<? extends SensorRealtimeException>> getExceptionTypes() {
		List<Class<? extends SensorRealtimeException>> types = new ArrayList<Class<? extends SensorRealtimeException>>();

		for (SensorRealtimeException e : defineSensorExceptions()) {
			types.add(e.getClass());
		}
		return types;
	}

	/**
	 * Checks of the given object is of the same class as this object
	 *
	 * @param other other object
	 * @return true if other is of the same class as this
	 */
	protected final boolean classEqual(Object other) {
		return other != null && other.getClass() == getClass();
	}

	/**
	 * Calculates a hash code from this.getClass and the given further objects
	 *
	 * @param children further objects to include
	 * @return hash code
	 */
	protected final int classHash(Object... children) {
		int hashCode = HashCodeUtil.hash(HashCodeUtil.SEED, getClass());
		return hash(hashCode, children);
	}

	/**
	 * Calculates a hash code from the given hash code and the given further
	 * objects.
	 *
	 * @param haseCode starting hash code
	 * @param children further objects to include
	 * @return hash code
	 */
	protected final int hash(int hashCode, Object... children) {
		for (Object child : children) {
			hashCode = HashCodeUtil.hash(hashCode, child);
		}
		return hashCode;
	}

	public PersistContext<T> persist(RoboticsRuntime runtime) throws RoboticsException {
		return persist("Persisted Sensor", runtime);
	}

	public PersistContext<T> persist(String name, RoboticsRuntime runtime) throws RoboticsException {
		if (getRuntime() != null && runtime != getRuntime()) {
			throw new RoboticsException("The sensor is not valid for the given runtime.");
		}
		Command persistCmd = runtime.createWaitCommand(name);
		PersistContext<T> ret = new PersistContext<T>(persistCmd);
		persistCmd.assign(this, ret);
		persistCmd.start();
		return ret;
	}
}
