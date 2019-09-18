/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.facet.javarcc.JNet.JNetcommListener;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.Type;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

public abstract class JPrimitive {
	Map<String, JOutPort<?>> outPorts = new HashMap<String, JOutPort<?>>();
	Map<String, JInPort<?>> inPorts = new HashMap<String, JInPort<?>>();
	Map<String, JParameter<?>> parameters = new HashMap<String, JParameter<?>>();
	private JNet net;

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	protected <T> JOutPort<T> add(String name, JOutPort<T> port) {
		if (outPorts.containsKey(name))
			throw new IllegalArgumentException("name");
		outPorts.put(name, port);
		port.setPrimitive(name, this);
		return port;
	}

	protected <T> JInPort<T> add(String name, JInPort<T> port) {
		if (inPorts.containsKey(name))
			throw new IllegalArgumentException("name");
		inPorts.put(name, port);
		port.setPrimitive(name, this);
		return port;
	}

	protected <T> JParameter<T> add(String name, JParameter<T> parameter) {
		if (parameters.containsKey(name))
			throw new IllegalArgumentException("name");
		parameters.put(name, parameter);
		parameter.setPrimitive(name, this);
		return parameter;
	}

	protected final <T extends JDevice> T device(JParameter<RPIstring> name, Class<T> clazz)
			throws IllegalArgumentException {
		T ret = getNet().getDevice(name.get().get(), clazz);
		if (ret == null) {
			for (String key : parameters.keySet())
				if (parameters.get(key) == name)
					throw new IllegalArgumentException(key);
			throw new IllegalArgumentException(name.get().get());
		}
		return ret;
	}

	protected final boolean anyNull(Type... types) {
		for (Type type : types) {
			if (type == null)
				return true;
		}
		return false;
	}

	protected final boolean anyNull(JInPort<?>... ports) {
		for (JInPort<?> port : ports) {
			if (port.get() == null)
				return true;
		}
		return false;
	}

	protected final void connected(JInPort<?>... ports) throws IllegalArgumentException {
		for (JInPort<?> port : ports)
			if (!port.isConnected())
				throw new IllegalArgumentException(port.name);
	}

	public abstract void checkParameters() throws IllegalArgumentException;

	public void readSensor() {
	}

	public Set<JDevice> getSensors() {
		return Collections.emptySet();
	}

	public void updateData() {
	}

	public void writeActuator() {
	}

	public void recordDebugValues(Map<JInPort<?>, String[]> debug) {
		for (JInPort<?> port : inPorts.values()) {
			if (debug.containsKey(port)) {
				debug.get(port)[getNet().getCycle() % debug.get(port).length] = "" + port.get();
			}
		}
	}

	public Set<JDevice> getActuators() {
		return Collections.emptySet();
	}

	public JNet getNet() {
		return net;
	}

	protected void setNet(JNet net) {
		this.net = net;
	}

	protected Set<JPrimitive> getReferencedPrimitives() {
		Set<JPrimitive> ret = new HashSet<JPrimitive>();
		for (JInPort<?> port : inPorts.values()) {
			if (port.getSource() != null)
				ret.add(port.getSource().getPrimitive());
		}
		return ret;
	}

	public JParameter<?> getParameter(String name) {
		return parameters.get(name);
	}

	public JInPort<?> getInPort(String name) {
		return inPorts.get(name);
	}

	public JOutPort<?> getOutPort(String name) {
		return outPorts.get(name);
	}

	protected void addValuesToReport(Map<JNetcommListener, String> map) {
	}

	protected void addListenersToReport(List<JNetcommListener> listeners) {
	}

	protected Set<JDevice> deviceSet(JDevice... devices) {
		Set<JDevice> ret = new HashSet<JDevice>();
		for (JDevice d : devices)
			ret.add(d);
		return ret;
	}

	public void cleanup() {
		net = null;
		for (JInPort<?> in : inPorts.values())
			in.connectTo(null);
	}

}
