/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform;

import org.roboticsapi.core.sensor.DeviceBasedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.runtime.AbstractSoftRobotActuatorDriver;
import org.roboticsapi.runtime.platform.driver.SoftRobotRobotBaseDriver;

/**
 * {@link DoubleSensor} implementation for measuring the velocity of a wheel
 * using the {@link SoftRobotRobotBaseDriver}.
 */
public final class PlatformWheelVelocityDoubleSensor extends DeviceBasedDoubleSensor<AbstractSoftRobotActuatorDriver> {

	private final int wheelNumber;

	/**
	 * Constructor.
	 * 
	 * @param driver      the driver
	 * @param wheelNumber the wheel number
	 */
	public PlatformWheelVelocityDoubleSensor(AbstractSoftRobotActuatorDriver driver, int wheelNumber) {
		super(driver);
		this.wheelNumber = wheelNumber;
	}

	/**
	 * Returns the wheel number.
	 * 
	 * @return the wheel number.
	 */
	public int getWheelNumber() {
		return wheelNumber;
	}

	@Override
	public boolean equals2(Object obj) {
		return wheelNumber == ((PlatformWheelVelocityDoubleSensor) obj).wheelNumber;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { wheelNumber };
	}

}
