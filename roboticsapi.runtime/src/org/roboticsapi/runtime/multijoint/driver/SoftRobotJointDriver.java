/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver;

import java.util.List;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.actuator.GeneralActuatorException;
import org.roboticsapi.core.sensor.DeviceBasedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.IllegalJointValueException;
import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.runtime.AbstractSoftRobotActuatorDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;

public class SoftRobotJointDriver extends AbstractSoftRobotActuatorDriver implements JointDriver {

	public final class SoftRobotJointSensor extends DeviceBasedDoubleSensor<SoftRobotJointDriver> {

		private final String portName;

		public SoftRobotJointSensor(SoftRobotJointDriver jointDriver, String port) {
			super(jointDriver);
			this.portName = port;
		}

		public String getPortName() {
			return portName;
		}

		@Override
		public boolean equals2(Object obj) {
			return portName.equals(((SoftRobotJointSensor) obj).portName);
		}

		@Override
		protected Object[] getMoreObjectsForHashCode() {
			return new Object[] { portName };
		}

	}

	private int jointNumber;
	private String deviceType;

	public SoftRobotJointDriver() {
		super();
	}

	public SoftRobotJointDriver(SoftRobotRuntime runtime, String deviceName, String deviceType, int jointNumber) {
		super(runtime, deviceName);

		this.deviceType = deviceType;
		this.jointNumber = jointNumber;
	}

	@Override
	public String getDeviceType() {
		return deviceType;
	}

	public int getJointNumber() {
		return jointNumber;
	}

	/**
	 * Sets the number of the joint.
	 * 
	 * @param jointNumber The joint's new number.
	 */
	@ConfigurationProperty(Optional = false)
	public void setJointNumber(int jointNumber) {
		immutableWhenInitialized();
		this.jointNumber = jointNumber;
	}

	@Override
	public DoubleSensor getCommandedPositionSensor() {
		return new SoftRobotJointSensor(this, "outCommandedPosition");
	}

	@Override
	public DoubleSensor getCommandedVelocitySensor() {
		return new SoftRobotJointSensor(this, "outCommandedVelocity");
	}

	@Override
	public DoubleSensor getMeasuredPositionSensor() {
		return new SoftRobotJointSensor(this, "outMeasuredPosition");
	}

	@Override
	public DoubleSensor getMeasuredVelocitySensor() {
		return new SoftRobotJointSensor(this, "outMeasuredVelocity");
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		// TODO: Check joint number
		return interfaces.contains(SoftRobotMultiJointDeviceDriver.DEVICE_INTERFACE);

	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		List<ActuatorDriverRealtimeException> exceptions = super.defineActuatorDriverExceptions();

		// exceptions.add(new JointVelocityExceededException(this));
		// exceptions.add(new JointSpaceExceededException(this));
		exceptions.add(new IllegalJointValueException(this));
		exceptions.add(new ConcurrentAccessException(this));
		exceptions.add(new ActuatorNotOperationalException(this));
		exceptions.add(new GeneralActuatorException(this));

		return exceptions;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getDeviceName() + " Joint " + jointNumber + "'";
	}
}
