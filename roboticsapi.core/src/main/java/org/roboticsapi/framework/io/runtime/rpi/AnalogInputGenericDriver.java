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
import org.roboticsapi.core.realtimevalue.realtimedouble.DriverBasedRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractDeviceDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.framework.io.activity.AnalogInputSensorInterface;
import org.roboticsapi.framework.io.analog.AnalogInput;
import org.roboticsapi.framework.io.analog.AnalogInputDriver;
import org.roboticsapi.framework.io.runtime.rpi.activity.AnalogInputSensorInterfaceImpl;

/**
 * {@link AnalogInputDriver} implementation for the SoftRobot RCC.
 */
public class AnalogInputGenericDriver extends AbstractDeviceDriver<AnalogInput> implements AnalogInputDriver {

	private final Dependency<Integer> number;

	/**
	 * Constructor.
	 */
	public AnalogInputGenericDriver() {
		number = createDependency("number");
	}

	/**
	 * Constructor.
	 *
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected AnalogInputGenericDriver(int number, RpiRuntime runtime, String deviceName, AnalogInput device) {
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
	public RealtimeDouble getSensor() {
		return new AnalogInputRealtimeDouble(this);
	}

	/**
	 * {@link RealtimeDouble} implementation for {@link AnalogInputGenericDriver}
	 */
	public static final class AnalogInputRealtimeDouble extends DriverBasedRealtimeDouble<AnalogInputGenericDriver> {

		public AnalogInputRealtimeDouble(final AnalogInputGenericDriver parent) {
			super(parent);
		}

	}

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		if (!interfaces.containsKey("io")) {
			return false;
		}
		RPIint count = interfaces.get("io").get(RPIint.class, "anin");
		return count == null || count.get() >= getNumber();
	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		super.collectDeviceInterfaceFactories(collector);

		collector.add(AnalogInputSensorInterface.class, () -> new AnalogInputSensorInterfaceImpl(this));
	}

}
