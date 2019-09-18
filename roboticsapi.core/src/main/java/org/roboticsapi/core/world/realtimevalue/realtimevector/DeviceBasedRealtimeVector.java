/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;

/**
 * Abstract implementation for {@link RealtimeVector}s that are based on
 * {@link Device}s.
 *
 * @param <T> the device driver's type.
 */
public abstract class DeviceBasedRealtimeVector<T extends DeviceDriver> extends RealtimeVector {

	private final T driver;

	/**
	 * Constructor.
	 *
	 * @param driver the driver
	 */
	public DeviceBasedRealtimeVector(final T driver) {
		super(driver.getRuntime());
		this.driver = driver;
	}

	/**
	 * Returns the driver.
	 *
	 * @return the driver.
	 */
	public final T getDriver() {
		return driver;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && driver.equals(((DeviceBasedRealtimeVector<?>) obj).driver) && equals2(obj);
	}

	protected boolean equals2(Object obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash(driver);
		return hash(hashCode, getMoreObjectsForHashCode());
	}

	/**
	 * Override to supply more objects for calculation the hash code.
	 *
	 * @return more objects for the hash code.
	 */
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[0];
	}

	@Override
	public final boolean isAvailable() {
		return driver.isPresent();
	}

}
