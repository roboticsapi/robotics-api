/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.digital;

import org.roboticsapi.core.SensorState;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.io.Input;

public final class DigitalInput extends Input<DigitalInputDriver, Boolean> {

	@Override
	public final BooleanSensor getSensor() {
		return getDriver().getSensor();
	}

	public final SensorState getSetState() {
		return getSensor().isTrue();
	}

}