/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.sensor.SensorReadException;

/**
 * Abstract implementation for a digital/analog output.
 * 
 * @param <ID> type of the driver
 * @param <T>  type of the input
 */
public abstract class Output<OD extends OutputDriver<T>, T> extends AbstractActuator<OD> {

	// private Integer number;
	//
	public final int getNumber() {
		return getDriver().getNumber(); // number != null ? number : -1;
	}

	//
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

	@Override
	public final void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	protected final void defineMaximumParameters() throws InvalidParametersException {
	}

	public final T getValue() throws SensorReadException {
		return getSensor().getCurrentValue();
	}

	public Sensor<T> getSensor() {
		return getDriver().getSensor();
	}

	@Override
	protected void setupDriver(OD driver) {
		// empty
	}

	public static class OutputException extends ActuatorDriverRealtimeException {
		private static final long serialVersionUID = 1L;

		public OutputException(OutputDriver<?> actuatorDriver) {
			super(actuatorDriver);
		}
	}

	public static class CommunicationException extends OutputException {
		private static final long serialVersionUID = 1L;

		public CommunicationException(OutputDriver<?> actuatorDriver) {
			super(actuatorDriver);
		}

	}

	public static class ValueOutOfRangeException extends OutputException {
		private static final long serialVersionUID = 1L;

		public ValueOutOfRangeException(OutputDriver<?> actuatorDriver) {
			super(actuatorDriver);
		}
	}

}
