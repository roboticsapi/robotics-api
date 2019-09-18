/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Modifies a given {@link DeviceInterface} to adapt it to a given task
 *
 * @param <T> type of {@link DeviceInterface} modified
 */
public interface DeviceInterfaceModifier<T extends DeviceInterface> {

	/**
	 * Modifies the given {@link DeviceInterface}
	 *
	 * @param instance {@link DeviceInterface} instance to modify
	 * @return new (derived) or modified {@link DeviceInterface}
	 */
	public T modify(T instance);

}
