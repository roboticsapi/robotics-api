/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc;

import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;

public class RccNetResult extends NetResult {
	private final NetcommValue netcomm;

	public RccNetResult(SoftRobotNetHandle handle, NetcommValue netcomm) {
		super(handle);
		this.netcomm = netcomm;
	}

	@Override
	public SoftRobotNetHandle getHandle() {
		return (SoftRobotNetHandle) super.getHandle();
	}

	public String getKey() {
		return netcomm.getName();
	}

	public NetcommValue getNetcomm() {
		return netcomm;
	}

	@Override
	public boolean isActive() {
		return "true".equals(getNetcomm().getString());
	}
}
