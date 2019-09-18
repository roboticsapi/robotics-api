/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.runtime.rpi;

import org.roboticsapi.core.realtimevalue.realtimedouble.DriverBasedRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;

/**
 * {@link RealtimeDouble} implementation for measuring the velocity of a wheel
 * using the {@link SoftRobotRobotBaseDriver}.
 */
public final class PlatformWheelVelocityRealtimeDouble extends DriverBasedRealtimeDouble<NamedActuatorDriver<?>> {

	private final int wheelNumber;

	/**
	 * Constructor.
	 *
	 * @param driver      the driver
	 * @param wheelNumber the wheel number
	 */
	public PlatformWheelVelocityRealtimeDouble(NamedActuatorDriver<?> driver, int wheelNumber) {
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
		return wheelNumber == ((PlatformWheelVelocityRealtimeDouble) obj).wheelNumber;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { wheelNumber };
	}

}
