/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc;

import org.roboticsapi.facet.javarcc.JCondition;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.feature.runtime.javarcc.condition.JNetcommCondition;

public class JNetResult extends NetResult {
	private String name;
	private NetcommValue condition;

	public JNetResult(JNetHandle handle, NetcommValue condition) {
		super(handle);
		this.condition = condition;
		name = handle.getName() + "." + condition.getName();
	}

	@Override
	public JNetHandle getHandle() {
		return (JNetHandle) super.getHandle();
	}

	public JCondition getCondition() {
		return new JNetcommCondition(name, getHandle(), condition.getName());
	}

	@Override
	public boolean isActive() {
		Boolean isTrue = getCondition().isTrue();
		return isTrue != null && isTrue;
	}

}
