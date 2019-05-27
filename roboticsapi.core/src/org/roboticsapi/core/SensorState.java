/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.util.HashCodeUtil;

/**
 * An event triggered by a sensor
 */
public class SensorState extends State {

	private final Sensor<Boolean> sensor;

	public SensorState(Sensor<Boolean> sensor) {
		super();
		this.sensor = sensor;
	}

	public Sensor<Boolean> getSensor() {
		return sensor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.Event#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && sensor == ((SensorState) obj).sensor;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), sensor);
	}
}
