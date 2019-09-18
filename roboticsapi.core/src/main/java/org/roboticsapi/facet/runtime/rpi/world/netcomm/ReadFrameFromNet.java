/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.world.netcomm;

import org.roboticsapi.facet.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameNetcommOut;

/**
 * Communication module to read value from net
 */
public class ReadFrameFromNet extends FrameNetcommOut implements NetcommPrimitive {

	private NetcommValue netcomm;

	/**
	 * Creates communication module to read value from net
	 *
	 * @param key Key (unique name) of the property
	 */
	public ReadFrameFromNet(final String key) {
		super(key, true);
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
