/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;

/**
 * Abstract implementation for {@link BooleanSensor}s that are based on
 * {@link Device}s.
 *
 * @param <T> the device driver's type.
 */
public abstract class DeviceBasedBooleanSensor<T extends DeviceDriver> extends BooleanSensor {

	private final T parent;

	/**
	 * Constructor.
	 *
	 * @param parent the driver
	 */
	public DeviceBasedBooleanSensor(final T parent) {
		super(parent.getRuntime());
		this.parent = parent;
	}

	@Override
	protected Boolean getDefaultValue() {
		return false;
	}

	/**
	 * Returns the driver.
	 *
	 * @return the driver.
	 */
	public final T getParent() {
		return parent;
	}

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && parent.equals(((DeviceBasedBooleanSensor<?>) obj).parent) && equals2(obj);
	}

	protected boolean equals2(Object obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash(parent);
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
		return parent.isPresent();
	}

}
