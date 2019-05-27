/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.startup.configuration.util.Configuration;

public class EntityParser {

	/**
	 * Loads configurations from a given XML configuration file
	 * 
	 * @param filename file name of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	public static List<Configuration> loadConfigurationsFrom(String filename) throws RoboticsException {
		return loadConfigurationsFrom(new File(filename));
	}

	/**
	 * Loads configurations from a given XML configuration file
	 * 
	 * @param file file of the given XML configuration file
	 * @throws RoboticsException if loading the XML file fails
	 */
	public static List<Configuration> loadConfigurationsFrom(File file) throws RoboticsException {
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
	private static List<Configuration> createConfigurations(Document configXml) {
		List<Configuration> ret = new Vector<Configuration>();

		// Entities
		NodeList entityNodes = configXml.getElementsByTagName("Entity");
		for (int i = 0; i < entityNodes.getLength(); i++) {
			Element entityNode = (Element) entityNodes.item(i);
			ret.addAll(handleEntityNode(entityNode));
		}

		return ret;
	}

	/**
	 * Creates a list of configurations for an {@link Entity}.
	 * 
	 * @param entityNode The XML element representing the entity.
	 */
	private static List<Configuration> handleEntityNode(Element entityNode) {
		List<Configuration> ret = new ArrayList<Configuration>();
		String deviceName = entityNode.getAttribute("device");

		// Entity.Properties
		NodeList propertiesList = entityNode.getElementsByTagName("Property");
		for (int j = 0; j < propertiesList.getLength(); j++) {
			Element propertyNode = (Element) propertiesList.item(j);
			ret.add(handlePropertyNode(propertyNode, deviceName));
		}

		return ret;
	}

	/**
	 * Creates a list of configurations for a {@link Property}.
	 * 
	 * @param propertyNode The XML element representing the property.
	 * @param deviceName   The device, the properties entity is associated to.
	 * @return A configuration for the given property.
	 */
	private static Configuration handlePropertyNode(Element propertyNode, String deviceName) {
		String className = propertyNode.getAttribute("class");
		Map<String, String> properties = getParameters(propertyNode);
		// add associated device
		properties.put("device", deviceName);
		return new Configuration("Property", className, properties);
	}

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

	private static boolean validateXMLFile(File file) {
		return validateXMLFile(new StreamSource(file));
	}

	private static boolean validateXMLFile(Source source) {
		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
			Schema schema = factory.newSchema(EntityParser.class.getResource("/Entities.xsd"));
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

}