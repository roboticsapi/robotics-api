/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.core.netcomm;

import org.roboticsapi.facet.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntArrayNetcommIn;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIintArray;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

/**
 * Communication Module to propagate value into net
 */
public class WriteIntArrayToNet extends IntArrayNetcommIn implements NetcommPrimitive {
	private NetcommValue netcomm;

	/**
	 * Creates communication Module to propagate value into net
	 *
	 * @param name    Key (unique name) of the property
	 * @param initial Value to write to net
	 */
	public WriteIntArrayToNet(final String name, final Integer[] initial) {
		super(name, toString(initial));
		netcomm = new NetcommValue(this);
		netcomm.setString(toString(initial));
	}

	public static String toString(int[] initial) {
		RPIintArray array = new RPIintArray(initial.length);
		for (int i = 0; i < initial.length; i++) {
			array.set(i, new RPIint(initial[i]));
		}
		return array.toString();
	}

	public static String toString(Integer[] initial) {
		RPIintArray array = new RPIintArray(initial.length);
		for (int i = 0; i < initial.length; i++) {
			array.set(i, new RPIint(initial[i]));
		}
		return array.toString();
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
