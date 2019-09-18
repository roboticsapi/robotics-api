/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.core.realtimevalue.realtimeboolean.DriverBasedRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractDeviceDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.framework.io.activity.DigitalInputSensorInterface;
import org.roboticsapi.framework.io.digital.DigitalInput;
import org.roboticsapi.framework.io.digital.DigitalInputDriver;
import org.roboticsapi.framework.io.runtime.rpi.activity.DigitalInputSensorInterfaceImpl;

/**
 * {@link DigitalInputDriver} implementation for the SoftRobot RCC.
 */
public final class DigitalInputGenericDriver extends AbstractDeviceDriver<DigitalInput> implements DigitalInputDriver {

	private final Dependency<Integer> number;

	/**
	 * Constructor.
	 */
	public DigitalInputGenericDriver() {
		number = createDependency("number");
	}

	/**
	 * Constructor.
	 *
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected DigitalInputGenericDriver(int number, RpiRuntime runtime, String deviceName, DigitalInput device) {
		this();
		setNumber(number);
		setRuntime(runtime);
		setDevice(device);
		setRpiDeviceName(deviceName);
	}

	@ConfigurationProperty(Optional = false)
	public void setNumber(int number) {
		this.number.set(number);
	}

	@Override
	public int getNumber() {
		return number.get();
	}

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		if (!interfaces.containsKey("io")) {
			return false;
		}
		RPIint count = interfaces.get("io").get(RPIint.class, "digin");
		return count == null || count.get() >= getNumber();
	}

	@Override
	public RealtimeBoolean getSensor() {
		return new DigitalInputRealtimeBoolean(this);
	}

	/**
	 * {@link RealtimeBoolean} implementation for {@link DigitalInputGenericDriver}
	 */
	public static final class DigitalInputRealtimeBoolean
			extends DriverBasedRealtimeBoolean<DigitalInputGenericDriver> {

		public DigitalInputRealtimeBoolean(final DigitalInputGenericDriver parent) {
			super(parent);
		}

	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		super.collectDeviceInterfaceFactories(collector);

		collector.add(DigitalInputSensorInterface.class, () -> new DigitalInputSensorInterfaceImpl(this));
	}
}
