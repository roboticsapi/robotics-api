/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.ConsistentTimeRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.DataAgeForTimeRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.DataAgeRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.util.HashCodeUtil;

public abstract class RealtimeValue<T> {
	protected static boolean CONSTANT_FOLDING = true;
	private RoboticsRuntime runtime;
	private final Command scope;

	private List<RealtimeValueListener<? super T>> listeners = new ArrayList<RealtimeValueListener<? super T>>();
	private final List<RealtimeValue<?>> innerSensors = new ArrayList<RealtimeValue<?>>();

	private T cheapValue = null;

	/**
	 * Returns {@code true} if this {@link RealtimeValue} is constant over time,
	 * {@code false} otherwise.
	 *
	 * If this method return <code>true</code>, then
	 * {@link RealtimeValue#getCheapValue()} must not return <code>null</code>.
	 *
	 * @return {@code true} if the value represented by this object is constant;
	 *         {@code false} otherwise.
	 */
	public boolean isConstant() {
		return false;
	}

	public Observer<?> createObserver(final RealtimeValueListener<T> listener, RealtimeBoolean condition,
			boolean async) {
		return new Observer<T>(this, listener, condition, async);
	}

	private final RealtimeValueListener<Object> innerListener = new InnerRealtimeValueListener(this);

	private static class InnerRealtimeValueListener implements RealtimeValueListener<Object> {
		WeakReference<RealtimeValue<?>> outer;

		public InnerRealtimeValueListener(RealtimeValue<?> value) {
			outer = new WeakReference<RealtimeValue<?>>(value);
		}

		@Override
		public void onValueChanged(Object newValue) {
			RealtimeValue<?> value = outer.get();
			if (value != null) {
				value.notifyCheapValueChanged();
			}
		}
	}

	public RealtimeValue(RealtimeValue<?>... values) {
		Command scope = null;
		RoboticsRuntime runtime = null;
		for (RealtimeValue<?> v : values) {
			if (v.getScope() != null) {
				if (scope != null && scope != v.getScope()) {
					throw new IllegalArgumentException("Scope of composed values must be equal");
				}
				scope = v.getScope();
			}
			if (v.getRuntime() != null) {
				if (runtime != null && v.getRuntime() != runtime) {
					throw new IllegalArgumentException("RoboticsRuntimes of composed values must be equal");
				}
				runtime = v.getRuntime();
			}
		}
		if (scope != null && runtime != null && scope.getRuntime() != runtime) {
			throw new IllegalArgumentException("Scope and RoboticsRuntime of composed sensors must be equal");
		}
		this.runtime = runtime;
		this.scope = scope;
		addInnerValues(values);
	}

	public RealtimeValue(Command scope, RoboticsRuntime runtime) {
		this.scope = scope;
		this.runtime = runtime;
	}

	public RealtimeValue(Command scope) {
		this.scope = scope;
		this.runtime = scope == null ? null : scope.getRuntime();
	}

	public RealtimeValue(RoboticsRuntime runtime) {
		this.runtime = runtime;
		this.scope = null;
	}

	public RealtimeValue() {
		this.runtime = null;
		this.scope = null;
	}

	protected void setRuntime(RoboticsRuntime runtime) {
		this.runtime = runtime;
	}

	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	public Command getScope() {
		return scope;
	}

	public RealtimeValue<T> getForScope(Command command) {
		return this;
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
	 * Retrieves the current value of the RealtimeValue.
	 *
	 * This operation is expensive - use addListener() for regular updates of
	 * current sensor values.
	 *
	 * @return The current value.
	 * @throws RoboticsException if an error occurs when communicating with the
	 *                           sensor.
	 */
	public T getCurrentValue() throws RealtimeValueReadException {
		if (getCheapValue() != null) {
			return getCheapValue();
		}
		if (scope != null) {
			throw new RealtimeValueReadException(
					"RealtimeValue in only valid in a command and cannot be read from outside");
		}
		if (runtime == null) {
			throw new RealtimeValueReadException(
					"RealtimeValue has neither cheap value nor runtime - check implementation");
		}
		return runtime.getRealtimeValue(this);
	}

	/**
	 * Adds a RealtimeValueListener to this Sensor that is notified regularly about
	 * the current Sensor data value.
	 *
	 * Note that this operation is expensive - to register multiple
	 * RealtimeValueListeners for different Sensors, it is recommended to use the
	 * static Method Sensor.addListeners().
	 *
	 * @param listener the RealtimeValueListener to add
	 * @throws RoboticsException thrown if an error occurs when registering the
	 *                           listener with the sensor
	 */
	public synchronized void addListener(RealtimeValueListener<? super T> listener) throws RoboticsException {

		if (getScope() != null) {
			throw new RoboticsException(
					"RealtimeValue is only valid in a certain command, please add the listener there.");
		}
		// call listeners of runtime-less sensors only once
		if (getRuntime() == null) {
			// if constant, we don't have to remember the listener
			if (!isConstant()) {
				List<RealtimeValueListener<? super T>> listeners = new ArrayList<RealtimeValueListener<? super T>>(
						this.listeners);
				listeners.add(listener);
				this.listeners = listeners;
			}

			listener.onValueChanged(getCurrentValue());
		} else {
			List<RealtimeValueListenerRegistration<?>> list = new ArrayList<RealtimeValueListenerRegistration<?>>();
			list.add(new RealtimeValueListenerRegistration<T>(this, listener));

			addListeners(list);
		}
	}

	protected void notifyCheapValueChanged() {
		cheapValue = null;
		for (RealtimeValueListener<? super T> listener : listeners) {
			listener.onValueChanged(getCheapValue());
		}
	}

	/**
	 * Removes a previously added RealtimeValueListener from this Sensor.
	 *
	 * Note that this operation is expensive - to remove multiple
	 * RealtimeValueListeners for different Sensors, it is recommended to use the
	 * static Method Sensor.removeListeners().
	 *
	 * @param listener the RealtimeValueListener to remove
	 * @throws RoboticsException thrown if an error occurs when remove the listener
	 *                           from the sensor
	 */
	public synchronized void removeListener(RealtimeValueListener<? super T> listener) throws RoboticsException {
		if (getRuntime() == null) {
			List<RealtimeValueListener<? super T>> listeners = new ArrayList<RealtimeValueListener<? super T>>(
					this.listeners);
			listeners.remove(listener);
			this.listeners = listeners;

		} else {
			List<RealtimeValueListenerRegistration<?>> list = new ArrayList<RealtimeValueListenerRegistration<?>>();
			list.add(new RealtimeValueListenerRegistration<T>(this, listener));

			removeListeners(list);
		}
	}

	/**
	 * Creates a RealtimeValueListenerRegistration of the given
	 * RealtimeValueListener for this Sensor, which can be used to register or
	 * remove multiple RealtimeValueListeners using addListeners() or
	 * removeListeners().
	 *
	 * @param listener the RealtimeValueListener
	 * @return the RealtimeValueListenerRegistration
	 */
	public final RealtimeValueListenerRegistration<T> createListenerRegistration(RealtimeValueListener<T> listener) {
		return new RealtimeValueListenerRegistration<T>(this, listener);
	}

	/**
	 * Registers a series of RealtimeValueListeners for the given Sensors.
	 *
	 * @param listeners list of RealtimeValueListenerRegistrations, each specifying
	 *                  a Sensor and a RealtimeValueListener to be added
	 * @throws RoboticsException thrown if an error occurs when registering a
	 *                           listener with a sensor
	 */
	public static synchronized void addListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException {

		List<RealtimeValueListenerRegistration<?>> regsWithoutRuntime = new ArrayList<RealtimeValueListenerRegistration<?>>();

		// splitting registrations by runtime
		Map<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>> runtimeMap = splitListenersByRuntime(listeners,
				regsWithoutRuntime);

		// register the listeners at each runtime separately
		for (Entry<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>> entry : runtimeMap.entrySet()) {
			entry.getKey().addRelatimeValueListeners(entry.getValue());
		}

		// for listeners without runtime once
		for (RealtimeValueListenerRegistration<?> reg : regsWithoutRuntime) {
			reg.registerListenerDirectly();
		}
	}

	/**
	 * Removes a list of (previously added) RealtimeValueListeners for Sensors.
	 *
	 * Note that only the Sensor and RealtimeValueListener objects need to be the
	 * same as added previously. The RealtimeValueListenerRegistration objects are
	 * only treated as containers, not respecting their identity.
	 *
	 * @param listeners list of RealtimeValueListenerRegistration, each specifying a
	 *                  Sensor and a RealtimeValueListener to be removed
	 * @throws RoboticsException thrown if RealtimeValueListener removal failed
	 */
	public static synchronized void removeListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException {

		// splitting registrations by runtime
		Map<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>> runtimeMap = splitListenersByRuntime(listeners,
				null);

		// remove the listeners from each runtime separately
		for (Entry<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>> entry : runtimeMap.entrySet()) {
			entry.getKey().removeRealtimeValueListeners(entry.getValue());
		}
	}

	private static Map<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>> splitListenersByRuntime(
			List<RealtimeValueListenerRegistration<?>> listeners,
			List<RealtimeValueListenerRegistration<?>> outNoRuntime) throws RealtimeValueReadException {
		Map<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>> runtimeMap = new HashMap<RoboticsRuntime, List<RealtimeValueListenerRegistration<?>>>();

		for (RealtimeValueListenerRegistration<?> reg : listeners) {
			RoboticsRuntime sensorRuntime = reg.getSensor().getRuntime();
			if (sensorRuntime != null) { // we store the registration for this
				// runtime
				if (runtimeMap.containsKey(sensorRuntime)) {
					runtimeMap.get(sensorRuntime).add(reg);
				} else {
					List<RealtimeValueListenerRegistration<?>> list = new ArrayList<RealtimeValueListenerRegistration<?>>();
					list.add(reg);
					runtimeMap.put(sensorRuntime, list);
				}
			} else if (outNoRuntime != null) {
				outNoRuntime.add(reg);
			}
		}
		return runtimeMap;
	}

	/**
	 * Retrieves the other {@link RealtimeValue}s that contribute to this
	 * {@link RealtimeValue}.
	 *
	 * @return list of {@link RealtimeValue}s this {@link RealtimeValue} depends on
	 */
	public List<RealtimeValue<?>> getDependencies() {
		return Collections.unmodifiableList(innerSensors);
	}

	/**
	 * Creates a new {@link RealtimeValue} for this {@link RealtimeValue}'s
	 * computation, such that in the dependencies each occurrence of
	 * {@link RealtimeValue}s present as keys in the given map by the
	 * {@link RealtimeValue}s in the respective values.
	 *
	 * @param substitutionMap Map of {@link RealtimeValue}s to substitute
	 * @return {@link RealtimeValue} with given dependencies substituted
	 */
	public abstract RealtimeValue<T> substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap);

	protected final synchronized void addInnerValues(RealtimeValue<?>... sensors) {
		for (RealtimeValue<?> sensor : sensors) {
			if (sensor == null) {
				throw new IllegalArgumentException("None of the inner values may be null.");
			}
			innerSensors.add(sensor);

			if (sensor.getRuntime() == null) {
				try {
					sensor.addListener(innerListener);
				} catch (RoboticsException e) {
				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		for (RealtimeValue<?> sensor : innerSensors) {
			if (sensor.getRuntime() == null) {
				try {
					sensor.removeListener(innerListener);
				} catch (RoboticsException e) {
				}
			}
		}
		super.finalize();
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
	 * Checks if the given objects are both null, or both equal
	 *
	 * @param mine  my object
	 * @param other other object
	 * @return true if both are null or both are equal
	 */
	protected final boolean nullOrEqual(Object mine, Object other) {
		return mine == null ? other == null : other != null && mine.equals(other);
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

	public abstract boolean isAvailable();

	protected static boolean areAvailable(RealtimeValue<?>... sensors) {
		for (int i = 0; i < sensors.length; i++) {
			if (!sensors[i].isAvailable()) {
				return false;
			}
		}
		return true;
	}

	protected static boolean areAvailable(List<? extends RealtimeValue<?>> sensors) {
		for (int i = 0; i < sensors.size(); i++) {
			if (!sensors.get(i).isAvailable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * Creates a {@link PersistContext} that continuously perfoms this
	 * {@link RealtimeValue}'s computation and makes its value available in the
	 * given {@link RoboticsRuntime} (even if this {@link RealtimeValue} was limited
	 * to a smaller scope).
	 *
	 * @param condition the value is computed and provided whenever this
	 *                  {@link RealtimeBoolean} evaluates to true
	 * @param runtime   {@link RoboticsRuntime} to persist this
	 *                  {@link RealtimeValue} in. If this {@link RealtimeValue} has
	 *                  no runtime (<code>null</code>), any {@link RoboticsRuntime}
	 *                  may be given, otherwise runtime has to match this
	 *                  {@link RealtimeValue}'s runtime.
	 * @return {@link PersistContext} providing access to the persisted value
	 * @throws RoboticsException if an error occurs while establishing the
	 *                           calculation
	 */
	public PersistContext<T> persist(RealtimeBoolean condition, RoboticsRuntime runtime) throws RoboticsException {
		if (getRuntime() != null && runtime != getRuntime()) {
			throw new RoboticsException("The sensor is not valid for the given runtime.");
		}
		Command persistCmd = runtime
				.createWaitCommand("Persist " + this.toString().substring(0, Math.min(this.toString().length(), 50)));
		PersistContext<T> ret = new PersistContext<T>(persistCmd);
		persistCmd.assign(this, ret, condition);
		persistCmd.start();
		return ret;
	}

	/**
	 * Creates a {@link PersistContext} that continuously perfoms this
	 * {@link RealtimeValue}'s computation and makes its value available in the
	 * given {@link RoboticsRuntime} (even if this {@link RealtimeValue} was limited
	 * to a smaller scope).
	 *
	 * @param runtime {@link RoboticsRuntime} to persist this {@link RealtimeValue}
	 *                in. If this {@link RealtimeValue} has no runtime
	 *                (<code>null</code>), any {@link RoboticsRuntime} may be given,
	 *                otherwise runtime has to match this {@link RealtimeValue}'s
	 *                runtime.
	 * @return {@link PersistContext} providing access to the persisted value
	 * @throws RoboticsException if an error occurs while establishing the
	 *                           calculation
	 */
	public PersistContext<T> persist(RoboticsRuntime runtime) throws RoboticsException {
		return persist(RealtimeBoolean.TRUE, runtime);
	}

	/**
	 * Returns the age of the value. The data age represents the time when the value
	 * has been valid (which may be earlier than the time when the value has been
	 * available)
	 *
	 * @return age in seconds
	 */
	public RealtimeDouble getDataAge() {
		return new DataAgeRealtimeDouble(this);
	}

	/**
	 * Returns a time for which all given RealtimeValues had valid data (within an
	 * epsilon neighborhood, restricted to data younger than maxAge)
	 *
	 * @param maxAge  maximum age that has to be considered
	 * @param epsilon allowed difference between the times
	 * @param values  RealtimeValues to find a common time for
	 * @return a time for which all values had data (for use with forTime)
	 */
	public static RealtimeDouble getConsistentTime(double maxAge, double epsilon, Collection<RealtimeValue<?>> values) {
		return new ConsistentTimeRealtimeDouble(maxAge, epsilon, values);
	}

	/**
	 * Returns a time for which all given RealtimeValues had valid data (within an
	 * epsilon neighborhood, restricted to data younger than maxAge)
	 *
	 * @param maxAge  maximum age that has to be considered
	 * @param epsilon allowed difference between the times
	 * @param values  RealtimeValues to find a common time for
	 * @return a time for which all values had data (for use with forTime)
	 */
	public static RealtimeDouble getConsistentTime(double maxAge, double epsilon, RealtimeValue<?>... values) {
		return new ConsistentTimeRealtimeDouble(maxAge, epsilon, values);
	}

	/**
	 * Returns the data age (time in seconds) when a value for the given time has
	 * been available.
	 *
	 * @param time   time to get the value for
	 * @param maxAge maximum age that has to be considered
	 * @return age (time in seconds) when the value has been available (for use with
	 *         fromHistory)
	 */
	public RealtimeDouble getTimeForAge(RealtimeDouble time, double maxAge) {
		return new DataAgeForTimeRealtimeDouble(this, time, maxAge);
	}

	/**
	 * Returns the value that has been valid at the given time.
	 *
	 * @param time   time to get the value for (age in seconds)
	 * @param maxAge maximum age that has to be considered
	 * @return value that was valid at the given time
	 */
	public RealtimeValue<T> forAge(RealtimeDouble time, double maxAge) {
		return fromHistory(getTimeForAge(time, maxAge), maxAge);
	}

	/**
	 * Returns the value that has been available at the given time.
	 *
	 * @param age    time to get the value for (age in seconds)
	 * @param maxAge maximum age that has to be considered
	 * @return value that was known at the given time
	 */
	public abstract RealtimeValue<T> fromHistory(RealtimeDouble age, double maxAge);

	/**
	 * Returns the value that has been available a fixed amount of time ago.
	 *
	 * @param age time to get the value for (age in seconds)
	 * @return value that was known at the given time
	 */
	public RealtimeValue<T> fromHistory(double age) {
		return fromHistory(RealtimeDouble.createFromConstant(age), age);
	}

	/**
	 * Returns a {@link RealtimeBoolean} which indicated whether this
	 * {@link RealtimeValue} is <code>null</code>, i.e., it is not providing a valid
	 * value.
	 *
	 * @return a {@link RealtimeBoolean} which indicated whether this
	 *         {@link RealtimeValue} is <code>null</code>
	 */
	public abstract RealtimeBoolean isNull();

	/**
	 * Checks if a certain {@link RealtimeValue} occurs in the calculation for this
	 * {@link RealtimeValue}.
	 *
	 * @param other {@link RealtimeValue} to check for
	 * @return true if the given {@link RealtimeValue} is required to compute this
	 *         {@link RealtimeValue}
	 */
	public boolean contains(RealtimeValue<?> other) {
		if (innerSensors.contains(other)) {
			return true;
		}
		for (RealtimeValue<?> inner : innerSensors) {
			if (inner.contains(other)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		RealtimeValue<?> val = (RealtimeValue<?>) obj;
		return val.scope == scope && val.runtime == runtime && val.innerSensors.equals(innerSensors);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, scope), runtime), innerSensors);
	}

}
