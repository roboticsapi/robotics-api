/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.roboticsapi.runtime.core.javarcc.devices.DeviceRegistry;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericNetcommOut;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.FragmentOutPort;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;
import org.roboticsapi.runtime.rpi.types.Type;

public class JNetCreator {
	private static Map<String, Class<? extends JPrimitive>> primitives = new HashMap<String, Class<? extends JPrimitive>>();

	public void reg(String name, Class<? extends JPrimitive> primitive) {
		primitives.put(name, primitive);
	}

	public JNet createNet(String name, String description, Fragment fragment, DeviceRegistry devices,
			Executor netcommExecutor, Map<String, JNet> previousNets) {
		Map<JInPort<?>, String[]> debug = new HashMap<>();
		JFragment root = convert(fragment, previousNets, debug);
		JNet ret = new JNet(name, description, root, devices, netcommExecutor, debug);
		root.checkParameters();
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JFragment convert(Fragment fragment, Map<String, JNet> previousNets, Map<JInPort<?>, String[]> debug) {
		JFragment ret = new JFragment();
		Map<String, JPrimitive> primMap = new HashMap<String, JPrimitive>();
		for (Primitive prim : fragment.getPrimitives()) {
			final JPrimitive rp;
			if (prim instanceof Fragment) {
				rp = convert((Fragment) prim, previousNets, debug);

			} else {
				String type = prim.getType();
				if (primitives.containsKey(type)) {
					try {
						rp = primitives.get(type).newInstance();
						rp.setName(prim.getName());
					} catch (Exception e) {
						e.printStackTrace();
						throw new IllegalArgumentException("Could not instantiate primitive of type " + type, e);
					}
				} else {
					throw new IllegalArgumentException("Unsupported primitive " + type);
				}

				if (rp instanceof JGenericInterNetcommIn) {
					String net = "", key = "";
					for (Parameter<?> param : prim.getParameters()) {
						if ("RemoteNet".equals(param.getName()))
							net = param.getValue().toString();
						if ("RemoteKey".equals(param.getName()))
							key = param.getValue().toString();
					}
					JNetcommData<Type> netcomm = previousNets.get(net).getNetcomm(key);
					if (netcomm == null)
						throw new IllegalArgumentException(
								"Primitive " + prim.getName() + " references missing key " + key + " in net " + net);
					((JGenericInterNetcommIn) rp).setNetcomm(netcomm);
				} else if (rp instanceof JGenericInterNetcommOut) {
					String net = "", key = "";
					for (Parameter<?> param : prim.getParameters()) {
						if ("RemoteNet".equals(param.getName()))
							net = param.getValue().toString();
						if ("RemoteKey".equals(param.getName()))
							key = param.getValue().toString();
					}
					((JGenericInterNetcommOut) rp).setNetcomm(previousNets.get(net).getNetcomm(key));
				} else {
					for (Parameter<?> param : prim.getParameters()) {
						JParameter parameter = (JParameter) rp.getParameter(param.getName());
						if (parameter == null)
							throw new IllegalArgumentException("Primitive " + prim.getType()
									+ " does not have a parameter named " + param.getName());
						parameter.set(param.getValue());
					}
				}

				if (prim instanceof NetcommPrimitive) {
					NetcommValue toRpi = ((NetcommPrimitive) prim).getNetcommToRPI();
					if (toRpi != null)
						((JGenericNetcommIn<?>) rp).connectNetcommValue(toRpi);

					final NetcommValue fromRpi = ((NetcommPrimitive) prim).getNetcommFromRPI();
					if (fromRpi != null) {
						((JGenericNetcommOut<?>) rp).connectNetcommValue(fromRpi);
					}
				}
			}

			ret.add(rp);
			primMap.put(prim.getName(), rp);
		}

		for (OutPort port : fragment.getOutPorts()) {
			OutPort source = ((FragmentOutPort) port).getInnerPort();
			if (source.getPrimitive() == fragment) {
				ret.forwardPort(source.getName(), port.getName());
			} else {
				ret.providePort(port.getName(), findPort(primMap, source));
			}
		}

		for (Primitive prim : fragment.getPrimitives()) {
			JPrimitive rp = primMap.get(prim.getName());
			for (InPort port : prim.getInPorts()) {
				OutPort connectedPort = port.getConnectedPort();
				if (connectedPort != null) {
					JInPort inPort = (JInPort) rp.getInPort(port.getName());
					if (connectedPort.getPrimitive() == fragment) {
						if (ret.getInPort(connectedPort.getName()) == null) {
							ret.requirePort(connectedPort.getName(), findPort(primMap, port));
						} else {
							inPort.connectTo(((JFragmentInPort) ret.getInPort(connectedPort.getName())).getInnerPort());
						}
					} else {
						if (rp.getInPort(port.getName()) != null)
							inPort.connectTo(findPort(primMap, connectedPort));
					}
					if (port.getDebug() > 0) {
						debug.put(inPort, new String[(int) (port.getDebug() * 40)]);
					}
				}
			}
		}
		return ret;
	}

	private JInPort<?> findPort(Map<String, JPrimitive> primMap, InPort port) {
		return primMap.get(port.getPrimitive().getName()).getInPort(port.getName());
	}

	private JOutPort<?> findPort(Map<String, JPrimitive> primMap, OutPort port) {
		return primMap.get(port.getPrimitive().getName()).getOutPort(port.getName());
	}
}
