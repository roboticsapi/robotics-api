/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.analog;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.io.Input;
import org.roboticsapi.framework.io.activity.AnalogInputSensorInterface;

public final class AnalogInput extends Input<AnalogInputDriver, Double> {

	private final Dependency<Double> minimalVoltage, maximumVoltage;

	public AnalogInput() {
		minimalVoltage = createDependency("minimalVoltage", 0d);
		maximumVoltage = createDependency("maximumVoltage", 10d);
	}

	/**
	 * Sensor for the value of the analog input. The sensor value is ranging from 0
	 * to 1.
	 *
	 * @return the double sensor for the analog input's value
	 */
	@Override
	public final RealtimeDouble getSensor() {
		return use(AnalogInputSensorInterface.class).getRawValueSensor();
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
	public final Double getVoltage() throws RealtimeValueReadException {
		return convertToVoltage(getValue());
	}

	/**
	 * Sensor for the signal voltage of the analog input. The sensor value is
	 * ranging from the input's minimal signal voltage to the maximum signal
	 * voltage.
	 *
	 * @return the double sensor for the analog input's voltage
	 */
	public final RealtimeDouble getVoltageSensor() {
		return use(AnalogInputSensorInterface.class).getVoltageSensor();
	}

	/**
	 * Returns the minimal signal voltage of this analog input.
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
	 * Returns the maximum signal voltage of this analog input.
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
	 */
	public final double convertToVoltage(double value) {
		return minimalVoltage.get() + (maximumVoltage.get() - minimalVoltage.get()) * value;
	}

	/**
	 * Converts a given sensor value to the corresponding signal voltage.
	 *
	 * @param value the sensor value.
	 * @return the corresponding signal voltage.
	 */
	public final RealtimeDouble convertToVoltage(RealtimeDouble value) {
		return value.multiply((maximumVoltage.get() - minimalVoltage.get())).add(minimalVoltage.get());
	}

	/**
	 * Converts a given signal voltage to a corresponding sensor value ranging from
	 * 0 to 1.
	 *
	 * @param voltage the signal voltage.
	 * @return the corresponding sensor value.
	 */
	public final double convertToValue(double voltage) {
		return (voltage - minimalVoltage.get()) / (maximumVoltage.get() - minimalVoltage.get());
	}

	/**
	 * Converts a given signal voltage to a corresponding sensor value ranging from
	 * 0 to 1.
	 *
	 * @param voltage the signal voltage.
	 * @return the corresponding sensor value.
	 */
	public final RealtimeDouble convertToValue(RealtimeDouble voltage) {
		return voltage.minus(minimalVoltage.get()).divide((maximumVoltage.get() - minimalVoltage.get()));
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();
		assertTrue(new String[] { "minimalVoltage", "maximumVoltage" }, minimalVoltage.get() < maximumVoltage.get(),
				"The configured maximum signal voltage must be greater than the minimal signal voltage.");
	}

	@Override
	public RealtimeValue<Double> getSensor(RoboticsRuntime runtime) {
		return use(AnalogInputSensorInterface.class, runtime).getRawValueSensor();
	}

}