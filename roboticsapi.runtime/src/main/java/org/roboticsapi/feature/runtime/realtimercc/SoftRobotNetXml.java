/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.FragmentOutPort;
import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.UnknownPrimitive;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SoftRobotNetXml {

	/**
	 * Serialize net to XML
	 * 
	 * @param net Net to serialize
	 * @return XML representation of the net
	 * @throws RpiException
	 */
	public static String toXml(Fragment net) throws RpiException {
		StringBuilder sb = new StringBuilder();
		addXml(net, "", "rpinet", sb);
		return sb.toString();
	}

	/**
	 * Serialize fragment to XML
	 * 
	 * @param fragment fragment to serialize
	 * @param prefix   prefix before each line (for indentation)
	 * @return XML representation of the fragment
	 */
	private static void addXml(Fragment fragment, String prefix, String nodeName, StringBuilder sb) {
		sb.append(prefix).append("<").append(nodeName).append(" id=\"").append(fragment.getName()).append("\">\n");

		for (final Primitive primitive : fragment.getPrimitives()) {
			if (primitive instanceof Fragment) {
				addXml((Fragment) primitive, prefix + "\t", "fragment", sb);
			}
		}
		for (final Primitive primitive : fragment.getPrimitives()) {
			if (!(primitive instanceof Fragment)) {
				sb.append(prefix).append("\t<module type=\"").append(primitive.getType()).append("\" id=\"")
						.append(primitive.getName()).append("\">\n");
				for (final Parameter<?> param : primitive.getParameters()) {
					if (param.getValue() == null) {
						continue;
					}
					sb.append(prefix).append("\t\t<parameter name=\"").append(param.getName()).append("\" value=\"")
							.append(param.getValue()).append("\" />\n");
				}
				for (final InPort in : primitive.getInPorts()) {
					final OutPort out = in.getConnectedPort();
					if (out != null) {
						sb.append(prefix).append("\t\t<port name=\"").append(in.getName()).append("\" frommodule=\"")
								.append(out.getPrimitiveName()).append("\" fromport=\"").append(out.getName())
								.append((in.getDebug() > 0 ? "\" debug=\"" + in.getDebug() : "")).append("\" />\n");
					}
				}
				sb.append(prefix).append("\t</module>\n");
			}
		}
		for (final OutPort out : fragment.getOutPorts()) {
			if (out instanceof FragmentOutPort) {
				final OutPort fout = ((FragmentOutPort) out).getInnerPort();
				sb.append(prefix).append("\t<outport name=\"").append(out.getName()).append("\" frommodule=\"")
						.append(fout.getPrimitiveName()).append("\" fromport=\"").append(fout.getName())
						.append("\" />\n");
			}
		}
		for (final InPort in : fragment.getInPorts()) {
			final OutPort out = in.getConnectedPort();
			if (out != null) {
				sb.append(prefix).append("\t<inport name=\"").append(in.getName()).append("\" frommodule=\"")
						.append(out.getPrimitiveName()).append("\" fromport=\"").append(out.getName())
						.append((in.getDebug() > 0 ? "\" debug=\"" + in.getDebug() : "")).append("\" />\n");
			}
		}
		sb.append(prefix).append("</").append(nodeName).append(">\n");
	}

	/**
	 * Deserialize net from XML
	 * 
	 * @param net XML representation of the net
	 * @return deserialized net
	 * @throws MappingException when an error occurs
	 */
	public static Fragment fromXml(String xml) {
		Fragment net = new Fragment();
		net.setName("ROOT");

		Document doc = parse(xml);
		if (doc == null) {
			return null;
		}
		parseFragment(net, doc.getDocumentElement());
		return net;
	}

	private static void parseFragment(Fragment fragment, Node fragmentXml) {
		NodeList children = fragmentXml.getChildNodes();

		// step 1 - create modules and child fragments
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getAttributes() == null || node.getAttributes().getNamedItem("id") == null) {
				continue;
			}

			if (node.getNodeName().toLowerCase().equals("module")) {
				Node id = node.getAttributes().getNamedItem("id");
				Node type = node.getAttributes().getNamedItem("type");
				if (id == null || type == null) {
					continue;
				}
				Primitive primitive = new UnknownPrimitive(type.getNodeValue());
				primitive.setName(id.getNodeValue());
				parsePrimitive(primitive, node);
				fragment.add(primitive);

			} else if (node.getNodeName().toLowerCase().equals("fragment")) {
				Node id = node.getAttributes().getNamedItem("id");
				if (id == null) {
					continue;
				}
				Fragment child = new Fragment();
				child.setName(id.getNodeValue());
				parseFragment(child, node);
				fragment.add(child);

			} else if (node.getNodeName().toLowerCase().equals("inport")) {
				Node name = node.getAttributes().getNamedItem("name");
				if (name == null) {
					continue;
				}
				String portName = name.getNodeValue();
				fragment.addInPort(portName);

			}
		}

		// step 2 - connect ports
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getAttributes() == null || node.getAttributes().getNamedItem("id") == null) {
				continue;
			}

			if (node.getNodeName().toLowerCase().equals("module")) {
				Node id = node.getAttributes().getNamedItem("id");
				if (id == null) {
					continue;
				}
				Primitive module = getPrimitive(fragment, id.getNodeValue());
				NodeList nodeChildren = node.getChildNodes();
				for (int j = 0; j < nodeChildren.getLength(); j++) {
					Node child = nodeChildren.item(i);
					if (child.getNodeName().equals("port")) {
						Node name = child.getAttributes().getNamedItem("name");
						if (name == null) {
							continue;
						}

						InPort port = getPort(module, name.getNodeValue());
						connectPort(fragment, port, child);
					}
				}

			} else if (node.getNodeName().toLowerCase().equals("fragment")) {
				Node id = node.getAttributes().getNamedItem("id");
				if (id == null) {
					continue;
				}
				Primitive module = getPrimitive(fragment, id.getNodeValue());
				NodeList nodeChildren = node.getChildNodes();
				for (int j = 0; j < nodeChildren.getLength(); j++) {
					Node child = nodeChildren.item(i);
					if (child.getNodeName().equals("inport")) {
						Node name = child.getAttributes().getNamedItem("name");
						if (name == null) {
							continue;
						}

						InPort port = getPort(module, name.getNodeValue());
						connectPort(fragment, port, child);
					}
				}

			} else if (node.getNodeName().toLowerCase().equals("outport")) {
				Node name = node.getAttributes().getNamedItem("name");
				if (name == null) {
					continue;
				}

				String portName = name.getNodeValue();
				Node frommoduleNode = node.getAttributes().getNamedItem("frommodule");
				Node fromportNode = node.getAttributes().getNamedItem("fromport");
				if (frommoduleNode == null || fromportNode == null) {
					continue;
				}
				Primitive from = getPrimitive(fragment, frommoduleNode.getNodeValue());

				OutPort theOut = null;
				if (from != null) {
					for (OutPort out : from.getOutPorts()) {
						if (out.getName().equals(fromportNode.getNodeValue())) {
							theOut = out;
						}
					}

					if (theOut == null) {
						if (!(from instanceof UnknownPrimitive)) {
							continue;
						}
						theOut = new OutPort(fromportNode.getNodeValue());
						((UnknownPrimitive) from).add(theOut);
					}
					fragment.provideOutPort(theOut, portName);
				}
			}
		}
	}

	private static InPort getPort(Primitive module, String name) {
		for (InPort in : module.getInPorts()) {
			if (in.getName().equals(name)) {
				return in;
			}
		}
		return null;
	}

	private static NodeList parsePrimitive(Primitive primitive, Node primitiveNode) {
		NodeList nodechildren = primitiveNode.getChildNodes();
		for (int j = 0; j < nodechildren.getLength(); j++) {
			Node child = nodechildren.item(j);
			String nodeName = child.getNodeName().toLowerCase();
			if (nodeName.equals("parameter")) {
				Node nameNode = child.getAttributes().getNamedItem("name");
				if (nameNode == null) {
					continue;
				}
				String name = nameNode.getNodeValue();
				Node valueNode = child.getAttributes().getNamedItem("value");
				if (valueNode == null) {
					continue;
				}
				String value = valueNode.getNodeValue();
				Parameter<?> param = null;
				for (Parameter<?> p : primitive.getParameters()) {
					if (p.getName().equals(name)) {
						param = p;
					}
				}
				if (param == null) {
					if (!(primitive instanceof UnknownPrimitive)) {
						continue;
					}
					param = new Parameter<String>(name, value);
					((UnknownPrimitive) primitive).add(param);
				}
			} else if (nodeName.equals("port")) {
				Node nameNode = child.getAttributes().getNamedItem("name");
				if (nameNode == null) {
					continue;
				}
				String name = nameNode.getNodeValue();
				InPort inPort = null;
				for (InPort in : primitive.getInPorts()) {
					if (in.getName().equals(name)) {
						inPort = in;
					}
				}
				if (inPort == null) {
					if (!(primitive instanceof UnknownPrimitive)) {
						continue;
					}
					inPort = new InPort(name);
					((UnknownPrimitive) primitive).add(inPort);
				}
			}
		}
		return nodechildren;
	}

	private static Primitive getPrimitive(Fragment net, String id) {
		if (net.getName().equals(id)) {
			return net;
		}
		for (Primitive prim : net.getPrimitives()) {
			if (prim.getName().equals(id)) {
				return prim;
			}
		}
		return null;
	}

	private static void connectPort(Fragment net, InPort port, Node portNode) {
		Node frommoduleNode = portNode.getAttributes().getNamedItem("frommodule");
		Node fromportNode = portNode.getAttributes().getNamedItem("fromport");
		if (frommoduleNode == null || fromportNode == null) {
			return;
		}
		Primitive from = getPrimitive(net, frommoduleNode.getNodeValue());

		if (from != null) {
			OutPort theOut = null;
			for (OutPort out : from.getOutPorts()) {
				if (out.getName().equals(fromportNode.getNodeValue())) {
					theOut = out;
				}
			}

			if (theOut == null) {
				if (from instanceof UnknownPrimitive) {
					theOut = new OutPort(fromportNode.getNodeValue());
					((UnknownPrimitive) from).add(theOut);

				} else if (from instanceof Fragment) {
					InPort fragmentIn = getPort(from, fromportNode.getNodeValue());
					if (fragmentIn != null && fragmentIn instanceof FragmentInPort) {
						theOut = ((FragmentInPort) fragmentIn).getInternalOutPort();
					}
				}
			}
			if (theOut == null) {
				return;
			}

			try {
				port.connectTo(theOut);
			} catch (RpiException e) {
			}
		}

		Node debug = portNode.getAttributes().getNamedItem("debug");
		if (debug != null) {
			try {
				port.setDebug(Double.parseDouble(debug.getNodeValue()));
			} catch (NumberFormatException ex) {
			}
		}
	}

	private static Document parse(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			return builder.parse(bais);

		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
