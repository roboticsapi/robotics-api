/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

import java.util.List;
import java.util.Vector;

/**
 * A variable used to communicate between RPI net and Robotics API
 */
public class NetcommValue {
	private String value;
	private final List<NetcommListener> listeners = new Vector<NetcommListener>();
	private final NetcommPrimitive primitive;

	/**
	 * Creates a new communication variable
	 * 
	 * @param name  name of the variable
	 * @param value initial value
	 */
	public NetcommValue(final NetcommPrimitive primitive) {
		this.primitive = primitive;
		this.value = null;
	}

	/**
	 * Retrieves the name of the variable
	 * 
	 * @return name of the variable
	 */
	public String getName() {
		return primitive.getNetcommKey();
	}

	/**
	 * Retrieves the current (string) value
	 * 
	 * @return current value
	 */
	public String getString() {
		return value;
	}

	/**
	 * Sets the value of the variable
	 * 
	 * @param newValue new value
	 */
	public void setString(final String newValue) {
		if (value == null || !value.equals(newValue)) {
			value = newValue;
			for (final NetcommListener listener : listeners) {
				listener.valueChanged(this);
			}
		}
	}

	public void notifyUpdatePerformed() {
		for (final NetcommListener listener : listeners) {
			listener.updatePerformed();
		}
	}

	/**
	 * Adds a value change listener
	 * 
	 * @param listener listener informed about value changes
	 */
	public void addNetcommListener(final NetcommListener listener) {
		listeners.add(listener);
		if (value != null) {
			listener.valueChanged(this);
		}
	}

	/**
	 * Removes a value change listener
	 * 
	 * @param listener listener to remove
	 */
	public void removeNetcommListener(final NetcommListener listener) {
		listeners.remove(listener);
	}
}
