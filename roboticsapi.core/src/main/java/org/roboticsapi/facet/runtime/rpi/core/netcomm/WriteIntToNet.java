/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.core.netcomm;

import org.roboticsapi.facet.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntNetcommIn;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

/**
 * Communication Module to propagate value into net
 */
public class WriteIntToNet extends IntNetcommIn implements NetcommPrimitive {
	private NetcommValue netcomm;

	/**
	 * Creates communication Module to propagate value into net
	 * 
	 * @param name    Key (unique name) of the property
	 * @param initial Value to write to net
	 */
	public WriteIntToNet(final String name, final int initial) {
		super(name, initial);
		netcomm = new NetcommValue(this);
		netcomm.setString(new RPIint(initial).toString());
	}

	public NetcommValue getNetcomm() {
		return netcomm;
	}

	@Override
	public NetcommValue getNetcommToRPI() {
		return netcomm;
	}

	@Override
	public NetcommValue getNetcommFromRPI() {
		return null;
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
