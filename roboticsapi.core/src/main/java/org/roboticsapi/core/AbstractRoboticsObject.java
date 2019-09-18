/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.roboticsapi.core.Dependency.ProgressException;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.RAPILogger;

public abstract class AbstractRoboticsObject implements RoboticsObject {

	private final ConfPropertyBag<Object> configurationProperties = new ConfPropertyBag<Object>();
	private final Dependency<String> name = createDependency("name", null);
	private boolean initializing = false;
	private boolean initialized = false;

	private List<InitializationListener> initListeners = new ArrayList<InitializationListener>();
	private final Set<RoboticsObject> references = new HashSet<RoboticsObject>();
	private final List<ReferenceListener> referenceListeners = new ArrayList<ReferenceListener>();
	private Set<Relationship> relationships = new HashSet<Relationship>();
	private final Map<Class<? extends Relationship>, List<RelationshipListener<?>>> relationshipListeners = new HashMap<Class<? extends Relationship>, List<RelationshipListener<?>>>();

	protected final <T> Dependency<T> createDependency(String name) {
		return addDependency(new Dependency<T>(name));
	}

	protected final <T, U extends T> Dependency<T> createDependency(String name, U value) {
		return addDependency(new Dependency<T>(name, value));
	}

	protected final <T> Dependency<T> createDependency(String name, Dependency.Builder<T> builder) {
		return addDependency(new Dependency<T>(name, builder));
	}

	protected final <T> MultiDependency<T> createMultiDependency(String name) {
		return addDependency(new MultiDependency<T>(name));
	}

	@SafeVarargs
	protected final <T> MultiDependency<T> createMultiDependency(String name, T... values) {
		return addDependency(new MultiDependency<T>(name, values));
	}

	protected final <T> MultiDependency<T> createMultiDependency(String name, int size) {
		return addDependency(new MultiDependency<T>(name, size));
	}

	protected final <T> MultiDependency<T> createMultiDependency(String name, MultiDependency.Builder<T> builder) {
		return addDependency(new MultiDependency<T>(name, builder));
	}

	protected final <T> MultiDependency<T> createMultiDependency(String name, int size,
			MultiDependency.Builder<T> builder) {
		return addDependency(new MultiDependency<T>(name, size, builder));
	}

	protected final <T> MultiDependency<T> createMultiDependency(String name, Dependency.Builder<Integer> sizeBuilder,
			MultiDependency.Builder<T> builder) {
		return addDependency(new MultiDependency<T>(name, sizeBuilder, builder));
	}

	private final <U, T extends Dependency<U>> T addDependency(T configurationProperty) {
		this.configurationProperties.add(configurationProperty);
		return configurationProperty;
	}

	protected final void initialize(RoboticsContext context) throws InitializationException {
		// Another initialization process already running on this object?
		if (initializing) {
			throw new InitializationException(
					"Object '" + toString() + "' is tried to be initialized multiple times concurrently.");
		}
		initializing = true;

		callBeforeInitializationSafely();

		try {
			configurationProperties.initializeAll(context, this);
			try {
				callValidateConfigurationProperties();
			} catch (InitializationException e) {
				configurationProperties.uninitializeAll();
				throw e;
			} catch (Throwable e) {
				configurationProperties.uninitializeAll();
				throw new InitializationException(
						"Unexpected error while validating dependencies of '" + toString() + "'.", e);
			}
			initialized = true;
			// this.context = context;
		} catch (ProgressException e) {
			throw new InitializationException(e.getMessage());
		} finally {
			initializing = false;
		}

		callAfterInitializationSafely();
		notifyListenersOnInitialized();
	}

	protected final void uninitialize() throws InitializationException {
		checkRecursiveUninitializationWorks();

		// FIXME: should this really be done automatically?? I think, all
		// relationships should already removed before uninitialize() gets
		// called...
		// RelationshipChangeSet situation =
		// RelationshipChangeSet.getUnmodified();
		// for (Relationship r : relationships) {
		// if (!r.canRemove(situation)) {
		// throw new InitializationException("Can not uninitialize object '" +
		// toString()
		// + "' since there is still an unremoveable relationship: " + r);
		// } else {
		// situation = situation.removing(r);
		// }
		// }

		callBeforeUninitializationSafely();

		// situation.apply();
		// context = null;
		initialized = false;
		configurationProperties.uninitializeAll();

		callAfterUninitializationSafely();

		notifyListenersOnUninitialized();
	}

	private boolean validateConfigurationPropertiesCalled = true;

	private final void callValidateConfigurationProperties() throws ConfigurationException {
		validateConfigurationPropertiesCalled = false;
		validateConfigurationProperties();
		ensureCalled("validateConfigurationProperties", validateConfigurationPropertiesCalled);
	}

	private boolean beforeInitializationCalled = false;

	private final void callBeforeInitializationSafely() {
		beforeInitializationCalled = false;
		callSafely("beforeInitialization", new Runnable() {
			@Override
			public void run() {
				beforeInitialization();
			}
		});
		ensureCalled("beforeInitialization", beforeInitializationCalled);
	}

	private boolean afterInitializationCalled = false;

	private final void callAfterInitializationSafely() {
		afterInitializationCalled = false;
		callSafely("afterInitialization", new Runnable() {
			@Override
			public void run() {
				afterInitialization();
			}
		});
		ensureCalled("afterInitialization", afterInitializationCalled);
	}

	private boolean beforeUninitializationCalled = false;

	private final void callBeforeUninitializationSafely() {
		beforeUninitializationCalled = false;
		callSafely("beforeUninitialization", new Runnable() {
			@Override
			public void run() {
				beforeUninitialization();
			}
		});
		ensureCalled("beforeUninitialization", beforeUninitializationCalled);
	}

	private boolean afterUninitializationCalled = false;

	private final void callAfterUninitializationSafely() {
		afterUninitializationCalled = false;
		callSafely("afterUninitialization", new Runnable() {
			@Override
			public void run() {
				afterUninitialization();
			}
		});
		ensureCalled("afterUninitialization", afterUninitializationCalled);
	}

	private final void callSafely(String methodName, Runnable function) {
		try {
			function.run();
		} catch (Throwable e) {
			RAPILogger.getLogger(this).log(Level.WARNING,
					"Unexpected error in '" + methodName + "' of '" + toString() + "'.", e);
		}
	}

	private final void ensureCalled(String methodName, boolean hasBeenCalled) {
		if (!hasBeenCalled) {
			RAPILogger.getLogger(this).log(Level.WARNING,
					"Superclass method call for " + methodName + " missing in " + getClass().getName());
		}
	}

	private void checkRecursiveUninitializationWorks() throws InitializationException {
		getRemovedObjectsAfterPretendedUninitialization(Collections.<RoboticsObject>emptySet());
	}

	Set<RoboticsObject> getRemovedObjectsAfterPretendedUninitialization(Set<RoboticsObject> pretendedToBeRemoved)
			throws InitializationException {
		Set<RoboticsObject> references = new HashSet<RoboticsObject>(getReferences());
		references.removeAll(pretendedToBeRemoved);
		if (!references.isEmpty()) {
			throw new InitializationException("Can not uninitialize object '" + toString()
					+ "' since there are still references from: " + references.toString());
		}

		Set<RoboticsObject> result = new HashSet<RoboticsObject>(pretendedToBeRemoved);
		result.add(this);

		result = configurationProperties.getRemovedObjectsAfterPretendedUninitialization(result);
		return result;
	}

	@Override
	public final boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Sets the name of this object.
	 *
	 * @param name the new name
	 */
	@Override
	@org.roboticsapi.configuration.ConfigurationProperty
	public final void setName(String name) {
		this.name.set(name);
	}

	@Override
	public final String getName() {
		return name.get();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + name.get() + "'";
	}

	protected void validateConfigurationProperties() throws ConfigurationException {
		validateConfigurationPropertiesCalled = true;
	}

	protected void beforeInitialization() {
		beforeInitializationCalled = true;
		// empty default implementation
	}

	protected void afterInitialization() {
		afterInitializationCalled = true;
		// empty default implementation
	}

	protected void beforeUninitialization() {
		beforeUninitializationCalled = true;
		// empty implementation
	}

	protected void afterUninitialization() {
		afterUninitializationCalled = true;
		// empty default implementation
	}

	private final void notifyListenersOnInitialized() {
		for (InitializationListener l : initListeners) {
			try {
				l.onInitialized(this);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying listener after initialization", e);
			}
		}
	}

	private final void notifyListenersOnUninitialized() {
		for (InitializationListener l : initListeners) {
			try {
				l.onUninitialized(this);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying listener after uninitialization",
						e);
			}
		}
	}

	@Override
	public final void addInitializationListener(InitializationListener l) {
		if (!initListeners.contains(l)) {
			List<InitializationListener> newListeners = new ArrayList<>(initListeners);
			newListeners.add(l);
			initListeners = newListeners;
		}
	}

	@Override
	public final void removeInitializationListener(InitializationListener l) {
		List<InitializationListener> newListeners = new ArrayList<>(initListeners);
		newListeners.remove(l);
		initListeners = newListeners;
	}

	final void addReference(RoboticsObject referencingObject) {
		references.add(referencingObject);
		for (ReferenceListener listener : referenceListeners) {
			try {
				listener.referenceAdded(referencingObject, this);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying reference listener", e);
			}
		}
	}

	final void removeReference(RoboticsObject referencingObject) {
		boolean success = references.remove(referencingObject);
		if (!success) {
			return;
		}
		for (ReferenceListener listener : referenceListeners) {
			try {
				listener.referenceRemoved(referencingObject, this);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying reference listener", e);
			}
		}
	}

	@Override
	public final Collection<RoboticsObject> getReferences() {
		return Collections.unmodifiableCollection(references);
	}

	protected final <T extends RoboticsObject> Collection<T> getReferences(Class<T> type) {
		Collection<T> ret = new ArrayList<T>();
		for (RoboticsObject ref : references) {
			if (type.isAssignableFrom(ref.getClass())) {
				ret.add(type.cast(ref));
			}
		}
		return ret;
	}

	@Override
	public final void addReferenceListener(ReferenceListener listener) {
		referenceListeners.add(listener);
		for (RoboticsObject ref : references) {
			try {
				listener.referenceAdded(ref, this);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying reference listener", e);
			}
		}
	}

	@Override
	public final void removeReferenceListener(ReferenceListener listener) {
		referenceListeners.remove(listener);
		for (RoboticsObject ref : references) {
			try {
				listener.referenceRemoved(ref, this);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying reference listener", e);
			}
		}
	}

	protected final void assertTrue(String[] keys, boolean condition, String description)
			throws ConfigurationException {
		if (!condition) {
			throw new ConfigurationException(this, keys[0], description);
		}
	}

	protected final void assertTrue(String key, boolean condition, String description) throws ConfigurationException {
		if (!condition) {
			throw new ConfigurationException(this, key, description);
		}
	}

	@Override
	public final <R extends Relationship> List<R> getRelationships(Class<R> type) {
		return getRelationshipStream(type).collect(Collectors.toList());
	}

	protected final <R extends Relationship> Stream<R> getRelationshipStream(final Class<R> type) {
		return relationships.stream().filter(r -> type.isAssignableFrom(r.getClass())).map(type::cast);
	}

	@Override
	public final <R extends Relationship> void addRelationshipListener(Class<R> type,
			RelationshipListener<R> listener) {
		List<RelationshipListener<?>> list = new ArrayList<>();
		if (relationshipListeners.containsKey(type)) {
			list.addAll(relationshipListeners.get(type));
		}

		// add listener to corresponding list
		list.add(listener);
		relationshipListeners.put(type, list);

		// notify about all currently known relationships
		for (R rs : getRelationships(type)) {
			listener.onAdded(rs);
		}
	}

	@Override
	public final <R extends Relationship> void removeRelationshipListener(Class<R> type,
			RelationshipListener<R> listener) {
		List<RelationshipListener<?>> list = new ArrayList<>();
		if (relationshipListeners.containsKey(type)) {
			list.addAll(relationshipListeners.get(type));
		}

		// remove listener from corresponding list
		list.remove(listener);
		if (list.isEmpty()) {
			relationshipListeners.remove(type);
		} else {
			relationshipListeners.put(type, list);
		}

		// notify about all currently known relationships
		getRelationshipStream(type).forEach(listener::onRemoved);
	}

	final void addRelationship(Relationship r) {
		if (relationships.contains(r)) {
			return;
		}
		Set<Relationship> newRelationships = new HashSet<Relationship>(relationships);
		newRelationships.add(r);
		relationships = newRelationships;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final void notifyRelationshipAdded(Relationship r) {
		for (Entry<Class<? extends Relationship>, List<RelationshipListener<?>>> entry : relationshipListeners
				.entrySet()) {
			if (entry.getKey().isAssignableFrom(r.getClass())) {
				for (RelationshipListener listener : entry.getValue()) {
					listener.onAdded(r);
				}
			}
		}
	}

	final void removeRelationship(Relationship r) {
		if (!relationships.contains(r)) {
			return;
		}
		Set<Relationship> newRelationships = new HashSet<Relationship>(relationships);
		newRelationships.remove(r);
		relationships = newRelationships;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final void notifyRelationshipRemoved(Relationship r) {
		for (Entry<Class<? extends Relationship>, List<RelationshipListener<?>>> entry : new HashSet<>(
				relationshipListeners.entrySet())) {
			if (entry.getKey().isAssignableFrom(r.getClass())) {
				for (RelationshipListener listener : entry.getValue()) {
					listener.onRemoved(r);
				}
			}
		}

	}

}