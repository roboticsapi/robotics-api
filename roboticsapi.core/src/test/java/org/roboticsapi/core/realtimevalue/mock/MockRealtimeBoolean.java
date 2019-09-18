/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.mock;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class MockRealtimeBoolean extends RealtimeBoolean {
	private boolean available = true;
	private final Boolean value;

	public MockRealtimeBoolean() {
		this(null, null);
	}

	public MockRealtimeBoolean(RoboticsRuntime runtime) {
		this(runtime, null);
	}

	public MockRealtimeBoolean(Boolean value) {
		this(null, value);
	}

	public MockRealtimeBoolean(RoboticsRuntime runtime, Boolean value) {
		super(runtime);
		this.value = value;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

	@Override
	protected Boolean calculateCheapValue() {
		return value;
	}

}
