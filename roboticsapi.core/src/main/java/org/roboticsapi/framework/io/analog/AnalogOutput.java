/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.analog;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.io.Output;
import org.roboticsapi.framework.io.activity.AnalogOutputSensorInterface;

/**
 * Representation of an analog output port.
 */
public final class AnalogOutput extends Output<AnalogOutputDriver, Double> {

	private final Dependency<Double> minimalVoltage;
	private final Dependency<Double> maximumVoltage;

	public AnalogOutput() {
		minimalVoltage = createDependency("minimalVoltage", 0d);
		maximumVoltage = createDependency("maximumVoltage", 10d);
	}

	/**
	 * Sensor for the value of the analog output. The sensor value is ranging from 0
	 * to 1.
	 *
	 * @return the double sensor for the analog output's value
	 */
	@Override
	public final RealtimeDouble getSensor() {
		return use(AnalogOutputSensorInterface.class).getRawValueSensor();
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
	public final Double getVoltage() throws RealtimeValueReadException {
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
	public final RealtimeDouble getVoltageSensor() {
		return use(AnalogOutputSensorInterface.class).getVoltageSensor();
	}

	/**
	 * Returns the minimal signal voltage of this analog output.
	 *
	 * @return the minimal signal voltage
	 */
	public final double getMinimalVoltage() {
		return this.minimalVoltage.get();
	}

	/**
	 * Sets the minimal signal voltage. The pre-defined default value is 0 V.
	 *
	 * @param minimalVoltage the new minimal signal voltage to set
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMinimalVoltage(double minimalVoltage) {
		this.minimalVoltage.set(minimalVoltage);
	}

	/**
	 * Returns the maximum signal voltage of this analog output.
	 *
	 * @return the maximum signal voltage
	 */
	public final double getMaximumVoltage() {
		return maximumVoltage.get();
	}

	/**
	 * Sets the maximum signal voltage. The pre-defined default value is 10 V.
	 *
	 * @param maximumVoltage the new maximum signal voltage to set
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMaximumVoltage(double maximumVoltage) {
		this.maximumVoltage.set(maximumVoltage);
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
		return value * (maximumVoltage.get() - minimalVoltage.get()) + minimalVoltage.get();
	}

	/**
	 * Converts a given double sensor representing the output's value [0, 1] to a
	 * double sensor representing the corresponding signal voltage.
	 *
	 * @param valueSensor the value sensor.
	 * @return the corresponding signal voltage sensor.
	 */
	public final RealtimeDouble convertToVoltage(RealtimeDouble valueSensor) {
		return valueSensor.multiply(maximumVoltage.get() - minimalVoltage.get()).add(minimalVoltage.get());
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
		if (voltage < minimalVoltage.get()) {
			throw new IllegalArgumentException(
					"The given voltage is less than the analog output's minimal signal voltage.");
		}
		if (voltage > maximumVoltage.get()) {
			throw new IllegalArgumentException(
					"The given voltage is greater than the analog output's maximum signal voltage.");
		}
		return (voltage - minimalVoltage.get()) / (maximumVoltage.get() - minimalVoltage.get());
	}

	/**
	 * Converts a given double sensor representing the output's signal voltage to a
	 * double sensor value representing the corresponding value from 0 to 1.
	 *
	 * @param voltageSensor the signal voltage sensor.
	 * @return the corresponding value sensor.
	 */
	public final RealtimeDouble convertToValue(RealtimeDouble voltageSensor) {
		return voltageSensor.minus(minimalVoltage.get()).divide(maximumVoltage.get() - minimalVoltage.get());
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();
		assertTrue(new String[] { "minimalVoltage", "maximumVoltage" }, minimalVoltage.get() < maximumVoltage.get(),
				"The configured maximum signal voltage must be greater than the minimal signal voltage.");
	}

}
