/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;

public abstract class AbstractComposedDeviceDriver<D extends Device, R extends RoboticsRuntime>
		extends AbstractRoboticsObject implements ComposedDeviceDriver {

	private final Dependency<R> runtime;

	private final Map<OnlineObject, OperationState> childStates = new HashMap<OnlineObject, OnlineObject.OperationState>();
	private final OperationStateListener operationStateListener = new OperationStateListener() {
		@Override
		public void operationStateChanged(OnlineObject object, OperationState newState) {
			childStates.put(object, newState);
			notifyListeners();
		}
	};

	private final List<OperationStateListener> operationStateListeners = new ArrayList<OperationStateListener>();
	private OperationState lastListenerOperationState = getState();

	public AbstractComposedDeviceDriver() {
		runtime = createDependency("runtime");
	}

	private final void notifyListeners() {
		OperationState newState = getState();
		if (lastListenerOperationState == newState) {
			return;
		}
		lastListenerOperationState = newState;

		for (OperationStateListener l : operationStateListeners) {
			l.operationStateChanged(this, newState);
		}
	}

	protected final void addChildObject(OnlineObject child) {
		if (child != null) {
			childStates.put(child, child.getState());
			child.addOperationStateListener(operationStateListener);
		}
	}

	protected final void removeChildObject(OnlineObject child) {
		if (child != null) {
			child.removeOperationStateListener(operationStateListener);
			childStates.remove(child);
		}
	}

	@ConfigurationProperty(Optional = false)
	public void setRuntime(R runtime) {
		this.runtime.set(runtime);
	}

	@Override
	public R getRuntime() {
		return runtime.get();
	}

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
		operationStateListeners.add(listener);
		listener.operationStateChanged(this, lastListenerOperationState);
	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
		operationStateListeners.remove(listener);
	}

	@Override
	public OperationState getState() {
		if (!isInitialized()) {
			return OperationState.NEW;
		} else {
			return calculateState(childStates);
		}
	}

	protected OperationState calculateState(Map<OnlineObject, OperationState> childStates) {
		return getMinimumStateFrom(childStates.values().toArray(new OperationState[childStates.size()]));
	}

	protected static OperationState getAllEqualStateOrUnknownFrom(OperationState... operationStates) {
		OperationState allEqual = null;
		for (OperationState opState : operationStates) {
			if (allEqual == null) {
				allEqual = opState;
			}
			if (opState != allEqual) {
				return OperationState.UNKNOWN;
			}
		}
		return allEqual == null ? OperationState.OPERATIONAL : allEqual;
	}

	protected static OperationState getMinimumStateFrom(OperationState... operationStates) {
		OperationState minOpSstate = OperationState.OPERATIONAL;
		for (OperationState opState : operationStates) {
			if (getPriority(opState) < getPriority(minOpSstate)) {
				minOpSstate = opState;
			}
		}
		return minOpSstate;
	}

	private static int getPriority(OperationState operationState) {
		// switch operation can't handle null references (switch can only handle
		// integers)
		if (operationState == null) {
			throw new IllegalArgumentException("Argument operationState must not be null.");
		}

		switch (operationState) {
		case ABSENT:
			return 1;
		case NEW:
			return 0;
		case OFFLINE:
			return 3;
		case OPERATIONAL:
			return 5;
		case SAFEOPERATIONAL:
			return 4;
		case UNKNOWN:
			return 2;
		// TODO: Maybe throw new NotImplementedException instead of return
		// -1?
		default:
			return -1;
		}
	}

	@Override
	public boolean isPresent() {
		return getState().isPresent();
	}

	private final Dependency<D> device = createDependency("device");

	private DeviceInterfaceFactoryCollector collectionOfDeviceInterfaceFactoriesInProgress = null;
	private final Map<Class<? extends DeviceInterface>, DeviceInterfaceFactory<?>> providedDeviceInterfaceFactories = new HashMap<>();

	private DeviceInterfaceModifierCollector collectionOfDeviceInterfaceModifiersInProgress = null;
	private final Map<Class<? extends DeviceInterface>, DeviceInterfaceModifier<?>> providedDeviceInterfaceModifiers = new HashMap<>();

	@Override
	public final D getDevice() {
		return device.get();
	}

	@ConfigurationProperty(Optional = false)
	public final void setDevice(D device) {
		this.device.set(device);
	}

	@Override
	protected void afterInitialization() {
		super.afterInitialization();

		// Create and add device interface factories
		collectionOfDeviceInterfaceFactoriesInProgress = new DeviceInterfaceFactoryCollector(
				providedDeviceInterfaceFactories);
		collectDeviceInterfaceFactories(collectionOfDeviceInterfaceFactoriesInProgress);
		if (collectionOfDeviceInterfaceFactoriesInProgress != null) {
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfacefactories does not end up correctly.");
		}
		providedDeviceInterfaceFactories.forEach((di, factory) -> {
			addInterfaceFactory(getDevice(), di, factory);
		});

		// Create and add device interface modifiers
		collectionOfDeviceInterfaceModifiersInProgress = new DeviceInterfaceModifierCollector(
				providedDeviceInterfaceModifiers);
		collectDeviceInterfaceModifiers(collectionOfDeviceInterfaceModifiersInProgress);
		if (collectionOfDeviceInterfaceModifiersInProgress != null) {
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfaceModifiers does not end up correctly.");
		}
		providedDeviceInterfaceModifiers.forEach((di, factory) -> {
			addInterfaceModifier(getDevice(), di, factory);
		});
	}

	@Override
	protected void beforeUninitialization() {
		// Remove device interface modifiers
		providedDeviceInterfaceModifiers.forEach((di, factory) -> {
			removeInterfaceModifier(getDevice(), di, factory);
		});

		// Remove device interface factories
		providedDeviceInterfaceFactories.forEach((di, factory) -> {
			removeInterfaceFactory(getDevice(), di, factory);
		});

		super.beforeUninitialization();
	}

	@SuppressWarnings("unchecked")
	private static <T extends DeviceInterface> void addInterfaceFactory(Device device, Class<T> di,
			DeviceInterfaceFactory<?> factory) {
		device.addInterfaceFactory(di, (DeviceInterfaceFactory<T>) factory);
	}

	@SuppressWarnings("unchecked")
	private static <T extends DeviceInterface> void removeInterfaceFactory(Device device, Class<T> di,
			DeviceInterfaceFactory<?> factory) {
		device.removeInterfaceFactory(di, (DeviceInterfaceFactory<T>) factory);
	}

	@SuppressWarnings("unchecked")
	private static <T extends DeviceInterface> void addInterfaceModifier(Device device, Class<T> di,
			DeviceInterfaceModifier<?> modifier) {
		device.addInterfaceModifier(di, (DeviceInterfaceModifier<T>) modifier);
	}

	@SuppressWarnings("unchecked")
	private static <T extends DeviceInterface> void removeInterfaceModifier(Device device, Class<T> di,
			DeviceInterfaceModifier<?> modifier) {
		device.removeInterfaceModifier(di, (DeviceInterfaceModifier<T>) modifier);
	}

	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		if (collectionOfDeviceInterfaceFactoriesInProgress != collector) {
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfacefactories does not end up correctly.");
		}
		collectionOfDeviceInterfaceFactoriesInProgress = null;
	}

	protected void collectDeviceInterfaceModifiers(DeviceInterfaceModifierCollector collector) {
		if (collectionOfDeviceInterfaceModifiersInProgress != collector) {
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfaceModifiers does not end up correctly.");
		}
		collectionOfDeviceInterfaceModifiersInProgress = null;
	}

}
