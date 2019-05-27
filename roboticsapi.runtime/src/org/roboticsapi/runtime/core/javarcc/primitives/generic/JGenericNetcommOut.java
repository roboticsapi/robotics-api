/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JNet;
import org.roboticsapi.runtime.javarcc.JNetcommData;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.javarcc.JNet.JNetcommListener;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.types.Type;

public class JGenericNetcommOut<T extends Type> extends JPrimitive {
	private JNetcommData<T> data = null;
	private JParameter<RPIstring> propKey = add("Key", new JParameter<RPIstring>());
	private JParameter<T> propValue = add("Value", new JParameter<T>());
	private JInPort<T> inValue = add("inValue", new JInPort<T>());
	private JParameter<RPIbool> propReport = add("Report", new JParameter<RPIbool>(new RPIbool(true)));

	@Override
	protected synchronized void setNet(JNet net) {
		super.setNet(net);
	}

	@Override
	public synchronized void checkParameters() throws IllegalArgumentException {
		connected(inValue);
		data = new JNetcommData<T>(propValue.get());
		getNet().addNetcomm(propKey.get().get(), data);
	}

	@Override
	public synchronized void updateData() {
		data.update(inValue.get(propValue), getNet().getTime());
	}

	public void writeActuator() {
	}

	@Override
	protected synchronized void addValuesToReport(Map<JNetcommListener, String> map) {
		super.addValuesToReport(map);

		if (propReport.get().get() && data.getData() != null) {
			String report = data.getData().toString();
			for (JNetcommListener listener : listeners)
				map.put(listener, report);
		}
	}

	@Override
	protected synchronized void addListenersToReport(List<JNetcommListener> listeners) {
		super.addListenersToReport(listeners);

		if (propReport.get().get()) {
			listeners.addAll(this.listeners);
		}
	}

	@Override
	public synchronized void cleanup() {
		super.cleanup();
		listeners.clear();
	}

	private List<JNetcommListener> listeners = new ArrayList<JNetcommListener>();

	public synchronized void addListener(JNetcommListener listener) {
		listeners.add(listener);
	}

	public void connectNetcommValue(NetcommValue netcommValue) {
		addListener(new JNetcommListener() {
			@Override
			public void valueChanged(String value) {
				try {
					netcommValue.setString(value);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void updatePerformed() {
				try {
					netcommValue.notifyUpdatePerformed();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
