/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

/**
 * An RPI parameter
 * 
 * @param <T> parameter type
 */
public class Parameter<T> {

	/** name of the parameter */
	private String name;

	/** value of the parameter */
	private T value;

	/**
	 * Creates a new parameter
	 * 
	 * @param name  name of the parameter
	 * @param value value of the parameter
	 */
	public Parameter(final String name, final T value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Sets the name of the parameter
	 * 
	 * @param name name of the parameter
	 */
	private void setName(final String name) {
		this.name = name;
	}

	/**
	 * Retrieves the name of the parameter
	 * 
	 * @return name of the parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the parameter
	 * 
	 * @param value parameter value
	 */
	public void setValue(final T value) {
		this.value = value;
	}

	/**
	 * Retrieves the value of the parameter
	 * 
	 * @return parameter value
	 */
	public T getValue() {
		return value;
	}
}
