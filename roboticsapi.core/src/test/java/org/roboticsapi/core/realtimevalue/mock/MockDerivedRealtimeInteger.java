/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.mock;

import org.roboticsapi.core.realtimevalue.realtimeinteger.DerivedRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public class MockDerivedRealtimeInteger extends DerivedRealtimeInteger {
	public MockDerivedRealtimeInteger(RealtimeInteger sensor, RealtimeInteger... sensors) {
		super(getSensorArray(sensor, sensors));
	}

	private static RealtimeInteger[] getSensorArray(RealtimeInteger sensor, RealtimeInteger... sensors) {
		RealtimeInteger[] tempSensors = new RealtimeInteger[sensors.length + 1];
		tempSensors[0] = sensor;
		System.arraycopy(sensors, 0, tempSensors, 1, sensors.length);
		return tempSensors;
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return (Integer) values[0];
	}
}
