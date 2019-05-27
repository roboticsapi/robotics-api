/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.netcomm;

import org.roboticsapi.runtime.core.primitives.DoubleArrayNetcommIn;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.core.types.RPIdoubleArray;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;

/**
 * Communication Module to propagate value into net
 */
public class WriteDoubleArrayToNet extends DoubleArrayNetcommIn implements NetcommPrimitive {
	private final NetcommValue netcomm;

	/**
	 * Creates communication Module to propagate value into net
	 *
	 * @param name    Key (unique name) of the property
	 * @param initial Value to write to net
	 */
	public WriteDoubleArrayToNet(final String name, final Double[] initial) {
		super(name, toString(initial));
		netcomm = new NetcommValue(this);
		netcomm.setString(toString(initial));
	}

	public static String toString(Double[] initial) {
		RPIdoubleArray array = new RPIdoubleArray(initial.length);
		for (int i = 0; i < initial.length; i++) {
			array.set(i, new RPIdouble(initial[i]));
		}
		return array.toString();
	}

	public static String toString(double[] initial) {
		RPIdoubleArray array = new RPIdoubleArray(initial.length);
		for (int i = 0; i < initial.length; i++) {
			array.set(i, new RPIdouble(initial[i]));
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
