/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.feature.startup.configuration.util.ConfigurationHook;
import org.roboticsapi.feature.startup.configuration.util.ConfigurationHookAdapter;
import org.roboticsapi.feature.startup.configuration.util.ConfigurationUtils;
import org.roboticsapi.feature.startup.configuration.util.IllegalConfigurationException;
import org.roboticsapi.feature.startup.configuration.util.RoboticsObjectFactory;
import org.roboticsapi.feature.startup.configuration.util.RoboticsObjectFactory.Callback;
import org.roboticsapi.feature.startup.configuration.xml.ApplicationConfigParser.ConfigEntry.ListenerAdapter;
import org.roboticsapi.feature.startup.configuration.xml.ApplicationConfigParser.ConfigEntry.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ApplicationConfigParser {

	public static class ConfigEntry {

		enum Status {
			INITIAL, // initial
			BUILDING, BUILT, BUILD_FAILED, // building
			CONFIGURING, WAITING_FOR_DEPENDENCIES, CONFIGURE_FAILED, // configuring
			INITIALIZING, INITIALIZED, INITIALIZE_FAILED // initializing
		}

		interface Listener {

			void onDependenciesResolved();

			void onFailed(Status status, ConfigurationException error);

			void onBuilt(RoboticsObject object);

			void onInitialized(RoboticsObject object);

		}

		static class ListenerAdapter implements Listener {

			@Override
			public void onDependenciesResolved() {
			}

			@Override
			public void onFailed(Status status, ConfigurationException error) {
			}

			@Override
			public void onBuilt(RoboticsObject object) {
			}

			@Override
			public void onInitialized(RoboticsObject object) {
			}

		}

		String parentName;
		String id;
		String type;

		Map<String, PropEntry> properties = new HashMap<String, ApplicationConfigParser.PropEntry>();

		List<Listener> listeners = new ArrayList<Listener>();
		Status status = Status.INITIAL;
		RoboticsObject builtObject = null;
		ConfigurationException error = null;
		List<ConfigEntry> dependencies = new ArrayList<ConfigEntry>();

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
			if (status == Status.BUILT) {
				for (Listener listener : new ArrayList<Listener>(listeners)) {
					listener.onBuilt(getBuiltObject());
				}
			} else if (status == Status.INITIALIZED) {
				for (Listener listener : new ArrayList<Listener>(listeners)) {
					listener.onInitialized(getBuiltObject());
				}
			} else if (status == Status.WAITING_FOR_DEPENDENCIES && dependencies.isEmpty()) {
				for (Listener listener : new ArrayList<Listener>(listeners)) {
					listener.onDependenciesResolved();
				}
			}

		}

		public void setBuiltObject(RoboticsObject builtObject) {
			this.builtObject = builtObject;
		}

		public void setError(Status status, ConfigurationException error) {
			this.status = status;
			this.error = error;
			for (Listener listener : new ArrayList<Listener>(listeners)) {
				listener.onFailed(status, error);
			}
		}

		public RoboticsObject getBuiltObject() {
			return builtObject;
		}

		public void addListener(Listener listener) {
			listeners.add(listener);
			if (builtObject != null) {
				listener.onBuilt(builtObject);
			}
			if (status == Status.INITIALIZED) {
				listener.onInitialized(builtObject);
			}
			if (error != null) {
				listener.onFailed(status, error);
			}
			if (status == Status.WAITING_FOR_DEPENDENCIES && dependencies.isEmpty()) {
				listener.onDependenciesResolved();
			}
		}

		public void removeListener(Listener listener) {
			listeners.remove(listener);
		}

		public ConfigEntry(String parentName, String id, String type) {
			this.parentName = parentName;
			this.id = id;
			this.type = type;
		}

		public String getName() {
			return parentName + (id.isEmpty() ? "" : "[" + id + "]");
		}

		public String getType() {
			return type;
		}

		public String getId() {
			return id;
		}

		public void setProperty(String name, PropEntry entry) {
			properties.put(name, entry);
		}

		public PropEntry getProperty(String name) {
			return properties.get(name);
		}

		public Map<String, PropEntry> getProperties() {
			return properties;
		}

		public List<ConfigEntry> getChildren() {
			List<ConfigEntry> ret = new ArrayList<ApplicationConfigParser.ConfigEntry>();
			for (PropEntry prop : properties.values()) {
				if (prop.getChild() != null) {
					ret.add(prop.getChild());
				}
			}
			return ret;
		}

		protected void addDependency(ConfigEntry dependency) {
			dependencies.add(dependency);
		}

		protected void removeDependency(ConfigEntry dependency) {
			dependencies.remove(dependency);
			if (status == Status.WAITING_FOR_DEPENDENCIES && dependencies.isEmpty()) {
				for (Listener listener : new ArrayList<Listener>(listeners)) {
					listener.onDependenciesResolved();
				}
			}
		}

		@Override
		public String toString() {
			return getName();
		}

	}

	static class PropEntry {
		ConfigEntry getChild() {
			return null;
		}
	}

	static class RefPropEntry extends PropEntry {
		String idref;

		public RefPropEntry(String ref) {
			this.idref = ref;
		}

		public String getRef() {
			return idref;
		}
	}

	static class RefParameterPropEntry extends RefPropEntry {

		private final String property;

		public RefParameterPropEntry(String ref, String property) {
			super(ref);
			this.property = property;
		}

		public String getProperty() {
			return property;
		}

	}

	static class ValuePropEntry extends PropEntry {
		String value;

		public ValuePropEntry(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	static class ChildPropEntry extends PropEntry {
		ConfigEntry child;

		public ChildPropEntry(ConfigEntry child) {
			this.child = child;
		}

		@Override
		public ConfigEntry getChild() {
			return child;
		}
	}

	static class ChildParameterPropEntry extends ChildPropEntry {

		private final String property;

		public ChildParameterPropEntry(String property, ConfigEntry child) {
			super(child);
			this.property = property;
		}

		public String getProperty() {
			return property;
		}
	}

	private ApplicationConfigParser() {
	}

	public static List<RoboticsObject> build(File file, RoboticsObjectFactory factory, RoboticsContext context,
			ConfigurationHook hook, Map<String, RoboticsObject> knownObjects) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(file);

			return build(document, factory, context, hook, knownObjects, file);
		} catch (SAXException e) {
			hook.onInputReadingFailed("Cannot parse input file", e);
		} catch (ParserConfigurationException e) {
			hook.onInputReadingFailed("Cannot parse input file", e);
		} catch (IOException e) {
			hook.onInputReadingFailed("Cannot read input file", e);
		} catch (IllegalConfigurationException e) {
			hook.onInputReadingFailed("Error reading input file", e);
		}
		return new ArrayList<RoboticsObject>();
	}

	public static List<RoboticsObject> build(InputStream stream, RoboticsObjectFactory factory, RoboticsContext context,
			ConfigurationHook hook) {
		return build(stream, factory, context, hook, new HashMap<String, RoboticsObject>());
	}

	public static List<RoboticsObject> build(InputStream stream, RoboticsObjectFactory factory, RoboticsContext context,
			ConfigurationHook hook, Map<String, RoboticsObject> knownObjects) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(stream);

			return build(document, factory, context, hook, knownObjects, new File("inputstream"));
		} catch (SAXException e) {
			hook.onInputReadingFailed("Cannot parse input file", e);
		} catch (ParserConfigurationException e) {
			hook.onInputReadingFailed("Cannot parse input file", e);
		} catch (IOException e) {
			hook.onInputReadingFailed("Cannot read input file", e);
		} catch (IllegalConfigurationException e) {
			hook.onInputReadingFailed("Error reading input file", e);
		}
		return new ArrayList<RoboticsObject>();
	}

	private static List<RoboticsObject> build(Document document, RoboticsObjectFactory factory, RoboticsContext context,
			ConfigurationHook hook, Map<String, RoboticsObject> knownObjects, File reference)
			throws IllegalConfigurationException {
		if (hook == null) {
			hook = new ConfigurationHookAdapter();
		}
		List<ConfigEntry> entries = parseDocument(reference, document, hook);
		return buildObjects(entries, factory, context, hook, knownObjects);
	}

	public static List<RoboticsObject> build(List<ConfigEntry> objects, RoboticsObjectFactory factory,
			RoboticsContext context, Map<String, RoboticsObject> knownObjects) {
		return build(objects, factory, context, new ConfigurationHookAdapter(), knownObjects);
	}

	public static List<RoboticsObject> build(List<ConfigEntry> objects, RoboticsObjectFactory factory,
			RoboticsContext context, ConfigurationHook hook, Map<String, RoboticsObject> knownObjects) {
		if (hook == null) {
			hook = new ConfigurationHookAdapter();
		}
		return buildObjects(objects, factory, context, hook, knownObjects);
	}

	public static List<ConfigEntry> parseFile(File file) throws IllegalConfigurationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			return parseDocument(file, document, new ConfigurationHookAdapter());
		} catch (SAXException e) {
			throw new IllegalConfigurationException("", e);
		} catch (ParserConfigurationException e) {
			throw new IllegalConfigurationException("", e);
		} catch (IOException e) {
			throw new IllegalConfigurationException("", e);
		}
	}

	private static List<RoboticsObject> buildObjects(List<ConfigEntry> entries, RoboticsObjectFactory factory,
			RoboticsContext context, ConfigurationHook hook, Map<String, RoboticsObject> knownObjects) {
		if (entries == null) {
			return null;
		}

		// collect named objects
		Map<String, ConfigEntry> namedEntries = new HashMap<String, ConfigEntry>();
		collectNamedEntries(entries, namedEntries);

		// build entries (triggerign configure & initialize
		for (ConfigEntry entry : entries) {
			buildEntry(entry, factory, context, hook, namedEntries, knownObjects);
		}

		failNotBuiltObjects(entries);

		List<RoboticsObject> ret = new ArrayList<RoboticsObject>();
		collectInitializedEntries(entries, ret);
		return ret;
	}

	private static void failNotBuiltObjects(List<ConfigEntry> entries) {
		for (ConfigEntry entry : entries) {
			if (entry.getBuiltObject() == null) {
				entry.setError(Status.BUILD_FAILED, new ConfigurationException(null, "type",
						"No builder found for object of type " + entry.getType()));
			}
			failNotBuiltObjects(entry.getChildren());
		}

	}

	private static void collectInitializedEntries(List<ConfigEntry> entries, List<RoboticsObject> ret) {
		for (ConfigEntry entry : entries) {
			if (entry.getStatus() == Status.INITIALIZED) {
				ret.add(entry.getBuiltObject());
			}
			collectInitializedEntries(entry.getChildren(), ret);
		}
	}

	private static void collectNamedEntries(List<ConfigEntry> objects, Map<String, ConfigEntry> namedObjects) {
		for (ConfigEntry entry : objects) {
			if (!entry.getId().isEmpty()) {
				namedObjects.put(entry.getId(), entry);
			}
			collectNamedEntries(entry.getChildren(), namedObjects);
		}
	}

	private static void initializeEntry(ConfigEntry entry, RoboticsContext context) {
		entry.setStatus(Status.INITIALIZING);
		RoboticsObject object = entry.getBuiltObject();
		try {
			context.initialize(object);
			entry.setStatus(Status.INITIALIZED);
		} catch (ConfigurationException e) {
			entry.setError(Status.INITIALIZE_FAILED, e);
		} catch (InitializationException e) {
			entry.setError(Status.INITIALIZE_FAILED,
					new ConfigurationException(object, "", "Failed to initialize object: " + e.getMessage(), e));
		}
	}

	private static void configureEntry(final ConfigEntry entry, final RoboticsContext context,
			final Map<String, ConfigEntry> namedEntries, final Map<String, RoboticsObject> knownObjects) {
		entry.setStatus(Status.CONFIGURING);
		final RoboticsObject object = entry.getBuiltObject();

		for (Entry<String, PropEntry> prop : entry.getProperties().entrySet()) {
			final String key = prop.getKey();
			PropEntry propValue = prop.getValue();

			if (propValue instanceof ValuePropEntry) {
				String value = ((ValuePropEntry) propValue).getValue();

				try {
					ConfigurationUtils.setPropertyFromString(object, key, value);
				} catch (IllegalConfigurationException e) {
					entry.setError(Status.CONFIGURE_FAILED,
							new ConfigurationException(object, key, "Could not set value '" + value + "'", e));
				}
			} else if (propValue instanceof RefPropEntry) {
				final String ref = ((RefPropEntry) propValue).getRef();

				String parameter = null;
				if (propValue instanceof RefParameterPropEntry) {
					parameter = ((RefParameterPropEntry) propValue).getProperty();
				}

				if (namedEntries.containsKey(ref)) {
					setPropertyWhenAvailable(entry, object, key, namedEntries.get(ref), parameter);

				} else if (knownObjects.containsKey(ref)) {
					setProperty(entry, object, key, knownObjects.get(ref), parameter);

				} else {
					entry.setError(Status.CONFIGURE_FAILED, new ConfigurationException(object, key,
							"The referenced object " + ref + " does not exist"));
				}

			} else if (propValue instanceof ChildPropEntry) {
				final ConfigEntry childEntry = ((ChildPropEntry) propValue).getChild();
				if (propValue instanceof ChildParameterPropEntry) {
					setPropertyWhenAvailable(entry, object, key, childEntry,
							((ChildParameterPropEntry) propValue).getProperty());
				} else {
					setPropertyWhenAvailable(entry, object, key, childEntry, null);
				}

			}
		}

		entry.addListener(new ListenerAdapter() {
			@Override
			public void onDependenciesResolved() {
				entry.removeListener(this);
				initializeEntry(entry, context);
			}
		});

		entry.setStatus(Status.WAITING_FOR_DEPENDENCIES);
	}

	private static void setPropertyWhenAvailable(final ConfigEntry entry, final RoboticsObject object, final String key,
			final ConfigEntry childEntry, final String parameter) {
		entry.addDependency(childEntry);
		childEntry.addListener(new ConfigEntry.ListenerAdapter() {
			@Override
			public void onInitialized(RoboticsObject child) {
				childEntry.removeListener(this);
				if (setProperty(entry, object, key, child, parameter)) {
					entry.removeDependency(childEntry);
				}
			}

			@Override
			public void onFailed(Status status, ConfigurationException error) {
				entry.setError(Status.CONFIGURE_FAILED, new ConfigurationException(object, key,
						"The referenced object " + childEntry.getName() + " failed.", error));
			}
		});
	}

	private static boolean setProperty(final ConfigEntry entry, final RoboticsObject object, final String key,
			RoboticsObject child, final String parameter) {
		try {
			if (parameter != null) {
				Object prop = ConfigurationUtils.getPropertyValue(parameter, child);
				if (prop == null) {
					entry.setError(Status.CONFIGURE_FAILED, new ConfigurationException(object, key,
							"Could not get property " + parameter + " of referenced object " + child.getName() + "."));
					return false;
				}
				ConfigurationUtils.setProperty(object, key, prop);
			} else {
				ConfigurationUtils.setProperty(object, key, child);
			}
			return true;
		} catch (IllegalConfigurationException e) {
			entry.setError(Status.CONFIGURE_FAILED,
					new ConfigurationException(object, key, "Could not set referenced object " + child.getName(), e));
			return false;
		}
	}

	private static void buildEntry(final ConfigEntry entry, final RoboticsObjectFactory factory,
			final RoboticsContext context, final ConfigurationHook hook, final Map<String, ConfigEntry> namedEntries,
			final Map<String, RoboticsObject> knownObjects) {

		entry.addListener(new ListenerAdapter() {
			@Override
			public void onBuilt(RoboticsObject object) {
				hook.onBuilt(entry.getId(), entry.getType());
			}

			@Override
			public void onDependenciesResolved() {
				hook.onConfigured(entry.getId(), entry.getType());
			}

			@Override
			public void onInitialized(RoboticsObject object) {
				hook.onInitialized(entry.getId(), object);
			}

			@Override
			public void onFailed(Status status, ConfigurationException error) {
				if (status == Status.BUILD_FAILED) {
					hook.onBuildingFailed(entry.getId(), entry.getType(), error.getMessage());
				} else if (status == Status.CONFIGURE_FAILED) {
					hook.onConfiguringFailed(entry.getId(), entry.getType(), error.getKey(), error.getMessage());
				} else if (status == Status.INITIALIZE_FAILED) {
					hook.onInitializingFailed(entry.getId(), entry.getType(), error.getMessage(), error);
				}
			}

		});

		entry.setStatus(Status.BUILDING);

		factory.build(entry.getType(), new Callback() {
			@Override
			public void onBuilt(RoboticsObject object) {
				if (!entry.getId().isEmpty()) {
					object.setName(entry.getId());
				}
				entry.setBuiltObject(object);
				entry.setStatus(Status.BUILT);
				configureEntry(entry, context, namedEntries, knownObjects);
			}
		});

		for (ConfigEntry child : entry.getChildren()) {
			buildEntry(child, factory, context, hook, namedEntries, knownObjects);
		}
	}

	private static List<ConfigEntry> parseDocument(File base, Document document, ConfigurationHook hook)
			throws IllegalConfigurationException {
		List<ConfigEntry> ret = new ArrayList<ApplicationConfigParser.ConfigEntry>();
		if (!"RobotApplication".equals(document.getDocumentElement().getNodeName())) {
			hook.onInputReadingFailed("Invalid configuration file.", null);
			return null;
		}

		NodeList childNodes = document.getDocumentElement().getChildNodes();
		int elementNr = 0;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element) {
				if ("Object".equals(childNode.getNodeName())) {
					elementNr++;
					String id = ((Element) childNode).getAttribute("id");
					ret.add(parseElement(id.isEmpty() ? "unnamed object " + elementNr : "", (Element) childNode));
				} else if ("Include".equals(childNode.getNodeName())) {
					String src = ((Element) childNode).getAttribute("src");
					ret.addAll(parseFile(new File(base.getParentFile(), src)));
				}
			}
		}
		return ret;
	}

	private static ConfigEntry parseElement(String ancestorName, Element element) {
		String id = element.getAttribute("id");
		String type = element.getAttribute("type");
		ConfigEntry ce = new ConfigEntry(ancestorName, id, type);
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element && ("Parameter".equals(childNode.getNodeName())
					|| "ParameterReference".equals(childNode.getNodeName())
					|| "ParameterValue".equals(childNode.getNodeName())
					|| "ParameterObject".equals(childNode.getNodeName()))) {
				Element child = (Element) childNode;
				String name = child.getAttribute("name");
				if (("Parameter".equals(childNode.getNodeName()) || "ParameterObject".equals(childNode.getNodeName()))
						&& !child.getAttribute("type").isEmpty()) {
					if (!child.getAttribute("parameter").isEmpty()) {
						ce.setProperty(name, new ChildParameterPropEntry(child.getAttribute("parameter"),
								parseElement(ce.getName() + "." + name, child)));
					} else {
						ce.setProperty(name, new ChildPropEntry(parseElement(ce.getName() + "." + name, child)));
					}

				} else if (("Parameter".equals(childNode.getNodeName())
						|| "ParameterReference".equals(childNode.getNodeName()))
						&& !child.getAttribute("ref").isEmpty()) {
					if (!child.getAttribute("parameter").isEmpty()) {
						ce.setProperty(name,
								new RefParameterPropEntry(child.getAttribute("ref"), child.getAttribute("parameter")));
					} else {
						ce.setProperty(name, new RefPropEntry(child.getAttribute("ref")));
					}
				} else if ("Parameter".equals(childNode.getNodeName())
						|| "ParameterValue".equals(childNode.getNodeName()) && !child.getAttribute("value").isEmpty()) {
					ce.setProperty(name, new ValuePropEntry(child.getAttribute("value")));
				} else {
					RAPILogger.getLogger(ApplicationConfigParser.class).warning(
							"Incorrect parameter definition for '" + id + "' (" + type + "), parameter '" + name + "'");
				}
			}
		}
		return ce;
	}
}
