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
import org.roboticsapi.io.Output;

/**
 * Representation of an analog output port.
 */
public final class AnalogOutput extends Output<AnalogOutputDriver, Double> {

	private double minimalVoltage = 0d;
	private double maximumVoltage = 10d;

	/**
	 * Sensor for the value of the analog output. The sensor value is ranging from 0
	 * to 1.
	 * 
	 * @return the double sensor for the analog output's value
	 */
	@Override
	public final DoubleSensor getSensor() {
		return getDriver().getSensor();
	}

	/**
	 * Returns the current signal voltage of the analog output. The value is ranging
	 * from the output's minimal signal voltage to the maximum signal voltage.
	 * 
	 * @return the analog output's voltage
	 * 
	 * @see AnalogOutput#getMinimalVoltage()
	 * @see AnalogOutput#getMaximumVoltage()
	 */
	public final Double getVoltage() throws SensorReadException {
		return convertToVoltage(getValue());
	}

	/**
	 * Sensor for the signal voltage of the analog output. The sensor value is
	 * ranging from the output's minimal signal voltage to the maximum signal
	 * voltage.
	 * 
	 * @return the double sensor for the analog output's voltage
	 * 
	 * @see AnalogOutput#getMinimalVoltage()
	 * @see AnalogOutput#getMaximumVoltage()
	 */
	public final DoubleSensor getVoltageSensor() {
		return convertToVoltage(getSensor());
	}

	/**
	 * Returns the minimal signal voltage of this analog output.
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
	 * Returns the maximum signal voltage of this analog output.
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
	 * @throws IllegalArgumentException if the given output value is not between the
	 *                                  0 and 1.
	 */
	public final double convertToVoltage(double value) {
		if (value < 0) {
			throw new IllegalArgumentException("The given output value is less than 0.");
		}
		if (value > 1) {
			throw new IllegalArgumentException("The given output value is greater than 1.");
		}
		return value * (maximumVoltage - minimalVoltage) + minimalVoltage;
	}

	/**
	 * Converts a given double sensor representing the output's value [0, 1] to a
	 * double sensor representing the corresponding signal voltage.
	 * 
	 * @param valueSensor the value sensor.
	 * @return the corresponding signal voltage sensor.
	 */
	public final DoubleSensor convertToVoltage(DoubleSensor valueSensor) {
		return valueSensor.multiply(maximumVoltage - minimalVoltage).add(minimalVoltage);
	}

	/**
	 * Converts a given signal voltage to a corresponding sensor value ranging from
	 * 0 to 1.
	 * 
	 * @param voltage the signal voltage.
	 * @return the corresponding sensor value.
	 * @throws IllegalArgumentException if the given voltage is not between the
	 *                                  output's minimal and its maximum signal
	 *                                  voltage.
	 */
	public final double convertToValue(double voltage) {
		if (voltage < minimalVoltage) {
			throw new IllegalArgumentException(
					"The given voltage is less than the analog output's minimal signal voltage.");
		}
		if (voltage > maximumVoltage) {
			throw new IllegalArgumentException(
					"The given voltage is greater than the analog output's maximum signal voltage.");
		}
		return (voltage - minimalVoltage) / (maximumVoltage - minimalVoltage);
	}

	/**
	 * Converts a given double sensor representing the output's signal voltage to a
	 * double sensor value representing the corresponding value from 0 to 1.
	 * 
	 * @param voltageSensor the signal voltage sensor.
	 * @return the corresponding value sensor.
	 */
	public final DoubleSensor convertToValue(DoubleSensor voltageSensor) {
		return voltageSensor.minus(minimalVoltage).divide(maximumVoltage - minimalVoltage);
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
