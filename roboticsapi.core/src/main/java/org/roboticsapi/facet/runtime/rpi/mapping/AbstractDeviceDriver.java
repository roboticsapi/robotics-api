/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.core.DeviceInterfaceModifier;
import org.roboticsapi.core.DeviceInterfaceModifierCollector;

/**
 * Abstract implementation for a {@link DeviceDriver} targeting the RCC
 * reference implementation.
 */
public abstract class AbstractDeviceDriver<D extends Device> extends AbstractDriver
		implements DeviceDriver, NamedDeviceDriver<D> {

	private final Dependency<D> device;

	private DeviceInterfaceFactoryCollector collectionOfDeviceInterfaceFactoriesInProgress = null;
	private final Map<Class<? extends DeviceInterface>, DeviceInterfaceFactory<?>> providedDeviceInterfaceFactories = new HashMap<>();

	private DeviceInterfaceModifierCollector collectionOfDeviceInterfaceModifiersInProgress = null;
	private final Map<Class<? extends DeviceInterface>, DeviceInterfaceModifier<?>> providedDeviceInterfaceModifiers = new HashMap<>();

	/**
	 * Default constructor.
	 */
	protected AbstractDeviceDriver() {
		device = createDependency("device");
	}

	/**
	 * Convenience constructor.
	 *
	 * @param runtime    the driver's runtime.
	 * @param deviceName the driver's device name at the RCC.
	 */
	protected AbstractDeviceDriver(RpiRuntime runtime, String deviceName) {
		this();
		setRuntime(runtime);
		setRpiDeviceName(deviceName);
	}

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
		if (collectionOfDeviceInterfaceFactoriesInProgress != null)
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfacefactories does not end up correctly.");
		providedDeviceInterfaceFactories.forEach((di, factory) -> {
			addInterfaceFactory(getDevice(), di, factory);
		});

		// Create and add device interface modifiers
		collectionOfDeviceInterfaceModifiersInProgress = new DeviceInterfaceModifierCollector(
				providedDeviceInterfaceModifiers);
		collectDeviceInterfaceModifiers(collectionOfDeviceInterfaceModifiersInProgress);
		if (collectionOfDeviceInterfaceModifiersInProgress != null)
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfaceModifiers does not end up correctly.");
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
		if (collectionOfDeviceInterfaceFactoriesInProgress != collector)
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfacefactories does not end up correctly.");
		collectionOfDeviceInterfaceFactoriesInProgress = null;
	}

	protected void collectDeviceInterfaceModifiers(DeviceInterfaceModifierCollector collector) {
		if (collectionOfDeviceInterfaceModifiersInProgress != collector)
			throw new RuntimeException("Recursive call of " + getClass().getSimpleName()
					+ "::collectDeviceInterfaceModifiers does not end up correctly.");
		collectionOfDeviceInterfaceModifiersInProgress = null;
	}

}
