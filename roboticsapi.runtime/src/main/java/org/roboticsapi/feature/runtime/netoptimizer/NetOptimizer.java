/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.netoptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.FragmentOutPort;
import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.UnknownPrimitive;

public class NetOptimizer {

	private static final boolean NUMERIC_NAMES = true;
	private static final boolean HIERARCHY_NAMES = true;

	private static final boolean RENAME_PRIMITIVES = true;
	private static final boolean RENAME_NETCOMMS = true;
	private static final boolean RENAME_FRAGMENTPORTS = true;

	private static final boolean REMOVE_FRAGMENTS = true;

	/**
	 * Optimizes the net (removes redundant primitives)
	 *
	 * @throws RpiException
	 */
	public static void optimize(Fragment net) throws RpiException {
		optimize(0, net, "");
	}

	private static int optimize(int startNr, Fragment net, String namePrefix) throws RpiException {
		final HashMap<String, Primitive> primitiveMap = new HashMap<String, Primitive>();
		final HashMap<OutPort, List<InPort>> connectionsTo = new HashMap<OutPort, List<InPort>>();
		final HashMap<OutPort, List<FragmentOutPort>> fragmentOuts = new HashMap<OutPort, List<FragmentOutPort>>();

		final Queue<Primitive> todo = new LinkedList<Primitive>();
		final HashSet<Primitive> todoSet = new HashSet<Primitive>();
		final HashSet<Primitive> deleted = new HashSet<Primitive>();

		if (REMOVE_FRAGMENTS) {
			defrag(net);
		}

		for (Primitive prim : net.getPrimitives()) {
			for (InPort in : prim.getInPorts()) {
				if (in.getConnectedPort() != null) {
					if (net != in.getConnectedPort().getPrimitive()
							&& !net.getPrimitives().contains(in.getConnectedPort().getPrimitive())) {
						throw new RpiException(
								"Primitive " + in.getConnectedPort().getPrimitiveName() + " does not exist.");
					}
				}
			}
		}

		for (final Primitive primitive : net.getPrimitives()) {
			for (InPort in : primitive.getInPorts()) {
				OutPort from = in.getConnectedPort();
				if (from != null) {
					if (!connectionsTo.containsKey(from)) {
						connectionsTo.put(from, new ArrayList<InPort>());
					}
					connectionsTo.get(from).add(in);
				}
			}
			if (!(primitive instanceof Fragment)) {
				todo.add(primitive);
				todoSet.add(primitive);
			}
		}

		for (OutPort out : net.getOutPorts()) {
			FragmentOutPort fout = (FragmentOutPort) out;
			if (!fragmentOuts.containsKey(fout.getInnerPort())) {
				fragmentOuts.put(fout.getInnerPort(), new ArrayList<FragmentOutPort>());
			}
			fragmentOuts.get(fout.getInnerPort()).add(fout);
		}

		while (!todo.isEmpty()) {
			Primitive primitive = todo.poll();
			todoSet.remove(primitive);
			if (deleted.contains(primitive)) {
				continue;
			}

			StringBuffer props = new StringBuffer(primitive.getType() + ":");
			for (final Parameter<?> p : primitive.getParameters()) {
				if (p.getValue() == null) {
					props.append(",");
				} else {
					props.append(p.getValue().toString()).append(",");
				}
			}
			for (final InPort i : primitive.getInPorts()) {
				props.append(i.getConnectedPort() != null
						? i.getConnectedPort().getPrimitiveName() + "." + i.getConnectedPort().getName()
						: "").append(",");
			}
			String key = props.toString();
			if (primitiveMap.containsKey(key)) {
				Primitive otherPrimitive = primitiveMap.get(key);
				if (otherPrimitive == primitive) {
					continue;
				}
				for (OutPort out : primitive.getOutPorts()) {
					OutPort newOut = null;
					for (OutPort o : otherPrimitive.getOutPorts()) {
						if (o.getName().equals(out.getName())) {
							newOut = o;
						}
					}
					if (newOut == null && otherPrimitive instanceof UnknownPrimitive) {
						newOut = new OutPort(out.getName());
						((UnknownPrimitive) otherPrimitive).add(newOut);
					}
					if (connectionsTo.containsKey(out)) {
						for (InPort in : new ArrayList<InPort>(connectionsTo.get(out))) {
							if (deleted.contains(in.getPrimitive())) {
								continue;
							}
							in.disconnect();
							in.connectTo(newOut);
							if (!connectionsTo.containsKey(newOut)) {
								connectionsTo.put(newOut, new ArrayList<InPort>());
							}

							connectionsTo.get(newOut).add(in);
							if (!todoSet.contains(in.getPrimitive())) {
								todo.add(in.getPrimitive());
								todoSet.add(in.getPrimitive());
							}
						}
					}
					if (fragmentOuts.containsKey(out)) {
						for (FragmentOutPort fout : new ArrayList<FragmentOutPort>(fragmentOuts.get(out))) {
							fout.setInnerPort(newOut);
							if (!fragmentOuts.containsKey(newOut)) {
								fragmentOuts.put(newOut, new ArrayList<FragmentOutPort>());
							}
							fragmentOuts.get(newOut).add(fout);
						}
					}
				}
				net.remove(primitive);
				deleted.add(primitive);
			} else {
				primitiveMap.put(props.toString(), primitive);
			}
		}
		int i = 0;
		for (Primitive prim : net.getPrimitives()) {
			String newName;
			if (HIERARCHY_NAMES) {
				newName = namePrefix + createName(++i);
			} else {
				newName = "" + createName(++startNr);
			}

			if (RENAME_PRIMITIVES) {
				prim.setName(newName);
			}

			if (RENAME_NETCOMMS) {
				if (prim instanceof NetcommPrimitive) {
					NetcommPrimitive netcomm = (NetcommPrimitive) prim;
					if (netcomm.getNetcommKey().equals("Override") || netcomm.getNetcommKey().equals("Error")) {
						continue;
					}
					netcomm.setNetcommKey("_" + newName);
				}
			}

			if (prim instanceof Fragment) {
				startNr = optimize(startNr, (Fragment) prim, newName + ":");

				if (RENAME_FRAGMENTPORTS) {
					int j = 0;
					for (InPort in : prim.getInPorts()) {
						if (in instanceof FragmentInPort) {
							if (!in.getName().startsWith("in")) {
								RAPILogger.getLogger(NetOptimizer.class).warning("Invalid port name " + in.getName());
							}
							((FragmentInPort) in).setName("i" + ++j);
						}
					}
					j = 0;
					for (OutPort out : prim.getOutPorts()) {
						if (out instanceof FragmentOutPort) {
							if (!out.getName().startsWith("out")) {
								RAPILogger.getLogger(NetOptimizer.class).warning("Invalid port name " + out.getName());
							}
							((FragmentOutPort) out).setName("o" + ++j);
						}
					}
				}
			}

			for (InPort in : prim.getInPorts()) {
				if (in.getConnectedPort() != null) {
					if (net != in.getConnectedPort().getPrimitive()
							&& !net.getPrimitives().contains(in.getConnectedPort().getPrimitive())) {
						throw new RpiException("NetOptimizer bug: Primitive " + in.getConnectedPort().getPrimitiveName()
								+ " is not in net.");
					}
				}
			}
		}
		return startNr;
	}

	private static void defrag(Fragment net) throws RpiException {
		for (int i = 0; i < net.getPrimitives().size(); i++) {
			Primitive prim = net.getPrimitives().get(i);
			if (prim instanceof Fragment) {
				Fragment frag = (Fragment) prim;
				defrag(frag);

				boolean mayDefrag = true;
				Map<OutPort, OutPort> portMap = new HashMap<OutPort, OutPort>();
				for (InPort port : frag.getInPorts()) {
					if (port.getName().equals("inActive")) {
						mayDefrag = false;
					} else if (port instanceof FragmentInPort) {
						portMap.put(((FragmentInPort) port).getInternalOutPort(), port.getConnectedPort());
					}
				}
				for (OutPort port : frag.getOutPorts()) {
					if (port instanceof FragmentOutPort) {
						portMap.put(port, ((FragmentOutPort) port).getInnerPort());
					}
				}
				if (!mayDefrag) {
					continue;
				}

				for (Primitive innerprim : new ArrayList<Primitive>(frag.getPrimitives())) {
					frag.remove(innerprim);
					net.add(innerprim);
				}
				for (Primitive outerprim : net.getPrimitives()) {
					for (InPort port : outerprim.getInPorts()) {
						OutPort from = port.getConnectedPort();
						while (portMap.containsKey(from)) {
							from = portMap.get(from);
						}
						if (from != port.getConnectedPort()) {
							port.disconnect();
							port.connectTo(from);
						}
					}
				}
				for (OutPort port : net.getOutPorts()) {
					if (port instanceof FragmentOutPort) {
						FragmentOutPort fPort = (FragmentOutPort) port;
						OutPort innerPort = fPort.getInnerPort();
						while (portMap.containsKey(innerPort)) {
							innerPort = portMap.get(innerPort);
						}
						if (innerPort != fPort.getInnerPort()) {
							fPort.setInnerPort(innerPort);
						}
					}
				}
				net.remove(frag);
				i--;
			}
		}
	}

	private static String createName(int nr) {
		String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		if (NUMERIC_NAMES) {
			return "" + nr;
		}
		String ret = "";
		int rest = nr;
		while (rest > 0) {
			ret = validChars.charAt(rest % validChars.length()) + ret;
			rest = rest / validChars.length();
		}
		return ret;
	}
}
