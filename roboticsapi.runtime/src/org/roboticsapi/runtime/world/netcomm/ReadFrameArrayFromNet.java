/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.netcomm;

import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.world.primitives.FrameArrayNetcommOut;
import org.roboticsapi.runtime.world.types.RPIFrameArray;

/**
 * Communication module to read value from net
 */
public class ReadFrameArrayFromNet extends FrameArrayNetcommOut implements NetcommPrimitive {

	private final NetcommValue netcomm;

	/**
	 * Creates communication module to read value from net
	 * 
	 * @param key Key (unique name) of the property
	 */
	public ReadFrameArrayFromNet(final String key, int size) {
		super(new RPIstring(key), new RPIFrameArray(size));
		netcomm = new NetcommValue(this);
	}

	public NetcommValue getNetcomm() {
		return netcomm;
	}

	@Override
	public NetcommValue getNetcommToRPI() {
		return null;
	}

	@Override
	public NetcommValue getNetcommFromRPI() {
		return netcomm;
	}

	@Override
	public String getNetcommKey() {
		return getKey().getValue().toString();
	}

	@Override
	public void setNetcommKey(String key) {
		getKey().setValue(new RPIstring(key));
	}
}
