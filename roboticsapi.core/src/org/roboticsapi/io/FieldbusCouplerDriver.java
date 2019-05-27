/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io;

import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.io.analog.AnalogInputDriver;
import org.roboticsapi.io.analog.AnalogOutputDriver;
import org.roboticsapi.io.digital.DigitalInputDriver;
import org.roboticsapi.io.digital.DigitalOutputDriver;

public interface FieldbusCouplerDriver extends DeviceDriver {

	DigitalInputDriver createDigitalInputDriver(int number);

	AnalogInputDriver createAnalogInputDriver(int number);

	DigitalOutputDriver createDigitalOutputDriver(int number);

	AnalogOutputDriver createAnalogOutputDriver(int number);

}
