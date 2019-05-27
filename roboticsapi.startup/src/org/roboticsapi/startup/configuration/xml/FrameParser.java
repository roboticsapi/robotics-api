/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.xml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.startup.configuration.util.Configuration;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.TeachingInfo;
import org.roboticsapi.world.Transformation;

public class FrameParser {

	/**
	 * Loads relation configurations from a given XML configuration file
	 * 
	 * @param filename file name of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	public static List<Configuration> loadRelationsFrom(String filename) throws RoboticsException {
		return loadRelationsFrom(new File(filename));
	}

	/**
	 * Loads relation configurations from a given XML configuration file
	 * 
	 * @param file file of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	public static List<Configuration> loadRelationsFrom(File file) throws RoboticsException {
		// validate XML
		// FIXME
		// validateXMLFile(file);

		// parse XML
		Document document = parseFile(file);

		// load configurations from parsed XML
		return createRelationConfigurations(document);
	}

	public static List<Configuration> loadRelationsFrom(URI uri) throws RoboticsException {
		Document document = parseUri(uri);

		// load configurations from parsed XML
		return createRelationConfigurations(document);
	}

	/**
	 * Loads TeachingInformation configurations from a given XML configuration file
	 * 
	 * @param filename file name of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	public static List<Configuration> loadTeachingInformationsFrom(String filename) throws RoboticsException {
		return loadTeachingInformationsFrom(new File(filename));
	}

	/**
	 * Loads TeachingInformation configurations from a given XML configuration file
	 * 
	 * @param file file of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	public static List<Configuration> loadTeachingInformationsFrom(File file) throws RoboticsException {
		// validate XML
		// FIXME
		// validateXMLFile(file);

		// parse XML
		Document document = parseFile(file);

		// load configurations from parsed XML
		return createTeachingInformationConfigurations(document);
	}

	public static List<Configuration> loadTeachingInformationsFrom(URI uri) throws RoboticsException {
		Document document = parseUri(uri);

		// load configurations from parsed XML
		return createTeachingInformationConfigurations(document);
	}

	private static Document parseFile(File file) throws RoboticsException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(file);
		} catch (SAXException e) {
			throw new RoboticsException(e);
		} catch (IOException e) {
			throw new RoboticsException(e);
		} catch (ParserConfigurationException e) {
			throw new RoboticsException(e);
		}
	}

	private static Document parseUri(URI uri) throws RoboticsException {
		try {
			// parse XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(uri.toString());
		} catch (SAXException e) {
			throw new RoboticsException(e);
		} catch (IOException e) {
			throw new RoboticsException(e);
		} catch (ParserConfigurationException e) {
			throw new RoboticsException(e);
		}
	}

	private static List<Configuration> createRelationConfigurations(Document configXml) {
		List<Configuration> ret = new Vector<Configuration>();

		// Objects
		NodeList objectNodes = configXml.getElementsByTagName("Relation");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objectNode = (Element) objectNodes.item(i);
			ret.add(handleRelationNode(objectNode));
		}

		return ret;
	}

	private static List<Configuration> createTeachingInformationConfigurations(Document configXml) {
		List<Configuration> ret = new Vector<Configuration>();

		// Objects
		NodeList objectNodes = configXml.getElementsByTagName("TeachingInformation");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objectNode = (Element) objectNodes.item(i);
			ret.add(handleTeachingInformationNode(objectNode));
		}

		return ret;
	}

	private static Configuration handleRelationNode(Element objectNode) {

		String className = objectNode.getAttribute("class");
		Node fromNode = objectNode.getElementsByTagName("from").item(0);
		Node toNode = objectNode.getElementsByTagName("to").item(0);
		Node rotationNode = objectNode.getElementsByTagName("rotation").item(0);
		Node translationNode = objectNode.getElementsByTagName("translation").item(0);

		Map<String, String> properties = new HashMap<String, String>();
		properties.put("from", fromNode.getFirstChild().getNodeValue());
		properties.put("to", toNode.getFirstChild().getNodeValue());
		addParameters(rotationNode, properties);
		addParameters(translationNode, properties);
		fromNode.getFirstChild().getNodeValue();

		return new Configuration("", className, properties);
	}

	private static Configuration handleTeachingInformationNode(Element objectNode) {

		String className = objectNode.getAttribute("class");
		Map<String, String> properties = new HashMap<String, String>();

		addParameters(objectNode, properties);

		NodeList hintParameters = objectNode.getElementsByTagName("hintParameter");
		for (int i = 0; i < hintParameters.getLength(); i++) {
			properties.put("hintParameter" + i, hintParameters.item(i).getTextContent());
		}

		return new Configuration("", className, properties);
	}

	private static void addParameters(Node node, Map<String, String> properties) {

		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();
			properties.put(name, value);
		}

	}

	public static void writeFramesTo(File file, List<Configuration> relations, List<Configuration> teachingInformations)
			throws RoboticsException {
		try {
			Document doc = createDocument(relations, teachingInformations);
			DOMSource src = new DOMSource(doc);
			Result result = new StreamResult(file);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(src, result);
		} catch (TransformerException e) {
			throw new RoboticsException(e);
		}

	}

	public static void writeFramesTo(File file, Frame... frames) throws RoboticsException {
		List<Frame> framesList = new ArrayList<Frame>();

		for (Frame f : frames) {
			framesList.add(f);
		}

		writeFramesTo(file, framesList);
	}

	public static void writeFramesTo(File file, List<Frame> frames) throws RoboticsException {
		List<Configuration> frameConfigs = toConfigurations(frames);

		List<Configuration> tiConfigs = new ArrayList<Configuration>();

		for (Frame f : frames) {
			if (f == null) {
				continue;
			}

			for (List<TeachingInfo> tis : f.getAllTeachingInfos().values()) {

				for (TeachingInfo ti : tis) {
					if (ti != null) {
						tiConfigs.add(toConfiguration(ti, f));
					}
				}
			}
		}

		writeFramesTo(file, frameConfigs, tiConfigs);
	}

	private static Document createDocument(List<Configuration> relations, List<Configuration> teachingInformations)
			throws RoboticsException {
		try {
			Document doc;
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element framesElement = doc.createElement("Frames");
			framesElement.setAttribute("xmlns", "http://schema.isse.de/SoftRobot/Frames.xsd");
			doc.appendChild(framesElement);

			Element relationsElement = doc.createElement("Relations");
			framesElement.appendChild(relationsElement);

			for (Configuration relationConf : relations) {
				Element relation = doc.createElement("Relation");
				relation.setAttribute("class", relationConf.getClassName());

				Element from = doc.createElement("from");
				from.setTextContent(relationConf.getProperties().get("from"));
				relation.appendChild(from);

				Element to = doc.createElement("to");
				to.setTextContent(relationConf.getProperties().get("to"));
				relation.appendChild(to);

				Element transformation = doc.createElement("transformation");
				relation.appendChild(transformation);

				Element rotation = doc.createElement("rotation");
				transformation.appendChild(rotation);
				addRotationAttributes(rotation, relationConf);

				Element translation = doc.createElement("translation");
				transformation.appendChild(translation);
				addTranslationAttributes(translation, relationConf);

				relationsElement.appendChild(relation);
			}

			Element teachingInformationsElement = doc.createElement("TeachingInformations");
			framesElement.appendChild(teachingInformationsElement);

			for (Configuration c : teachingInformations) {
				Element teachingInformationElement = doc.createElement("TeachingInformation");
				teachingInformationElement.setAttribute("class", c.getClassName());
				teachingInformationElement.setAttribute("device", c.getProperties().get("device"));
				teachingInformationElement.setAttribute("motionCenter", c.getProperties().get("motionCenter"));
				teachingInformationElement.setAttribute("frame", c.getProperties().get("frame"));

				for (int i = 0;; i++) {
					String currHint = c.getProperties().get("hintParameter" + i);

					if (currHint == null) {
						break;
					}

					Element hintParameterElement = doc.createElement("hintParameter");
					hintParameterElement.setTextContent(currHint);
					teachingInformationElement.appendChild(hintParameterElement);
				}
				teachingInformationsElement.appendChild(teachingInformationElement);
			}

			return doc;
		} catch (ParserConfigurationException e) {
			throw new RoboticsException(e);
		}
	}

	private static void addRotationAttributes(Element rotation, Configuration relation) {
		rotation.setAttribute("a", relation.getProperties().get("a"));
		rotation.setAttribute("b", relation.getProperties().get("b"));
		rotation.setAttribute("c", relation.getProperties().get("c"));

	}

	private static void addTranslationAttributes(Element translation, Configuration relation) {
		translation.setAttribute("x", relation.getProperties().get("x"));
		translation.setAttribute("y", relation.getProperties().get("y"));
		translation.setAttribute("z", relation.getProperties().get("z"));

	}

	public static List<Configuration> toConfigurations(Collection<Relation> relations) throws RoboticsException {
		List<Configuration> result = new ArrayList<Configuration>();

		for (Relation r : relations) {
			result.add(toConfiguration(r));
		}

		return result;
	}

	public static List<Configuration> toConfigurations(List<Frame> frames) throws RoboticsException {
		List<Configuration> result = new ArrayList<Configuration>();

		if (frames == null || frames.size() == 0) {
			return result;
		}

		// FIXME: implement (why does this need root frames for???)
		// List<Frame> rootFrames = FrameRegistry.getRootFrames();
		// if (rootFrames == null || rootFrames.size() == 0) {
		// throw new RoboticsException("No root frames");
		// }
		//
		// Set<Relation> relations = new HashSet<Relation>();
		//
		// List<Frame> todo = new ArrayList<Frame>();
		// for (Frame f : frames) {
		// if (f != null) {
		// todo.add(f);
		// }
		// }
		//
		// int rootIndex = 0;
		// do {
		// Frame currentRoot = rootFrames.get(rootIndex);
		// for (Frame f : frames) {
		// if (f == null) {
		// continue;
		// }
		//
		// List<Relation> rel = currentRoot.getRelationsTo(f);
		//
		// if (rel == null) {
		// continue;
		// }
		//
		// relations.addAll(rel);
		//
		// todo.remove(f);
		// }
		//
		// rootIndex++;
		//
		// } while (todo.size() > 0 && rootIndex < rootFrames.size());
		//
		// if (todo.size() > 0) {
		// String s = "";
		// for (Frame f : todo) {
		// s += f.getName() + ",";
		// }
		// s = s.substring(0, s.length() - 2);
		// throw new RoboticsException("Could not save the following frames: "
		// + s);
		// }
		//
		// for (Relation r : relations) {
		// result.add(toConfiguration(r));
		// }

		return result;
	}

	public static List<Configuration> toConfigurations(Frame... frames) throws RoboticsException {
		List<Frame> framesList = new ArrayList<Frame>();

		for (Frame f : frames) {
			framesList.add(f);
		}

		return toConfigurations(framesList);
	}

	public static Configuration toConfiguration(Relation relation) throws RoboticsException {
		Map<String, String> properties = new HashMap<String, String>();

		properties.put("from", relation.getFrom().getName());
		properties.put("to", relation.getTo().getName());

		Transformation t = relation.getTransformation();

		properties.put("x", t.getTranslation().getX() + "");
		properties.put("y", t.getTranslation().getY() + "");
		properties.put("z", t.getTranslation().getZ() + "");

		properties.put("a", t.getRotation().getA() + "");
		properties.put("b", t.getRotation().getB() + "");
		properties.put("c", t.getRotation().getC() + "");

		Configuration c = new Configuration("", relation.getClass().getName(), properties);

		return c;
	}

	public static Configuration toConfiguration(TeachingInfo info, Frame f) {
		Map<String, String> properties = new HashMap<String, String>();

		properties.put("frame", f.getName());
		properties.put("device", info.getDevice().getName());
		properties.put("motionCenter", info.getMotionCenter().getName());

		double[] hintParameters = info.getHintParameters();

		for (int i = 0; i < hintParameters.length; i++) {
			properties.put("hintParameter" + i, Double.toString(hintParameters[i]));
		}

		Configuration c = new Configuration("", "dummyclass", properties);

		return c;
	}

	public static List<TeachingInfoWithFrame> toTeachingInfos(Collection<Configuration> configs) {
		List<TeachingInfoWithFrame> result = new ArrayList<TeachingInfoWithFrame>();
		for (Configuration c : configs) {
			result.add(toTeachingInfo(c));
		}
		return result;
	}

	public static TeachingInfoWithFrame toTeachingInfo(Configuration config) {
		String frameName = config.getProperties().get("frame");

		String motionCenterName = config.getProperties().get("motionCenter");

		String deviceName = config.getProperties().get("device");

		List<Double> hintParameters = new ArrayList<Double>();
		for (int i = 0;; i++) {
			String hintParameter = config.getProperties().get("hintParameter" + i);

			if (hintParameter == null) {
				break;
			}

			hintParameters.add(Double.parseDouble(hintParameter));
		}
		Double[] hintParametersArray = hintParameters.toArray(new Double[hintParameters.size()]);

		double[] hintParametersPrimitiveArray = new double[hintParametersArray.length];

		for (int i = 0; i < hintParametersPrimitiveArray.length; i++) {
			hintParametersPrimitiveArray[i] = hintParametersArray[i];
		}

		return new TeachingInfoWithFrame(deviceName, motionCenterName, hintParametersPrimitiveArray, frameName);
	}

	public static List<RelationData> toRelations(Collection<Configuration> configs) {
		List<RelationData> result = new ArrayList<RelationData>();
		for (Configuration c : configs) {
			result.add(toRelation(c));
		}
		return result;
	}

	public static RelationData toRelation(Configuration config) {
		String fromName = config.getProperties().get("from");
		String toName = config.getProperties().get("to");

		double a = Double.parseDouble(config.getProperties().get("a"));
		double b = Double.parseDouble(config.getProperties().get("b"));
		double c = Double.parseDouble(config.getProperties().get("c"));
		double x = Double.parseDouble(config.getProperties().get("x"));
		double y = Double.parseDouble(config.getProperties().get("y"));
		double z = Double.parseDouble(config.getProperties().get("z"));

		Transformation transformation = new Transformation(x, y, z, a, b, c);

		return new RelationData(fromName, toName, transformation, config.getClassName());
	}
}
