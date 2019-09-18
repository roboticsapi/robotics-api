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
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.framework.io.activity.DigitalOutputInterface;
import org.roboticsapi.framework.io.digital.DigitalOutput;
import org.roboticsapi.framework.io.digital.DigitalOutputDriver;
import org.roboticsapi.framework.io.runtime.rpi.activity.DigitalOutputInterfaceImpl;

/**
 * {@link DigitalOutputDriver} implementation for the SoftRobot RCC.
 */
public class DigitalOutputGenericDriver extends AbstractActuatorDriver<DigitalOutput> implements DigitalOutputDriver {

	private final Dependency<Integer> number;

	/**
	 * Constructor.
	 */
	public DigitalOutputGenericDriver() {
		number = createDependency("number");
	}

	/**
	 * Constructor.
	 *
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected DigitalOutputGenericDriver(int number, RpiRuntime runtime, String deviceName, DigitalOutput device) {
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
		RPIint count = interfaces.get("io").get(RPIint.class, "digout");
		return count == null || count.get() >= getNumber();
	}

	@Override
	public RealtimeBoolean getSensor() {
		return new DigitalOutputRealtimeBoolean(this);
	}

	/**
	 * {@link RealtimeBoolean} implementation for {@link DigitalOutputGenericDriver}
	 */
	public static final class DigitalOutputRealtimeBoolean
			extends DriverBasedRealtimeBoolean<DigitalOutputGenericDriver> {

		public DigitalOutputRealtimeBoolean(final DigitalOutputGenericDriver parent) {
			super(parent);
		}

	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		super.collectDeviceInterfaceFactories(collector);

		collector.add(DigitalOutputInterface.class, () -> new DigitalOutputInterfaceImpl(this));
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
