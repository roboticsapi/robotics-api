/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Device;

/**
 * Class that can provide (further) meta data for an {@link Activity}
 *
 * @param <T> type of meta data provided
 */
public interface ActivityPropertyProvider<T extends ActivityProperty> {

	/**
	 * Provides the type of {@link ActivityProperty} provided by this provider
	 */
	Class<T> getProvidedType();

	/**
	 * Provides the types of {@link ActivityProperty}s required to compute this
	 * provider's {@link ActivityProperty}
	 *
	 * @return
	 */
	List<Class<? extends ActivityProperty>> getDependencies();

	/**
	 * Factory method to create meta data for an {@link ActivityResult}
	 *
	 * @param device     device the data is to be created for
	 * @param properties other {@link ActivityProperty}s the result already has (for
	 *                   all types specified in {@link #getDependencies()}.
	 * @return the created {@link ActivityProperty}
	 */
	T provideProperty(Device device, Map<Class<? extends ActivityProperty>, ActivityProperty> properties);

	/**
	 * Provides the list of {@link Device}s supported by this provider
	 */
	List<Device> getSupportedDevices();
}