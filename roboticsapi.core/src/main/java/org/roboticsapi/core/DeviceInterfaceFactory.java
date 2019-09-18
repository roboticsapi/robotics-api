/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Factory for the creation of {@link DeviceInterface} instances.
 *
 * @param <T> type of {@link DeviceInterface} created
 */
public interface DeviceInterfaceFactory<T extends DeviceInterface> {

	/**
	 * Creates a new {@link DeviceInterface} instance of this factory's type
	 *
	 * @return new instance of a corresponding {@link DeviceInterface}
	 */
	public T build();

}
