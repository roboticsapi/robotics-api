/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class MockRealtimeValue<T> extends RealtimeValue<T> {
	private boolean available = true;

	public MockRealtimeValue(RoboticsRuntime runtime) {
		super(runtime);
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

	@Override
	public RealtimeValue<T> substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeValue<T> fromHistory(RealtimeDouble age, double maxAge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeBoolean isNull() {
		// TODO Auto-generated method stub
		return null;
	}

}
