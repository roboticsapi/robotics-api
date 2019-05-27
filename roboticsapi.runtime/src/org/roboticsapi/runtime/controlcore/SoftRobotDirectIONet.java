/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.FragmentOutPort;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotDirectIONet {

	/**
	 * Serialize net to new format
	 * 
	 * @param net Net to serialize
	 * @return new representation of the net
	 * @throws RPIException
	 */
	public static String toNewFormat(Fragment net) throws RPIException {
		StringBuilder sb = new StringBuilder();
		addFragment(net, sb);
		return sb.toString();
	}

	/**
	 * Serialize fragment to new format
	 * 
	 * @param primitive primitive to serialize
	 * @param prefix    prefix before each line (for indentation)
	 * @return new representation of the fragment
	 */
	private static void addPrimitive(Primitive primitive, StringBuilder sb, Set<Primitive> inline) {
		if (primitive instanceof Fragment) {
			addFragment((Fragment) primitive, sb);
		} else {
			sb.append(primitive.getType());
		}
		sb.append("(");
		boolean needComma = false;
		for (final InPort in : primitive.getInPorts()) {
			final OutPort out = in.getConnectedPort();
			if (out != null) {
				if (needComma) {
					sb.append(",");
				}
				needComma = true;
				sb.append(in.getName());
				if (in.getDebug() > 0) {
					sb.append("[").append(in.getDebug()).append("]");
				}
				sb.append("=");
				addOutPort(out, sb, inline);
			}
		}
		for (final Parameter<?> param : primitive.getParameters()) {
			if (param.getValue() == null) {
				continue;
			}
			if (needComma) {
				sb.append(",");
			}
			needComma = true;
			sb.append(param.getName()).append("='").append(param.getValue()).append("'");
		}
		sb.append(")");
	}

	private static void addFragment(Fragment primitive, StringBuilder sb) {
		sb.append("{");
		boolean needComma = false;

		Set<Primitive> seen = new HashSet<Primitive>();
		Set<Primitive> inline = new HashSet<Primitive>();

		for (final Primitive prim : primitive.getPrimitives()) {
			for (InPort in : prim.getInPorts()) {
				if (in.getConnectedPort() != null) {
					Primitive src = in.getConnectedPort().getPrimitive();
					if (seen.contains(src)) {
						inline.remove(src);
					} else {
						seen.add(src);
						if (src.getType().startsWith("Core::")) {
							inline.add(src);
						}
					}
				}
			}
		}
		for (final OutPort out : primitive.getOutPorts()) {
			if (out instanceof FragmentOutPort) {
				OutPort fout = ((FragmentOutPort) out).getInnerPort();
				if (fout != null) {
					Primitive src = fout.getPrimitive();
					if (seen.contains(src)) {
						inline.remove(src);
					} else {
						seen.add(src);
						if (src.getType().startsWith("Core::")) {
							inline.add(src);
						}
					}
				}
			}
		}
		inline.clear();
		for (final Primitive prim : primitive.getPrimitives()) {
			if (inline.contains(prim)) {
				continue;
			}
			if (needComma) {
				sb.append(",");
			}
			needComma = true;
			sb.append(prim.getName()).append("=");
			addPrimitive(prim, sb, inline);
		}
		for (final OutPort out : primitive.getOutPorts()) {
			if (out instanceof FragmentOutPort) {
				if (needComma) {
					sb.append(",");
				}
				needComma = true;
				final OutPort fout = ((FragmentOutPort) out).getInnerPort();
				sb.append(out.getName()).append("=");
				addOutPort(fout, sb, inline);
			}
		}

		sb.append("}");
	}

	private static void addOutPort(OutPort out, StringBuilder sb, Set<Primitive> inline) {
		if (out.getPrimitive() instanceof Fragment && !out.getPrimitive().getOutPorts().contains(out)) {
			sb.append("parent." + out.getName());
		} else {
			if (inline.contains(out.getPrimitive())) {
				addPrimitive(out.getPrimitive(), sb, inline);
				sb.append(".").append(out.getName());
			} else {
				sb.append(out.getPrimitiveName()).append(".").append(out.getName());
			}
		}
	}
}
