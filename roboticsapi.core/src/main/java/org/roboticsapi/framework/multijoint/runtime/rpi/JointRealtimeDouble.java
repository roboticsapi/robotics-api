/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi;

import org.roboticsapi.core.realtimevalue.realtimedouble.DriverBasedRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.IndexedActuatorDriver;
import org.roboticsapi.framework.multijoint.Joint;

public class JointRealtimeDouble extends DriverBasedRealtimeDouble<IndexedActuatorDriver<Joint>> {

	private final String portName;

	public JointRealtimeDouble(IndexedActuatorDriver<Joint> jointDriver, String port) {
		super(jointDriver);
		this.portName = port;
	}

	public String getPortName() {
		return portName;
	}

	@Override
	public boolean equals2(Object obj) {
		return portName.equals(((JointRealtimeDouble) obj).portName);
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { portName };
	}

}