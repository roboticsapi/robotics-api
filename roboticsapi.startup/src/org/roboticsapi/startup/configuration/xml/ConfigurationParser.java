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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.startup.configuration.util.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Deprecated
public class ConfigurationParser {

	/**
	 * Loads configurations from a given XML configuration file
	 *
	 * @param filename file name of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 * @throws IOException       if the File with the specified name could not be
	 *                           accessed
	 */
	@Deprecated
	public static List<Configuration> loadConfigurationsFrom(String filename) throws RoboticsException, IOException {
		return loadConfigurationsFrom(new File(filename));
	}

	/**
	 * Loads configurations from a given XML configuration file
	 *
	 * @param file file of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 * @throws IOException       if the specified File could not be accessed
	 */
	@Deprecated
	public static List<Configuration> loadConfigurationsFrom(File file) throws RoboticsException, IOException {
		// validate XML
		validateXMLFile(file);

		// parse XML
		Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file);
		} catch (SAXException e) {
			throw new RoboticsException(e);
		} catch (ParserConfigurationException e) {
			throw new RoboticsException(e);
		}
		// load configurations from parsed XML
		return createConfigurations(document);
	}

	@Deprecated
	public static List<Configuration> loadConfigurationsFrom(URI uri) throws RoboticsException {
		Document document;

		try {
			// parse XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(uri.toString());

			// validate XML
			// validateDocument(document);
		} catch (SAXException e) {
			throw new RoboticsException(e);
		} catch (IOException e) {
			throw new RoboticsException(e);
		} catch (ParserConfigurationException e) {
			throw new RoboticsException(e);
		}
		// load configurations from parsed XML
		return createConfigurations(document);
	}

	/**
	 * Loads configurations from a given XML configuration file
	 *
	 * @param stream input stream of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	@Deprecated
	private static List<Configuration> createConfigurations(Document configXml) {
		List<Configuration> ret = new Vector<Configuration>();

		// Objects
		NodeList objectNodes = configXml.getElementsByTagName("Object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objectNode = (Element) objectNodes.item(i);
			ret.add(handleObjectNode(objectNode));
		}

		// Runtimes
		NodeList runtimesNdList = configXml.getElementsByTagName("Runtime");
		for (int i = 0; i < runtimesNdList.getLength(); i++) {
			Element runtimeNode = (Element) runtimesNdList.item(i);
			ret.addAll(handleRuntimeNode(runtimeNode));
		}
		return ret;
	}

	@Deprecated
	private static List<Configuration> handleRuntimeNode(Element runtimeNode) {
		List<Configuration> ret = new ArrayList<Configuration>();

		String name = runtimeNode.getAttribute("name");
		String className = runtimeNode.getAttribute("class");
		Element settingsNode = (Element) runtimeNode.getElementsByTagName("Settings").item(0);
		Map<String, String> properties = getParameters(settingsNode);
		ret.add(new Configuration(name, className, properties));

		// devices
		NodeList devicesNdList = runtimeNode.getElementsByTagName("Device");
		for (int j = 0; j < devicesNdList.getLength(); j++) {
			Element deviceNode = (Element) devicesNdList.item(j);
			ret.add(handleDeviceNode(deviceNode, name));
		}

		// sensors
		NodeList sensorNdList = runtimeNode.getElementsByTagName("Sensor");
		for (int j = 0; j < sensorNdList.getLength(); j++) {
			Element sensorNode = (Element) sensorNdList.item(j);
			ret.add(handleDeviceNode(sensorNode, name));
		}
		return ret;
	}

	@Deprecated
	private static Configuration handleDeviceNode(Element deviceNode, String runtimeName) {
		String name = deviceNode.getAttribute("name");
		String className = deviceNode.getAttribute("class");
		Map<String, String> properties = getParameters(deviceNode);
		// add runtime...
		properties.put("runtime", runtimeName);
		return new Configuration(name, className, properties);
	}

	@Deprecated
	private static Configuration handleObjectNode(Element objectNode) {
		String name = objectNode.getAttribute("name");
		String className = objectNode.getAttribute("class");
		Map<String, String> properties = getParameters(objectNode);
		return new Configuration(name, className, properties);
	}

	@Deprecated
	private static Map<String, String> getParameters(Element parentNode) {
		Map<String, String> properties = new HashMap<String, String>();

		NodeList parameters = parentNode.getElementsByTagName("Parameter");
		for (int i = 0; i < parameters.getLength(); i++) {
			Element parameter = (Element) parameters.item(i);
			properties.put(parameter.getAttribute("name"),
					parameter.getFirstChild() != null ? parameter.getFirstChild().getNodeValue() : "");
		}
		return properties;
	}

	@Deprecated
	private static boolean validateXMLFile(File file) {
		return validateXMLFile(new StreamSource(file));
	}

	@Deprecated
	private static boolean validateXMLFile(Source source) {
		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
			Schema schema = factory.newSchema(ConfigurationParser.class.getResource("/RobotApplication.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(source);
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	// private static void validateDocument(Document d) throws RoboticsException
	// {
	// validateDocument(new DOMSource(d));
	// }
	//
	// private static void validateDocument(Source source)
	// throws RoboticsException {
	// try {
	// SchemaFactory factory = SchemaFactory
	// .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	// Schema schema = factory.newSchema(ConfigurationParser.class
	// .getResource("/RobotApplication.xsd"));
	// Validator validator = schema.newValidator();
	// validator.validate(source);
	// } catch (SAXException e) {
	// throw new RoboticsException(e);
	// } catch (IOException e) {
	// throw new RoboticsException(e);
	// } catch (IllegalArgumentException e) {
	// throw new RoboticsException(e);
	// }
	// }

	@Deprecated
	public static void writeConfigurationTo(File file, Map<Configuration, Vector<Configuration>> configurations,
			List<Configuration> configsWithoutRuntime) throws RoboticsException {
		try {
			Document doc = createDocument(configurations, configsWithoutRuntime);
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

	@Deprecated
	private static Document createDocument(Map<Configuration, Vector<Configuration>> configurations,
			List<Configuration> configsWithoutRuntime) throws RoboticsException {
		try {
			Document doc;
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element application = doc.createElement("RobotApplication");
			application.setAttribute("xmlns", "http://schema.isse.de/SoftRobot/RobotApplication.xsd");
			doc.appendChild(application);
			Element runtimes = doc.createElement("Runtimes");
			application.appendChild(runtimes);

			for (Configuration runtimeConf : configurations.keySet()) {
				Element runtime = doc.createElement("Runtime");
				runtime.setAttribute("class", runtimeConf.getClassName());
				runtime.setAttribute("name", runtimeConf.getName());

				Element settings = doc.createElement("Settings");
				runtime.appendChild(settings);
				addParameters(runtimeConf.getProperties(), settings, doc);

				Element devices = doc.createElement("Devices");
				runtime.appendChild(devices);

				addDevices(configurations.get(runtimeConf), devices, doc);

				runtimes.appendChild(runtime);
			}

			Element roboticsObjects = doc.createElement("RoboticsObjects");
			application.appendChild(roboticsObjects);
			for (Configuration roconfig : configsWithoutRuntime) {
				addRoboticsObject(roconfig, roboticsObjects, doc);
			}

			return doc;
		} catch (ParserConfigurationException e) {
			throw new RoboticsException(e);
		}
	}

	@Deprecated
	private static void addRoboticsObject(Configuration roconfig, Element roboticsObjects, Document doc) {
		Element roboticsObject = doc.createElement("Object");
		roboticsObject.setAttribute("class", roconfig.getClassName());
		roboticsObject.setAttribute("name", roconfig.getName());
		roboticsObjects.appendChild(roboticsObject);

	}

	@Deprecated
	private static void addDevices(Vector<Configuration> configs, Element parent, Document doc) {
		for (Configuration configuration : configs) {
			Element device = doc.createElement("Device");
			device.setAttribute("class", configuration.getClassName());
			device.setAttribute("name", configuration.getName());
			parent.appendChild(device);

			addParameters(configuration.getProperties(), device, doc);
		}
	}

	@Deprecated
	private static void addParameters(Map<String, String> properties, Element parent, Document doc) {
		for (Entry<String, String> entry : properties.entrySet()) {
			Element param = doc.createElement("Parameter");
			param.setAttribute("name", entry.getKey());
			param.setTextContent(entry.getValue());
			parent.appendChild(param);
		}
	}

}