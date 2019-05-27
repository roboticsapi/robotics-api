/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.actuator.AbstractDevice;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.sensor.SensorReadException;

/**
 * Abstract implementation for a digital/analog input.
 * 
 * @param <ID> type of the driver
 * @param <T>  type of the input
 */
public abstract class Input<ID extends InputDriver<T>, T> extends AbstractDevice<ID> {

	// TODO: Define errors in Input's Sensor, make Sensors able to throw errors

	public final int getNumber() {
		return getDriver().getNumber(); // number != null ? number : -1;
	}

	// @ConfigurationProperty
	// public final void setNumber(int number) {
	// immutableWhenInitialized();
	// this.number = number;
	// }

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		// if (number == null) {
		// throw new ConfigurationException("number", "Number must be set.");
		// }
	}

	public final T getValue() throws SensorReadException {
		return getSensor().getCurrentValue();
	}

	public Sensor<T> getSensor() {
		return getDriver().getSensor();
	}

	@Override
	protected void setupDriver(ID driver) {
		// empty
	}

}