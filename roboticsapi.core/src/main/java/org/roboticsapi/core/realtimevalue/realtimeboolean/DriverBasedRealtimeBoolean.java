/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;

/**
 * Abstract implementation for {@link RealtimeBoolean}s that are based on
 * {@link Device}s.
 *
 * @param <T> the device driver's type.
 */
public abstract class DriverBasedRealtimeBoolean<D extends DeviceDriver> extends RealtimeBoolean {

	private final D driver;

	/**
	 * Constructor.
	 *
	 * @param driver the driver
	 */
	public DriverBasedRealtimeBoolean(D driver) {
		super(driver.getRuntime());
		this.driver = driver;
	}

	/**
	 * Returns the driver.
	 *
	 * @return the driver.
	 */
	public final D getDriver() {
		return driver;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && driver.equals(((DriverBasedRealtimeBoolean<D>) obj).driver) && equals2(obj);
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
