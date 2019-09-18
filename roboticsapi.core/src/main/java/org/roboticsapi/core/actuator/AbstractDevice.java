/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.AbstractRoboticsEntity;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.DeviceInterfaceModifier;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.Predicate;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsRuntime;

/**
 * Abstract implementation for a {@link Device}.
 *
 * @param <DD> the generic driver type
 */
public abstract class AbstractDevice<DD extends DeviceDriver> extends AbstractRoboticsEntity implements Device {

	private final Map<RoboticsRuntime, DD> drivers = new HashMap<RoboticsRuntime, DD>();
	private final Map<Class<? extends DeviceInterface>, List<DeviceInterfaceFactory<?>>> interfaceFactories = new HashMap<Class<? extends DeviceInterface>, List<DeviceInterfaceFactory<?>>>();
	private final Map<Class<? extends DeviceInterface>, List<DeviceInterfaceModifier<?>>> interfaceModifiers = new HashMap<Class<? extends DeviceInterface>, List<DeviceInterfaceModifier<?>>>();
	private final List<OperationStateListener> operationStateListeners = new ArrayList<OperationStateListener>();
	private final List<InterfaceListener> interfaceListeners = new ArrayList<Device.InterfaceListener>();
	private final OperationStateListener operationStateListener = new OperationStateListener() {
		@Override
		public void operationStateChanged(OnlineObject object, OperationState newState) {
			for (OperationStateListener listener : new ArrayList<OperationStateListener>(operationStateListeners)) {
				listener.operationStateChanged(AbstractDevice.this, getState());
			}
		}
	};
	private final ReferenceListener referenceListener = new ReferenceListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void referenceRemoved(RoboticsObject referencingObject, RoboticsObject referencedObject) {
			if (referencingObject instanceof DeviceDriver) {
				removeDriver((DD) referencingObject);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void referenceAdded(RoboticsObject referencingObject, RoboticsObject referencedObject) {
			if (referencingObject instanceof DeviceDriver) {
				addDriver((DD) referencingObject);
			}
		}
	};

	protected final void addDriver(DD driver) {
		drivers.put(driver.getRuntime(), driver);
		driver.addOperationStateListener(operationStateListener);
		onDriverAdded(driver);
	}

	protected void onDriverAdded(DD driver) {
	}

	protected final void removeDriver(DD driver) {
		try {
			onDriverRemoved(driver);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		driver.removeOperationStateListener(operationStateListener);
		drivers.remove(driver.getRuntime());
	}

	protected void onDriverRemoved(DD driver) {
	}

	@Override
	protected void afterInitialization() {
		super.afterInitialization();
		addReferenceListener(referenceListener);
	}

	@Override
	protected void beforeUninitialization() {
		removeReferenceListener(referenceListener);
		super.beforeUninitialization();
	}

	@Override
	public final List<Class<? extends DeviceInterface>> getInterfaces() {
		return new ArrayList<Class<? extends DeviceInterface>>(interfaceFactories.keySet());
	}

	public final <T extends DeviceInterface> T use(Class<T> type, Predicate<? super T> filter) {
		return use(type, filter, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return 0;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public final <T extends DeviceInterface> T use(Class<T> type, Predicate<? super T> filter,
			Comparator<? super T> comparator) {
		List<T> ret = new ArrayList<T>();
		if (interfaceFactories.containsKey(type)) {
			for (DeviceInterfaceFactory<?> factory : interfaceFactories.get(type)) {
				T di = (T) factory.build();
				if (interfaceModifiers.containsKey(type)) {
					for (DeviceInterfaceModifier<?> modifier : interfaceModifiers.get(type)) {
						DeviceInterfaceModifier<T> typedMod = (DeviceInterfaceModifier<T>) modifier;
						di = typedMod.modify(di);
					}
				}
				ret.add(di);
			}
		}
		if (ret.isEmpty()) {
			Class<? extends DeviceInterface> bestType = null;
			for (Class<? extends DeviceInterface> factoryType : interfaceFactories.keySet()) {
				if (type.isAssignableFrom(factoryType)) {
					if (bestType == null || factoryType.isAssignableFrom(bestType)) {
						bestType = factoryType;
					}
				}
			}
			if (bestType != null) {
				for (DeviceInterfaceFactory<?> factory : interfaceFactories.get(bestType)) {
					T di = (T) factory.build();
					if (interfaceModifiers.containsKey(bestType)) {
						for (DeviceInterfaceModifier<?> modifier : interfaceModifiers.get(bestType)) {
							DeviceInterfaceModifier<T> typedMod = (DeviceInterfaceModifier<T>) modifier;
							di = typedMod.modify(di);
						}
					}
					if (interfaceModifiers.containsKey(type)) {
						for (DeviceInterfaceModifier<?> modifier : interfaceModifiers.get(type)) {
							DeviceInterfaceModifier<T> typedMod = (DeviceInterfaceModifier<T>) modifier;
							di = typedMod.modify(di);
						}
					}
					ret.add(di);
				}
			}
		}

		Collections.sort(ret, comparator);
		for (T value : ret) {
			if (filter.appliesTo(value)) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final <T extends DeviceInterface> T use(Class<T> type) {
		return use(type, new Predicate<T>() {
			@Override
			public boolean appliesTo(T d) {
				return true;
			}
		});
	}

	@Override
	public <T extends DeviceInterface> T use(Class<T> type, final DeviceInterface compatibleWith) {
		return use(type, compatibleWith.getRuntime());
	}

	@Override
	public <T extends DeviceInterface> T use(Class<T> type, final RoboticsRuntime compatibleWith) {
		return use(type, new Predicate<T>() {
			@Override
			public boolean appliesTo(T d) {
				return compatibleWith == null || d.getRuntime() == null || d.getRuntime() == compatibleWith;
			}
		});
	}

	@Override
	public final <T extends DeviceInterface> void addInterfaceFactory(Class<T> type,
			DeviceInterfaceFactory<T> factory) {
		if (!interfaceFactories.containsKey(type)) {
			interfaceFactories.put(type, new ArrayList<DeviceInterfaceFactory<?>>());
		}
		interfaceFactories.get(type).add(factory);

		for (InterfaceListener l : new ArrayList<InterfaceListener>(interfaceListeners)) {
			l.interfaceAdded(this, type);
		}
	}

	@Override
	public <T extends DeviceInterface> void removeInterfaceFactory(Class<T> type, DeviceInterfaceFactory<T> factory) {
		if (!interfaceFactories.containsKey(type)) {
			return;
		}

		List<DeviceInterfaceFactory<?>> list = interfaceFactories.get(type);
		list.remove(factory);
		if (list.isEmpty()) {
			interfaceFactories.remove(type);
		}

		for (InterfaceListener l : new ArrayList<InterfaceListener>(interfaceListeners)) {
			l.interfaceRemoved(this, type);
		}
	}

	@Override
	public final <T extends DeviceInterface> void addInterfaceModifier(Class<T> type,
			DeviceInterfaceModifier<T> modifier) {
		if (interfaceModifiers.get(type) == null) {
			interfaceModifiers.put(type, new ArrayList<DeviceInterfaceModifier<?>>());
		}
		interfaceModifiers.get(type).add(modifier);
	}

	@Override
	public final <T extends DeviceInterface> void removeInterfaceModifier(Class<T> type,
			DeviceInterfaceModifier<T> modifier) {
		if (!interfaceModifiers.containsKey(type)) {
			return;
		}
		interfaceModifiers.get(type).remove(modifier);
		if (interfaceModifiers.get(type).isEmpty()) {
			interfaceModifiers.remove(type);
		}
	}

	@Override
	public void addInterfaceListener(InterfaceListener listener) {
		interfaceListeners.add(listener);

		for (Class<? extends DeviceInterface> type : interfaceFactories.keySet()) {
			listener.interfaceAdded(this, type);
		}
	}

	@Override
	public void removeInterfaceListener(InterfaceListener listener) {
		interfaceListeners.remove(listener);
	}

	public final List<DD> getDrivers() {
		return new ArrayList<DD>(drivers.values());
	}

	public final DD getDriver() {
		if (drivers.isEmpty()) {
			return null;
		} else {
			return Collections.max(drivers.values(), new Comparator<DD>() {
				@Override
				public int compare(DD o1, DD o2) {
					return o1.getState().compareTo(o2.getState());
				}
			});
		}
	}

	@Override
	public final DD getDriver(RoboticsRuntime runtime) {
		return drivers.get(runtime);
	}

	@Override
	public final void addOperationStateListener(final OperationStateListener listener) {
		operationStateListeners.add(listener);
		listener.operationStateChanged(this, getState());
	}

	@Override
	public final void removeOperationStateListener(OperationStateListener listener) {
		operationStateListeners.remove(listener);
	}

	@Override
	public final OperationState getState() {
		return isInitialized() ? getOperationState() : OperationState.NEW;
	}

	protected OperationState getOperationState() {
		OperationState ret = OperationState.NEW;
		for (DD driver : drivers.values()) {
			OperationState driverState = driver.getState();
			if (driverState.compareTo(ret) > 0) {
				ret = driverState;
			}
		}
		return ret;
	}

	@Override
	public final boolean isPresent() {
		return getState().isPresent();
	}

}
