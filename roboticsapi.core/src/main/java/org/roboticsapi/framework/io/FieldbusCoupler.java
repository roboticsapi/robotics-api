/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io;

import java.util.List;
import java.util.NoSuchElementException;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.actuator.AbstractDevice;
import org.roboticsapi.framework.io.analog.AnalogInput;
import org.roboticsapi.framework.io.analog.AnalogOutput;
import org.roboticsapi.framework.io.digital.DigitalInput;
import org.roboticsapi.framework.io.digital.DigitalOutput;

/**
 * Implementation for a fieldbus coupler with digital/analog I/Os.
 */
public final class FieldbusCoupler extends AbstractDevice<FieldbusCouplerDriver> {

	private final MultiDependency<DigitalInput> digitalInputProperties;
	private final MultiDependency<DigitalOutput> digitalOutputProperties;
	private final MultiDependency<AnalogInput> analogInputProperties;
	private final MultiDependency<AnalogOutput> analogOutputProperties;

	/**
	 * Constructor.
	 */
	public FieldbusCoupler() {
		digitalInputProperties = createMultiDependency("digitalInput", 0, new MultiDependency.Builder<DigitalInput>() {
			@Override
			public DigitalInput create(int index) {
				DigitalInput defaultPut = new DigitalInput();
				defaultPut.setName(getName() + " - Digital Input " + index);
				return defaultPut;
			};
		});

		digitalOutputProperties = createMultiDependency("digitalOutput", 0,
				new MultiDependency.Builder<DigitalOutput>() {
					@Override
					public DigitalOutput create(int index) {
						DigitalOutput defaultPut = new DigitalOutput();
						defaultPut.setName(getName() + " - Digital Output " + index);
						return defaultPut;
					};
				});

		analogInputProperties = createMultiDependency("analogInput", 0, new MultiDependency.Builder<AnalogInput>() {
			@Override
			public AnalogInput create(int index) {
				AnalogInput defaultPut = new AnalogInput();
				defaultPut.setName(getName() + " - Analog Input " + index);
				return defaultPut;
			};
		});

		analogOutputProperties = createMultiDependency("analogOutput", 0, new MultiDependency.Builder<AnalogOutput>() {
			@Override
			public AnalogOutput create(int index) {
				AnalogOutput defaultPut = new AnalogOutput();
				defaultPut.setName(getName() + " - Analog Output " + index);
				return defaultPut;
			};
		});
	}

	/**
	 * Returns a list of all digital inputs.
	 *
	 * @return a list of all digital inputs.
	 */
	public List<DigitalInput> getDigitalInputs() {
		return digitalInputProperties.getAll();
	}

	/**
	 * Returns the specified digital input.
	 *
	 * @param number the input's number.
	 * @return the digital input corresponding to the given number or
	 *         <code>null</code>.
	 */
	public DigitalInput getDigitalInput(int number) {
		try {
			return digitalInputProperties.get(number);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Returns the number of digital inputs of this field-bus coupler.
	 *
	 * @return the number of digital inputs.
	 */
	public int getNumberOfDigitalInputs() {
		return digitalInputProperties.get();
	}

	/**
	 * Set the number of digital inputs for configuring this field-bus coupler.
	 *
	 * @param numberOfDigitalInputs the number of digital inputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfDigitalInputs(int numberOfDigitalInputs) {
		digitalInputProperties.set(numberOfDigitalInputs);
	}

	/**
	 * Returns a list of all digital outputs.
	 *
	 * @return a list of all digital outputs.
	 */
	public List<DigitalOutput> getDigitalOutputs() {
		return digitalOutputProperties.getAll();
	}

	/**
	 * Returns the specified digital output.
	 *
	 * @param number the output's number.
	 * @return the digital output corresponding to the given number or
	 *         <code>null</code>.
	 */
	public DigitalOutput getDigitalOutput(int number) {
		try {
			return digitalOutputProperties.get(number);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Returns the number of digital outputs of this field-bus coupler.
	 *
	 * @return the number of digital outputs.
	 */
	public int getNumberOfDigitalOutputs() {
		return digitalOutputProperties.get();
	}

	/**
	 * Set the number of digital outputs for configuring this field-bus coupler.
	 *
	 * @param numberOfDigitalOutputs the number of digital outputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfDigitalOutputs(int numberOfDigitalOutputs) {
		digitalOutputProperties.set(numberOfDigitalOutputs);
	}

	/**
	 * Returns a list of all analog inputs.
	 *
	 * @return a list of all analog inputs.
	 */
	public List<AnalogInput> getAnalogInputs() {
		return analogInputProperties.getAll();
	}

	/**
	 * Returns the specified analog input.
	 *
	 * @param number the input's number.
	 * @return the analog input corresponding to the given number or
	 *         <code>null</code>.
	 */
	public AnalogInput getAnalogInput(int number) {
		try {
			return analogInputProperties.get(number);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Returns the number of analog inputs of this field-bus coupler.
	 *
	 * @return the number of analog inputs.
	 */
	public int getNumberOfAnalogInputs() {
		return analogInputProperties.get();
	}

	/**
	 * Set the number of analog inputs for configuring this field-bus coupler.
	 *
	 * @param numberOfAnalogInputs the number of analog inputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfAnalogInputs(int numberOfAnalogInputs) {
		analogInputProperties.set(numberOfAnalogInputs);
	}

	/**
	 * Returns a list of all analog outputs.
	 *
	 * @return a list of all analog outputs.
	 */
	public List<AnalogOutput> getAnalogOutputs() {
		return analogOutputProperties.getAll();
	}

	/**
	 * Returns the specified analog output.
	 *
	 * @param number the output's number.
	 * @return the analog output corresponding to the given number or
	 *         <code>null</code>.
	 */
	public AnalogOutput getAnalogOutput(int number) {
		try {
			return analogOutputProperties.get(number);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	@ConfigurationProperty(Optional = true)
	public void setAnalogOutput(int number, AnalogOutput output) {
		analogOutputProperties.set(number, output);
	}

	/**
	 * Returns the number of analog outputs of this field-bus coupler.
	 *
	 * @return the number of analog outputs.
	 */
	public int getNumberOfAnalogOutputs() {
		return analogOutputProperties.get();
	}

	/**
	 * Set the number of analog outputs for configuring this field-bus coupler.
	 *
	 * @param numberOfAnalogOutputs the number of analog outputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfAnalogOutputs(int numberOfAnalogOutputs) {
		analogOutputProperties.set(numberOfAnalogOutputs);
	}

}
