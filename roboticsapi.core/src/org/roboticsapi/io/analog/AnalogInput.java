/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.analog;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.io.Input;

public final class AnalogInput extends Input<AnalogInputDriver, Double> {

	private double minimalVoltage = 0d;
	private double maximumVoltage = 10d;

	/**
	 * Sensor for the value of the analog input. The sensor value is ranging from 0
	 * to 1.
	 * 
	 * @return the double sensor for the analog input's value
	 */
	@Override
	public final DoubleSensor getSensor() {
		return getDriver().getSensor();
	}

	/**
	 * Returns the current signal voltage of the analog input. The value is ranging
	 * from the input's minimal signal voltage to the maximum signal voltage.
	 * 
	 * @return the analog input's voltage
	 * 
	 * @see AnalogInput#getMinimalVoltage()
	 * @see AnalogInput#getMaximumVoltage()
	 */
	public final Double getVoltage() throws SensorReadException {
		return convertToVoltage(getValue());
	}

	/**
	 * Sensor for the signal voltage of the analog input. The sensor value is
	 * ranging from the input's minimal signal voltage to the maximum signal
	 * voltage.
	 * 
	 * @return the double sensor for the analog input's voltage
	 */
	public final DoubleSensor getVoltageSensor() {
		DoubleSensor minSensor = DoubleSensor.fromValue(minimalVoltage);
		DoubleSensor maxSensor = DoubleSensor.fromValue(maximumVoltage);
		DoubleSensor diff = maxSensor.minus(minSensor);

		return minSensor.add(diff.multiply(getSensor()));
	}

	/**
	 * Returns the minimal signal voltage of this analog input.
	 * 
	 * @return the minimal signal voltage
	 */
	public final double getMinimalVoltage() {
		return this.minimalVoltage;
	}

	/**
	 * Sets the minimal signal voltage. The pre-defined default value is 0 V.
	 * 
	 * @param minimalVoltage the new minimal signal voltage to set
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMinimalVoltage(double minimalVoltage) {
		this.minimalVoltage = minimalVoltage;
	}

	/**
	 * Returns the maximum signal voltage of this analog input.
	 * 
	 * @return the maximum signal voltage
	 */
	public final double getMaximumVoltage() {
		return maximumVoltage;
	}

	/**
	 * Sets the maximum signal voltage. The pre-defined default value is 10 V.
	 * 
	 * @param maximumVoltage the new maximum signal voltage to set
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMaximumVoltage(double maximumVoltage) {
		this.maximumVoltage = maximumVoltage;
	}

	/**
	 * Converts a given sensor value to the corresponding signal voltage.
	 * 
	 * @param value the sensor value.
	 * @return the corresponding signal voltage.
	 */
	public final double convertToVoltage(double value) {
		return minimalVoltage + (maximumVoltage - minimalVoltage) * value;
	}

	/**
	 * Converts a given signal voltage to a corresponding sensor value ranging from
	 * 0 to 1.
	 * 
	 * @param voltage the signal voltage.
	 * @return the corresponding sensor value.
	 */
	public final double convertToValue(double voltage) {
		return (voltage - minimalVoltage) / (maximumVoltage - minimalVoltage);
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		if (minimalVoltage >= maximumVoltage) {
			throw new ConfigurationException("",
					"The configured maximum signal voltage must be greater than the minimal signal voltage.");
		}
	}

}