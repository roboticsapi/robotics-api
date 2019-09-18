/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;

public class NetParser {

	/**
	 * Parse compact text format into fragment
	 *
	 * @param net fragment text representation
	 * @return fragment
	 * @throws RpiException if parsing fails.
	 */
	public static Fragment parse(String net) throws RpiException {
		PushbackInputStream reader = new PushbackInputStream(new ByteArrayInputStream(net.getBytes()));
		Fragment ret = parseFragment(reader, null);
		return ret;
	}

	private static List<Runnable> configurePrimitive(PushbackInputStream bytes, Primitive primitive, Fragment context)
			throws RpiException {
		List<Runnable> ret = new ArrayList<>();
		expect(bytes, '(');
		while (true) {
			String name = readUntil(bytes, '=', '[', ')');
			double debug = 0;
			if (next(bytes) == '[') {
				expect(bytes, '[');
				debug = Double.parseDouble(readUntil(bytes, ']'));
				expect(bytes, ']');
			}
			double fdebug = debug;
			if (next(bytes) == '=') {
				expect(bytes, '=');
				String type = readUntil(bytes, '\'', '.', '(', '{');
				Primitive prim = null;
				if (next(bytes) == '.') {
					if (type.equals("parent")) {
						prim = context;
					}
				} else if (next(bytes) == '(') {
					prim = new Primitive(type);
					ret.addAll(configurePrimitive(bytes, prim, context));
					context.add(prim);
				} else if (next(bytes) == '{') {
					prim = parseFragment(bytes, context);
					context.add(prim);
					if (next(bytes) == '(') {
						ret.addAll(configurePrimitive(bytes, prim, context));
					}
				}
				Primitive fprim = prim;
				if (next(bytes) == '.') {
					expect(bytes, '.');
					String outPort = readUntil(bytes, ',', ')');
					ret.add(() -> {
						try {
							Primitive lprim = fprim;
							if (lprim == null) {
								for (Primitive p : context.getPrimitives()) {
									if (p.getName().equals(type)) {
										lprim = p;
									}
								}
							}
							OutPort out = getOrCreateOutPort(lprim, outPort);
							InPort in = null;
							if (primitive instanceof Fragment) {
								in = ((Fragment) primitive).addInPort(name);
							} else {
								primitive.add(in = new InPort(name));
							}
							in.setDebug(fdebug);
							in.connectTo(out);
						} catch (RpiException e) {
						}
					});
				} else if (next(bytes) == '\'') {
					expect(bytes, '\'');
					String value = readUntil(bytes, '\'');
					primitive.add(new Parameter<RPIstring>(name, new RPIstring(value)));
					expect(bytes, '\'');
				}
			}
			if (next(bytes) == ')') {
				expect(bytes, ')');
				return ret;
			} else {
				expect(bytes, ',');
			}
		}
	}

	private static Fragment parseFragment(PushbackInputStream bytes, Fragment context) throws RpiException {
		expect(bytes, '{');
		List<Runnable> todos = new ArrayList<>();
		Fragment ret = new Fragment();
		while (true) {
			if (next(bytes) == '}') {
				expect(bytes, '}');

				for (Runnable todo : todos) {
					todo.run();
				}

				return ret;
			}
			String name = readUntil(bytes, '=');
			expect(bytes, '=');
			String type = readUntil(bytes, '.', '(', '{');
			Primitive prim = null;
			if (next(bytes) == '.') {
				for (Primitive p : ret.getPrimitives()) {
					if (p.getName().equals(type)) {
						prim = p;
					}
				}
				if (type.equals("parent"))
					prim = ret;
			} else if (next(bytes) == '(') {
				prim = new Primitive(type);
				prim.setName(name);
				todos.addAll(configurePrimitive(bytes, prim, ret));
				ret.add(prim);
			} else if (next(bytes) == '{') {
				prim = parseFragment(bytes, ret);
				prim.setName(name);
				ret.add(prim);
				if (next(bytes) == '(') {
					todos.addAll(configurePrimitive(bytes, prim, ret));
				}
			}
			if (next(bytes) == '.') {
				expect(bytes, '.');
				String portName = readUntil(bytes, ',', '}');
				OutPort out;
				if (prim == ret) {
					out = ret.addInPort(portName).getInternalOutPort();
				} else {
					out = getOrCreateOutPort(prim, portName);
				}
				ret.provideOutPort(out, name);
			}

			if (next(bytes) == ',') {
				expect(bytes, ',');
			}
		}
	}

	private static OutPort getOrCreateOutPort(Primitive prim, String outPort) {
		for (OutPort o : prim.getOutPorts()) {
			if (o.getName().equals(outPort)) {
				return o;
			}
		}
		OutPort port;
		if (prim instanceof Fragment) {
			port = ((Fragment) prim).addInPort(outPort).getInternalOutPort();
		} else {
			prim.add(port = new OutPort(outPort));
		}
		return port;
	}

	private static int next(PushbackInputStream bytes) throws RpiException {
		try {
			int read = bytes.read();
			bytes.unread(read);
			return read;
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	private static String readUntil(PushbackInputStream bytes, char... chars) throws RpiException {
		try {
			StringBuffer ret = new StringBuffer();
			while (true) {
				int read = bytes.read();
				for (char c : chars) {
					if (c == read) {
						bytes.unread(read);
						return ret.toString();
					}
				}
				ret.append((char) read);
			}
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	private static void expect(PushbackInputStream bytes, char expect) throws RpiException {
		try {
			int read = bytes.read();
			if (read != expect) {
				throw new IOException("Expected '" + expect + "', received '" + (char) read + "'");
			}
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

}
