/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * Container for specifying a SensorListener for a Sensor.
 * 
 * @param <T> the generic type of Sensor data
 */
public class SensorListenerRegistration<T> {

	private final Sensor<T> sensor;
	private final SensorListener<T> listener;

	public SensorListenerRegistration(Sensor<T> sensor, SensorListener<T> listener) {
		this.sensor = sensor;
		this.listener = listener;

	}

	public Sensor<T> getSensor() {
		return sensor;
	}

	public SensorListener<T> getListener() {
		return listener;
	}

	public void registerListenerDirectly() throws RoboticsException {
		getSensor().addListener(getListener());
	}

}
