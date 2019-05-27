/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment extends Primitive {

	private final List<Primitive> primitives = new ArrayList<Primitive>();
	private final Map<OutPort, OutPort> outPortCache = new HashMap<OutPort, OutPort>();

	public Fragment() {
		super("Fragment");
	}

	private static int nr = 0;

	public void add(Primitive primitive) {
		if (primitive.getName() == null) {
			primitive.setName(primitive.getType() + (nr++));
		}
		primitives.add(primitive);
	}

	public void remove(Primitive primitive) {
		primitives.remove(primitive);
	}

	public List<Primitive> getPrimitives() {
		return primitives;
	}

	public FragmentInPort addInPort(String portName) {
		FragmentInPort port = new FragmentInPort(portName);
		add(port);
		return port;
	}

	public FragmentOutPort provideOutPort(OutPort port, String portName) {
		OutPort childport = getOutPort(port);
		if (childport != null) {
			String name;
			if (portName != null) {
				name = portName;
			} else if (childport.getPrimitive() instanceof Fragment) {
				name = childport.getName();
			} else {
				name = childport.getPrimitiveName() + ":" + childport.getName();
			}

			for (OutPort out : getOutPorts()) {
				if (out instanceof FragmentOutPort && out.getName().equals(name)) {
					return (FragmentOutPort) out;
				}
			}

			FragmentOutPort ret = new FragmentOutPort(name, childport);
			add(ret);
			return ret;
		}
		return null;
	}

	private OutPort getOutPort(OutPort port) {
		if (outPortCache.containsKey(port)) {
			return outPortCache.get(port);
		}
		OutPort ret = null;
		for (Primitive primitive : primitives) {
			if (primitive.getOutPorts().contains(port)) {
				ret = port;
				break;
			} else if (primitive instanceof Fragment) {
				FragmentOutPort childPort = ((Fragment) primitive).provideOutPort(port, null);
				if (childPort != null) {
					ret = childPort;
					break;
				}
			}
		}
		outPortCache.put(port, ret);
		return ret;
	}

	public boolean correctLinks() throws RPIException {
		boolean result = true;
		for (Primitive prim : primitives) {
			if (prim instanceof Fragment) {
				((Fragment) prim).correctLinks();
			}
		}
		for (Primitive prim : primitives) {
			for (InPort in : prim.getInPorts()) {
				OutPort source = in.getConnectedPort();
				if (source == null || source.getPrimitive() == this) {
					continue;
				}
				OutPort newSource = getOutPort(source);
				if (newSource == null) {
					result = false;
					FragmentInPort fin = new FragmentInPort(
							(in.getPrimitive() instanceof Fragment ? "" : (in.getPrimitiveName() + ":"))
									+ in.getName());
					fin.addToPrimitive(this);
					fin.connectTo(source);
					add(fin);
					in.disconnect();
					in.connectTo(fin.getInternalOutPort());
				} else {
					if (source != newSource) {
						in.disconnect();
						in.connectTo(newSource);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Retrieves all Netcomm values sent to RPI
	 * 
	 * @return list of Netcomm values
	 */
	public List<NetcommValue> getNetcommToRPI() {
		final List<NetcommValue> ret = new ArrayList<NetcommValue>();
		for (final Primitive primitive : primitives) {
			if (primitive instanceof NetcommPrimitive) {
				NetcommValue netcommToRPI = ((NetcommPrimitive) primitive).getNetcommToRPI();

				if (netcommToRPI != null) {
					ret.add(netcommToRPI);
				}
			} else if (primitive instanceof Fragment) {
				ret.addAll(((Fragment) primitive).getNetcommToRPI());
			}
		}
		return ret;
	}

	/**
	 * Retrieves all Netcomm values received from RPI
	 * 
	 * @return list of Netcomm values
	 */
	public List<NetcommValue> getNetcommFromRPI() {
		final List<NetcommValue> ret = new ArrayList<NetcommValue>();
		for (final Primitive primitive : primitives) {
			if (primitive instanceof NetcommPrimitive) {
				NetcommValue netcommFromRPI = ((NetcommPrimitive) primitive).getNetcommFromRPI();

				if (netcommFromRPI != null) {
					ret.add(netcommFromRPI);
				}
			} else if (primitive instanceof Fragment) {
				ret.addAll(((Fragment) primitive).getNetcommFromRPI());
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return getName();
	}

}
