/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.interfaces;

import org.roboticsapi.facet.javarcc.devices.JDevice;

public interface JInputOutputInterface extends JDevice {

	/**
	 * sets a digital output
	 * 
	 * @param port  Number of port, 0 based
	 * @param value Value to set port to
	 */
	void setDigitalOut(int port, boolean value);

	/**
	 * reads current value of digital output port
	 * 
	 * @param port Number of port, 0 based
	 * @return Current value of digital output port
	 */
	boolean getDigitalOut(int port);

	/**
	 * reads current value of digital input port
	 * 
	 * @param port Number of port, 0 based
	 * @return Current value of digital input port
	 */
	boolean getDigitalIn(int port);

	/**
	 * sets an aanlog output \param port Number of port, 0 based
	 * 
	 * @param value Value to set port to
	 */
	void setAnalogOut(int port, double value);

	/**
	 * reads current value of analog output port
	 * 
	 * @param port Number of port, 0 based
	 * @return Current value of analog output port
	 */
	double getAnalogOut(int port);

	/**
	 * reads current value of analog input port
	 * 
	 * @param port Number of port, 0 based
	 * @return Current value of analog input port
	 */
	double getAnalogIn(int port);

	int getNumDigitalIn();

	int getNumDigitalOut();

	int getNumAnalogIn();

	int getNumAnalogOut();

}
