/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractDevice;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.io.analog.AnalogInput;
import org.roboticsapi.io.analog.AnalogInputDriver;
import org.roboticsapi.io.analog.AnalogOutput;
import org.roboticsapi.io.analog.AnalogOutputDriver;
import org.roboticsapi.io.digital.DigitalInput;
import org.roboticsapi.io.digital.DigitalInputDriver;
import org.roboticsapi.io.digital.DigitalOutput;
import org.roboticsapi.io.digital.DigitalOutputDriver;

/**
 * Implementation for a fieldbus coupler with digital/analog I/Os.
 */
public final class FieldbusCoupler extends AbstractDevice<FieldbusCouplerDriver> {

	private int numberOfDigitalInputs;
	private int numberOfDigitalOutputs;
	private int numberOfAnalogInputs;
	private int numberOfAnalogOutputs;

	private final List<DigitalInput> digitalInputs = new ArrayList<DigitalInput>();
	private final List<DigitalOutput> digitalOutputs = new ArrayList<DigitalOutput>();
	private final List<AnalogInput> analogInputs = new ArrayList<AnalogInput>();
	private final List<AnalogOutput> analogOutputs = new ArrayList<AnalogOutput>();

	/**
	 * Constructor.
	 */
	public FieldbusCoupler() {
		super();
		this.numberOfDigitalInputs = 0;
		this.numberOfDigitalOutputs = 0;
		this.numberOfAnalogInputs = 0;
		this.numberOfAnalogOutputs = 0;
	}

	/**
	 * Returns a list of all digital inputs.
	 *
	 * @return a list of all digital inputs.
	 */
	public List<DigitalInput> getDigitalInputs() {
		return Collections.unmodifiableList(digitalInputs);
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
			return digitalInputs.get(number);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Returns the number of digital inputs of this field-bus coupler.
	 *
	 * @return the number of digital inputs.
	 */
	public int getNumberOfDigitalInputs() {
		return numberOfDigitalInputs;
	}

	/**
	 * Set the number of digital inputs for configuring this field-bus coupler.
	 *
	 * @param numberOfDigitalInputs the number of digital inputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfDigitalInputs(int numberOfDigitalInputs) {
		this.numberOfDigitalInputs = numberOfDigitalInputs;
	}

	/**
	 * Returns a list of all digital outputs.
	 *
	 * @return a list of all digital outputs.
	 */
	public List<DigitalOutput> getDigitalOutputs() {
		return Collections.unmodifiableList(digitalOutputs);
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
			return digitalOutputs.get(number);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Returns the number of digital outputs of this field-bus coupler.
	 *
	 * @return the number of digital outputs.
	 */
	public int getNumberOfDigitalOutputs() {
		return numberOfDigitalOutputs;
	}

	/**
	 * Set the number of digital outputs for configuring this field-bus coupler.
	 *
	 * @param numberOfDigitalOutputs the number of digital outputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfDigitalOutputs(int numberOfDigitalOutputs) {
		immutableWhenInitialized();
		this.numberOfDigitalOutputs = numberOfDigitalOutputs;
	}

	/**
	 * Returns a list of all analog inputs.
	 *
	 * @return a list of all analog inputs.
	 */
	public List<AnalogInput> getAnalogInputs() {
		return Collections.unmodifiableList(analogInputs);
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
			return analogInputs.get(number);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Returns the number of analog inputs of this field-bus coupler.
	 *
	 * @return the number of analog inputs.
	 */
	public int getNumberOfAnalogInputs() {
		return numberOfAnalogInputs;
	}

	/**
	 * Set the number of analog inputs for configuring this field-bus coupler.
	 *
	 * @param numberOfAnalogInputs the number of analog inputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfAnalogInputs(int numberOfAnalogInputs) {
		this.numberOfAnalogInputs = numberOfAnalogInputs;
	}

	/**
	 * Returns a list of all analog outputs.
	 *
	 * @return a list of all analog outputs.
	 */
	public List<AnalogOutput> getAnalogOutputs() {
		return Collections.unmodifiableList(analogOutputs);
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
			return analogOutputs.get(number);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@ConfigurationProperty(Optional = true)
	public void setAnalogOutput(int number, AnalogOutput output) {
		immutableWhenInitialized();
		analogOutputs.set(number, output);
	}

	/**
	 * Returns the number of analog outputs of this field-bus coupler.
	 *
	 * @return the number of analog outputs.
	 */
	public int getNumberOfAnalogOutputs() {
		return numberOfAnalogOutputs;
	}

	/**
	 * Set the number of analog outputs for configuring this field-bus coupler.
	 *
	 * @param numberOfAnalogOutputs the number of analog outputs
	 */
	@ConfigurationProperty(Optional = true)
	public void setNumberOfAnalogOutputs(int numberOfAnalogOutputs) {
		this.numberOfAnalogOutputs = numberOfAnalogOutputs;
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		FieldbusCouplerDriver driver = getDriver();

		fillDigitalInputs(createdObjects, driver);
		fillDigitalOutputs(createdObjects, driver);
		fillAnalogInputs(createdObjects, driver);
		fillAnalogOutputs(createdObjects, driver);
	}

	private void fillAnalogOutputs(Map<String, RoboticsObject> createdObjects, FieldbusCouplerDriver driver) {
		for (int i = 0; i < numberOfAnalogOutputs; i++) {
			AnalogOutput analogOutput = getAnalogOutput(i);

			if (analogOutput == null || !analogOutput.isInitialized()) {
				AnalogOutputDriver d = driver.createAnalogOutputDriver(i);

				analogOutput = new AnalogOutput();
				analogOutput.setName(getName() + " - Analog Input " + i);
				analogOutput.setDriver(d);
				analogOutputs.add(i, analogOutput);

				createdObjects.put("analogOutputDriver[" + i + "]", d);
				createdObjects.put("analogOutput[" + i + "]", analogOutput);
			}
		}
	}

	private void fillAnalogInputs(Map<String, RoboticsObject> createdObjects, FieldbusCouplerDriver driver) {
		for (int i = 0; i < numberOfAnalogInputs; i++) {
			AnalogInput analogInput = getAnalogInput(i);

			if (analogInput == null || !analogInput.isInitialized()) {
				AnalogInputDriver d = driver.createAnalogInputDriver(i);

				analogInput = new AnalogInput();
				analogInput.setName(getName() + " - Analog Input " + i);
				analogInput.setDriver(d);
				analogInputs.add(i, analogInput);

				createdObjects.put("analogInputDriver[" + i + "]", d);
				createdObjects.put("analogInput[" + i + "]", analogInput);
			}
		}
	}

	private void fillDigitalOutputs(Map<String, RoboticsObject> createdObjects, FieldbusCouplerDriver driver) {
		for (int i = 0; i < numberOfDigitalOutputs; i++) {
			DigitalOutput digitalOutput = getDigitalOutput(i);

			if (digitalOutput == null || !digitalOutput.isInitialized()) {
				DigitalOutputDriver d = driver.createDigitalOutputDriver(i);

				digitalOutput = new DigitalOutput();
				digitalOutput.setName(getName() + " - Digital Output " + i);
				digitalOutput.setDriver(d);
				digitalOutputs.add(i, digitalOutput);

				createdObjects.put("digitalOutputDriver[" + i + "]", d);
				createdObjects.put("digitalOutput[" + i + "]", digitalOutput);
			}
		}
	}

	private void fillDigitalInputs(Map<String, RoboticsObject> createdObjects, FieldbusCouplerDriver driver) {
		for (int i = 0; i < numberOfDigitalInputs; i++) {
			DigitalInput digitalInput = getDigitalInput(i);

			if (digitalInput == null || !digitalInput.isInitialized()) {
				DigitalInputDriver d = driver.createDigitalInputDriver(i);

				digitalInput = new DigitalInput();
				digitalInput.setName(getName() + " - Digital Input " + i);
				digitalInput.setDriver(d);
				digitalInputs.add(i, digitalInput);

				createdObjects.put("digitalInputDriver[" + i + "]", d);
				createdObjects.put("digitalInput[" + i + "]", digitalInput);
			}
		}
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		for (int i = 0; i < numberOfDigitalInputs; i++) {
			if (createdObjects.containsKey("digitalInput[" + i + "]")) {
				digitalInputs.set(i, null);
			}
		}

		for (int i = 0; i < numberOfDigitalOutputs; i++) {
			if (createdObjects.containsKey("digitalOutputs[" + i + "]")) {
				digitalOutputs.set(i, null);
			}
		}

		for (int i = 0; i < numberOfAnalogInputs; i++) {
			if (createdObjects.containsKey("analogInputs[" + i + "]")) {
				analogInputs.set(i, null);
			}
		}

		for (int i = 0; i < numberOfAnalogOutputs; i++) {
			if (createdObjects.containsKey("analogOutputs[" + i + "]")) {
				analogOutputs.set(i, null);
			}
		}
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		setParents(this);
	}

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		setParents(null);
	}

	private void setParents(FieldbusCoupler parent) throws EntityException {
		for (final DigitalInput input : this.digitalInputs) {
			input.setParent(parent);
		}

		for (final DigitalOutput output : this.digitalOutputs) {
			output.setParent(parent);
		}

		for (final AnalogInput input : this.analogInputs) {
			input.setParent(parent);
		}

		for (final AnalogOutput output : this.analogOutputs) {
			output.setParent(parent);
		}
	}

	@Override
	protected void setupDriver(FieldbusCouplerDriver driver) {
		// empty
	}

}
