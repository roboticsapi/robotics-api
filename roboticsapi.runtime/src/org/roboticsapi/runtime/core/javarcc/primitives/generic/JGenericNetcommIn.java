/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.javarcc.JNet;
import org.roboticsapi.runtime.javarcc.JNetcommData;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.rpi.NetcommListener;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.types.Type;

public class JGenericNetcommIn<T extends Type> extends JPrimitive {
	JNetcommData<T> data = null;
	JParameter<RPIstring> propKey = add("Key", new JParameter<RPIstring>());
	JParameter<T> propValue = add("Value", new JParameter<T>());
	JOutPort<Long> outLastUpdated = add("outLastUpdated", new JOutPort<Long>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	@Override
	protected void setNet(JNet net) {
		super.setNet(net);
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
		data = new JNetcommData<T>(propValue.get());
		getNet().addNetcomm(propKey.get().get(), data);
	}

	@Override
	public void updateData() {
		Long lastUpdated = data.getLastUpdated();
		T value = data.getData();
		if (lastUpdated == -1) {
			outValue.set(propValue.get());
			outLastUpdated.set(null);
		} else {
			outLastUpdated.set(data.getLastUpdated());
			outValue.set(value);
		}
	}

	public JNetcommData<T> getNetcommData() {
		return data;
	}

	@Override
	public void cleanup() {
		super.cleanup();
		if (netcommValue != null)
			netcommValue.removeNetcommListener(netcommListener);
	}

	private NetcommValue netcommValue = null;
	private NetcommListener netcommListener = new NetcommListener() {
		@Override
		public void valueChanged(NetcommValue value) {
			if (getNetcommData() != null && getNet() != null) {
				long time = getNet().getTime();
				if (time == -1)
					time = System.currentTimeMillis();
				getNetcommData().update(value.getString(), time);
			}
		}

		@Override
		public void updatePerformed() {
		}
	};

	public void connectNetcommValue(NetcommValue netcommValue) {
		this.netcommValue = netcommValue;
		netcommValue.addNetcommListener(netcommListener);
	}
}
