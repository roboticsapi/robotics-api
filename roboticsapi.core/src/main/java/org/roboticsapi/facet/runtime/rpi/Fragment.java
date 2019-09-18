/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.ArrayList;
import java.util.List;

public class Fragment extends Primitive {

	private final List<Primitive> primitives = new ArrayList<Primitive>();

	public Fragment() {
		super("Fragment");
	}

	private static int nr = 0;

	public <T extends Primitive> T add(T primitive) {
		if (primitive.getName() == null) {
//			primitive.setName(primitive.getType() + (nr++));
			primitive.setName("p" + (nr++));
		}
		primitives.add(primitive);
		return primitive;
	}

	public void remove(Primitive primitive) {
		primitives.remove(primitive);
	}

	public List<Primitive> getPrimitives() {
		return primitives;
	}

	public FragmentInPort addInPort(String portName) {
		if (!portName.startsWith("in"))
			throw new IllegalArgumentException("The name of InPorts has to start with 'in', was " + portName);
		if (portName.contains("."))
			throw new IllegalArgumentException("The name of InPorts may not contain '.', was " + portName);
		FragmentInPort ret = new FragmentInPort(portName);
		add(ret);
		return ret;
	}

	public FragmentInPort addInPort(String portName, InPort port) {
		FragmentInPort ret = addInPort(portName);
		try {
			port.connectTo(ret.getInternalOutPort());
		} catch (RpiException e) { /* does not happen */
		}
		return ret;
	}

	public FragmentOutPort provideOutPort(OutPort port, String portName) {
		if (!portName.startsWith("out"))
			throw new IllegalArgumentException("The name of OutPorts has to start with 'out', was " + portName);
		if (portName.contains("."))
			throw new IllegalArgumentException("The name of OutPorts may not contain '.', was " + portName);
		if (!getPrimitives().contains(port.getPrimitive()) && port.getPrimitive() != this) {
			throw new IllegalArgumentException("The given port does not belong to the fragment.");
		}
		FragmentOutPort ret = new FragmentOutPort(portName, port);
		add(ret);
		return ret;
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
