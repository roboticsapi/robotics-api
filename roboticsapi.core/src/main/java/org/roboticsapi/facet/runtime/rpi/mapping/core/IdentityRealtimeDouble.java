/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class IdentityRealtimeDouble extends RealtimeDouble {

	private String name;

	public IdentityRealtimeDouble(String name, RoboticsRuntime runtime) {
		super(runtime);
		this.name = name;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

}
