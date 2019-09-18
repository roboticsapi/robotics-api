/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.RealtimeValue;

/**
 * Interface representing drivers of digital/analog outputs.
 */
public interface OutputDriver<T> extends ActuatorDriver {

	/**
	 * Returns the output's sensor.
	 *
	 * @return the output's sensor.
	 */
	RealtimeValue<T> getSensor();

	/**
	 * Returns the output's number.
	 *
	 * @return the output's number.
	 */
	int getNumber();

}
