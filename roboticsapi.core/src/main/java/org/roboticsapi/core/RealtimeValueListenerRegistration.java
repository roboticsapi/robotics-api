/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * Container for specifying a SensorListener for a Sensor.
 * 
 * @param <T> the generic type of Sensor data
 */
public class RealtimeValueListenerRegistration<T> {

	private final RealtimeValue<T> sensor;
	private final RealtimeValueListener<? super T> listener;

	public RealtimeValueListenerRegistration(RealtimeValue<T> sensor, RealtimeValueListener<? super T> listener) {
		this.sensor = sensor;
		this.listener = listener;
	}

	public RealtimeValue<T> getSensor() {
		return sensor;
	}

	public RealtimeValueListener<? super T> getListener() {
		return listener;
	}

	public void registerListenerDirectly() throws RoboticsException {
		getSensor().addListener(getListener());
	}

}
