/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.netoptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.FragmentInPort;
import org.roboticsapi.runtime.rpi.FragmentOutPort;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.rpi.UnknownPrimitive;

public class NetOptimizer {

	private static final boolean NUMERIC_NAMES = false;
	private static final boolean HIERARCHY_NAMES = false;

	private static final boolean RENAME_PRIMITIVES = true;
	private static final boolean RENAME_NETCOMMS = true;

	/**
	 * Optimizes the net (removes redundant primitives)
	 * 
	 * @throws RPIException
	 */
	public static void optimizeOld(Fragment net) throws RPIException {
		final Hashtable<String, Primitive> primitiveMap = new Hashtable<String, Primitive>();
		boolean changed = true;
		while (changed) {
			changed = false;
			primitiveMap.clear();
			for (final Primitive primitive : new ArrayList<Primitive>(net.getPrimitives())) {
				StringBuffer props = new StringBuffer(primitive.getType() + ":");
				for (final Parameter<?> p : primitive.getParameters()) {
					if (p.getValue() != null) {
						props.append(p.getValue().toString()).append(",");
					} else {
						props.append(",");
					}
				}
				for (final InPort i : primitive.getInPorts()) {
					props.append(i.getConnectedPort() != null
							? i.getConnectedPort().getPrimitiveName() + "." + i.getConnectedPort().getName()
							: "").append(",");
				}
				if (primitiveMap.containsKey(props.toString())) {
					Primitive otherPrimitive = primitiveMap.get(props.toString());
					for (final Primitive primitive2 : new ArrayList<Primitive>(net.getPrimitives())) {
						for (final InPort i : primitive2.getInPorts()) {
							if (i.getConnectedPort() != null
									&& i.getConnectedPort().getPrimitiveName().equals(primitive.getName())) {
								for (final OutPort o : otherPrimitive.getOutPorts()) {
									if (o.getName().equals(i.getConnectedPort().getName())) {
										i.disconnect();
										i.connectTo(o);
									}
								}
							}
						}
					}
					net.remove(primitive);
					changed = true;
					break;
				} else {
					primitiveMap.put(props.toString(), primitive);
				}
			}
		}
	}

	/**
	 * Optimizes the net (removes redundant primitives)
	 * 
	 * @throws RPIException
	 */
	public static void optimize(Fragment net) throws RPIException {
		optimize(0, net, "");
	}

	private static int optimize(int startNr, Fragment net, String namePrefix) throws RPIException {
		final HashMap<String, Primitive> primitiveMap = new HashMap<String, Primitive>();
		final HashMap<OutPort, List<InPort>> connectionsTo = new HashMap<OutPort, List<InPort>>();
		final HashMap<OutPort, List<FragmentOutPort>> fragmentOuts = new HashMap<OutPort, List<FragmentOutPort>>();

		final Queue<Primitive> todo = new LinkedList<Primitive>();
		final HashSet<Primitive> todoSet = new HashSet<Primitive>();
		final HashSet<Primitive> deleted = new HashSet<Primitive>();

		for (Primitive prim : net.getPrimitives()) {
			for (InPort in : prim.getInPorts()) {
				if (in.getConnectedPort() != null) {
					if (net != in.getConnectedPort().getPrimitive()
							&& !net.getPrimitives().contains(in.getConnectedPort().getPrimitive())) {
						throw new RPIException(
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

				int j = 0;
				for (InPort in : prim.getInPorts()) {
					if (in instanceof FragmentInPort) {
						((FragmentInPort) in).setName("i" + ++j);
					}
				}
				j = 0;
				for (OutPort out : prim.getOutPorts()) {
					if (out instanceof FragmentOutPort) {
						((FragmentOutPort) out).setName("o" + ++j);
					}
				}
			}

			for (InPort in : prim.getInPorts()) {
				if (in.getConnectedPort() != null) {
					if (net != in.getConnectedPort().getPrimitive()
							&& !net.getPrimitives().contains(in.getConnectedPort().getPrimitive())) {
						throw new RPIException("NetOptimizer bug: Primitive " + in.getConnectedPort().getPrimitiveName()
								+ " is not in net.");
					}
				}
			}
		}
		return startNr;
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
