/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.facet.javarcc.JNet.JNetcommListener;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;

public class JFragment extends JPrimitive {
	List<JPrimitive> children = new ArrayList<JPrimitive>();
	List<JPrimitive> sortedChildren = new ArrayList<JPrimitive>();
	JInPort<RPIbool> inActive = add("inActive", new JInPort<RPIbool>());

	public <T> JFragmentOutPort<T> providePort(String name, JOutPort<T> port) {
		JFragmentOutPort<T> ret = new JFragmentOutPort<T>();
		ret.getInnerPort().connectTo(port);
		add(name, ret);
		return ret;
	}

	@Override
	public <T> JInPort<T> add(String name, JInPort<T> port) {
		return super.add(name, port);
	}

	@Override
	public <T> JOutPort<T> add(String name, JOutPort<T> port) {
		return super.add(name, port);
	}

	public <T> JFragmentInPort<T> requirePort(String name, JInPort<T> port) {
		JFragmentInPort<T> ret = new JFragmentInPort<T>();
		port.connectTo(ret.getInnerPort());
		add(name, ret);
		return ret;
	}

	public <T> void forwardPort(String inName, String outName) {
		JFragmentInPort<T> in = new JFragmentInPort<T>();
		JFragmentOutPort<T> out = new JFragmentOutPort<T>();
		add(inName, in);
		add(outName, out);
		out.getInnerPort().connectTo(in.getInnerPort());
	}

	@Override
	protected void setNet(JNet net) {
		super.setNet(net);
		for (JPrimitive child : children)
			child.setNet(net);
		sortChildren();
	}

	private void sortChildren() {
		sortedChildren.clear();
		ArrayList<JPrimitive> available = new ArrayList<JPrimitive>();
		available.add(this);
		ArrayList<JPrimitive> toPlace = new ArrayList<JPrimitive>(children);
		boolean progress = true;
		while (!toPlace.isEmpty() && progress) {
			progress = false;
			for (JPrimitive p : toPlace) {
				Set<JPrimitive> needed = p.getReferencedPrimitives();
				if (available.containsAll(needed)) {
					sortedChildren.add(p);
					available.add(p);
					toPlace.remove(p);
					progress = true;
					break;
				}
			}
		}
		if (!toPlace.isEmpty())
			throw new IllegalArgumentException("Net cycle detected.");
	}

	public void add(JPrimitive child) {
		children.add(child);
	}

	@Override
	public void readSensor() {
		for (JPrimitive child : sortedChildren)
			child.readSensor();
	}

	@Override
	public void updateData() {
		if (inActive.get() == null || inActive.get().get() == true) {
			for (JPrimitive child : sortedChildren)
				child.updateData();
		}
	}

	@Override
	public void writeActuator() {
		if (inActive.get() == null || inActive.get().get() == true) {
			for (JPrimitive child : sortedChildren)
				child.writeActuator();
		}
	}

	@Override
	public void recordDebugValues(Map<JInPort<?>, String[]> debug) {
		super.recordDebugValues(debug);
		for (JPrimitive child : children) {
			child.recordDebugValues(debug);
		}
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
		for (JPrimitive child : children)
			child.checkParameters();
	}

	public Set<JDevice> getSensors() {
		Set<JDevice> ret = new HashSet<JDevice>();
		for (JPrimitive child : children)
			ret.addAll(child.getSensors());
		return ret;
	}

	public Set<JDevice> getActuators() {
		Set<JDevice> ret = new HashSet<JDevice>();
		for (JPrimitive child : children)
			ret.addAll(child.getActuators());
		return ret;
	}

	@Override
	protected void addValuesToReport(Map<JNetcommListener, String> map) {
		super.addValuesToReport(map);
		for (JPrimitive child : children)
			child.addValuesToReport(map);
	}

	@Override
	protected void addListenersToReport(List<JNetcommListener> listeners) {
		super.addListenersToReport(listeners);
		for (JPrimitive child : children)
			child.addListenersToReport(listeners);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		for (JPrimitive child : children)
			child.cleanup();
	}

}
