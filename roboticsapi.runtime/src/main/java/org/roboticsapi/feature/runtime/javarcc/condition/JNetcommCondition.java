/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc.condition;

import org.roboticsapi.facet.javarcc.JCondition;
import org.roboticsapi.facet.javarcc.JNetcommData;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.feature.runtime.javarcc.JNetHandle;

public class JNetcommCondition implements JCondition {
	String name;
	private JNetHandle handle;
	private String key;

	public JNetcommCondition(String name, JNetHandle handle, String key) {
		this.name = name;
		this.handle = handle;
		this.key = key;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Boolean isTrue() {
		JNetcommData<RPIbool> netcomm = handle.getNet().getNetcomm(key);
		if (handle.getNet().isUnloaded() && netcomm.getData() == null)
			return false;
		if (netcomm.getData() == null)
			return null;
		if (netcomm.isKilled())
			return null;
		return netcomm.getData().get();
	}

	@Override
	public boolean isFinal() {
		return handle.getNet().isKilled() || handle.getNet().isCompleted() || handle.getNet().isUnloaded();
	}
}
