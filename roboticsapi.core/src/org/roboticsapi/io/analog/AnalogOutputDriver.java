/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.analog;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.io.OutputDriver;

/**
 */
public interface AnalogOutputDriver extends OutputDriver<Double> {

	@Override
	DoubleSensor getSensor();

}
