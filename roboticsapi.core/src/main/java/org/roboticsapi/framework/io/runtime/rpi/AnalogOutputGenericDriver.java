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
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.framework.io.activity.AnalogOutputInterface;
import org.roboticsapi.framework.io.analog.AnalogOutput;
import org.roboticsapi.framework.io.analog.AnalogOutputDriver;
import org.roboticsapi.framework.io.runtime.rpi.activity.AnalogOutputInterfaceImpl;

/**
 * {@link AnalogOutputDriver} implementation for the SoftRobot RCC.
 */
public class AnalogOutputGenericDriver extends AbstractActuatorDriver<AnalogOutput> implements AnalogOutputDriver {
	private final Dependency<Integer> number;

	/**
	 * Constructor.
	 */
	public AnalogOutputGenericDriver() {
		number = createDependency("number");
	}

	/**
	 * Constructor.
	 *
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected AnalogOutputGenericDriver(int number, RpiRuntime runtime, String deviceName, AnalogOutput device) {
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
		RPIint count = interfaces.get("io").get(RPIint.class, "anout");
		return count == null || count.get() >= getNumber();
	}

	@Override
	public RealtimeDouble getSensor() {
		return new AnalogOutputRealtimeDouble(this);
	}

	/**
	 * {@link RealtimeDouble} implementation for {@link AnalogOutputGenericDriver}
	 */
	public static final class AnalogOutputRealtimeDouble extends DriverBasedRealtimeDouble<AnalogOutputGenericDriver> {

		/**
		 * Constructor.
		 *
		 * @param parent the driver
		 */
		public AnalogOutputRealtimeDouble(final AnalogOutputGenericDriver parent) {
			super(parent);
		}

	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		super.collectDeviceInterfaceFactories(collector);

		collector.add(AnalogOutputInterface.class, () -> new AnalogOutputInterfaceImpl(this));
	}

	// @Override
	// public List<ActuatorDriverRealtimeException>
	// defineActuatorDriverExceptions() {
	// final List<ActuatorDriverRealtimeException> errors = new
	// ArrayList<ActuatorDriverRealtimeException>();
	// errors.add(new CommunicationException(this));
	// errors.add(new ValueOutOfRangeException(this));
	// return errors;
	// }

}
