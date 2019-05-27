/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.extension.ActivatableExtension;
import org.roboticsapi.extension.Extension;
import org.roboticsapi.extension.ExtensionHandler;

/**
 */
public final class RoboticsContextImpl implements RoboticsContext {

	private boolean destroyed = false;
	private final String name;
	private final Map<String, RoboticsObject> registeredObjects = new HashMap<String, RoboticsObject>();
	private final Set<AbstractRoboticsObject> initializedObjects = new HashSet<AbstractRoboticsObject>();

	private final Set<Extension> extensions = new HashSet<Extension>();
	private final Set<ExtensionHandler<?>> extensionHandlers = new HashSet<ExtensionHandler<?>>();

	private final RoboticsObjectListenerHandler rolHandler = new RoboticsObjectListenerHandler();

	public RoboticsContextImpl(String name) {
		this.name = name;
		this.addExtensionHandler(rolHandler);
	}

	private void checkDestroyed() {
		if (destroyed) {
			throw new IllegalStateException("Context is already destroyed");
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public synchronized <T extends RoboticsObject> List<T> getAll(Class<T> type) {
		checkDestroyed();
		List<T> result = getRegistered(type);
		for (RoboticsObject ro : initializedObjects) {
			if (type.isAssignableFrom(ro.getClass()) && !result.contains(ro)) {
				result.add(type.cast(ro));
			}
		}
		return result;
	}

	@Override
	public synchronized <T extends RoboticsObject> List<T> getRegistered(Class<T> type) {
		checkDestroyed();
		List<T> result = new ArrayList<T>();
		for (RoboticsObject ro : registeredObjects.values()) {
			if (type.isAssignableFrom(ro.getClass())) {
				result.add(type.cast(ro));
			}
		}
		return result;
	}

	@Override
	public synchronized <T extends RoboticsObject> T getRegistered(String name, Class<T> type)
			throws IllegalArgumentException {
		checkDestroyed();
		RoboticsObject ro = registeredObjects.get(name);
		if (ro != null && type.isAssignableFrom(ro.getClass())) {
			return type.cast(ro);
		}
		throw new IllegalArgumentException(
				"There is no registered object of type " + type.getSimpleName() + " with name '" + name + "'");
	}

	@Override
	public synchronized <T extends RoboticsObject> boolean isRegistered(String name, Class<T> type) {
		checkDestroyed();
		RoboticsObject ro = registeredObjects.get(name);
		if (ro != null && type.isAssignableFrom(ro.getClass())) {
			return true;
		}
		return false;
	}

	@Override
	public synchronized void initialize(RoboticsObject object) throws InitializationException {
		checkDestroyed();
		// FIXME: initialization for RoboticsObjects or change signature!
		if (!(object instanceof AbstractRoboticsObject)) {
			throw new InitializationException("Can only initialize objects of type AbstractRoboticsObject");
		}

		AbstractRoboticsObject aro = (AbstractRoboticsObject) object;
		aro.initialize(this);
		initializedObjects.add(aro);
		rolHandler.onAvailable(object);
	}

	@Override
	public synchronized void uninitialize(RoboticsObject object) throws InitializationException {
		checkDestroyed();
		if (!(object instanceof AbstractRoboticsObject)) {
			throw new InitializationException("Can only uninitialize objects of type AbstractRoboticsObject");
		}

		if (object.getName() != null && registeredObjects.containsKey(object.getName())) {
			throw new InitializationException("Can not unitialize still registered objects");
		}

		AbstractRoboticsObject aro = (AbstractRoboticsObject) object;
		aro.uninitialize();
		initializedObjects.remove(aro);
		rolHandler.onUnavailable(object);
	}

	@Override
	public synchronized void register(RoboticsObject object) {
		checkDestroyed();
		if (!object.isInitialized()) {
			throw new IllegalArgumentException("The object is not initialized");
		}
		if (object.getName() == null) {
			throw new IllegalArgumentException("Registered objects requires a name");
		}
		if (registeredObjects.containsKey(object.getName())) {
			throw new IllegalArgumentException(
					"Another object with the same name is already registered: " + object.getName());
		}
		registeredObjects.put(object.getName(), object);
	}

	@Override
	public synchronized void unregister(RoboticsObject object) {
		checkDestroyed();
		if (object.getName() == null || !registeredObjects.containsKey(object.getName())) {
			throw new IllegalArgumentException("The object is not registered and thus cannot be unregistered");
		}
		registeredObjects.remove(object.getName());
	}

	@Override
	public synchronized boolean hasExtension(Extension extension) {
		checkDestroyed();
		return extensions.contains(extension);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <U> void addExtension(final Extension extension) {
		checkDestroyed();
		if (extensions.contains(extension)) {
			throw new IllegalArgumentException(
					"Extension is already added ('" + extension.getClass().getSimpleName() + "')");
		}

		if (extension instanceof ActivatableExtension) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					((ActivatableExtension) extension).activate();
				}
			});
		}

		extensions.add(extension);
		for (@SuppressWarnings("rawtypes")
		final ExtensionHandler ec : extensionHandlers) {
			if (ec.getType().isAssignableFrom(extension.getClass())) {
				safelyExecute(new Runnable() {
					@Override
					public void run() {
						ec.addExtension(extension);
					}
				});
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T extends Extension> Set<T> getExtensions(Class<T> type) {
		checkDestroyed();
		Set<T> result = new HashSet<T>();
		for (Extension extension : extensions) {
			if (type.isAssignableFrom(extension.getClass())) {
				result.add((T) extension);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void removeExtension(final Extension extension) {
		checkDestroyed();
		if (!extensions.contains(extension)) {
			throw new IllegalArgumentException("Extension is not contained");
		}

		for (@SuppressWarnings("rawtypes")
		final ExtensionHandler ec : extensionHandlers) {
			if (ec.getType().isAssignableFrom(extension.getClass())) {
				safelyExecute(new Runnable() {
					@Override
					public void run() {
						ec.removeExtension(extension);
					}
				});
			}
		}
		extensions.remove(extension);

		if (extension instanceof ActivatableExtension) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					((ActivatableExtension) extension).deactivate();
				}
			});
		}
	}

	@Override
	public synchronized <T extends Extension> void addExtensionHandler(final ExtensionHandler<T> extensionHandler) {
		checkDestroyed();
		extensionHandlers.add(extensionHandler);
		for (final T e : getExtensions(extensionHandler.getType())) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					extensionHandler.addExtension(e);
				}
			});
		}
	}

	@Override
	public synchronized <T extends Extension> void removeExtensionHandler(final ExtensionHandler<T> extensionHandler) {
		checkDestroyed();
		for (final T e : getExtensions(extensionHandler.getType())) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					extensionHandler.removeExtension(e);
				}
			});
		}
	}

	private void safelyExecute(Runnable r) {
		// try {
		r.run();
		// } catch (Throwable e) {
		// getLogger().log(Level.SEVERE,
		// "Can not register extension at extension consumer");
		// }
	}

	@Override
	public synchronized Logger getLogger() {
		// TODO: Mal besser machen...
		return RAPILogger.getLogger();
	}

	@Override
	public synchronized void clearRoboticsObjects() {
		checkDestroyed();

		// unregister all
		for (RoboticsObject ro : new HashSet<RoboticsObject>(registeredObjects.values())) {
			unregister(ro);
		}

		// uninitialize all
		while (initializedObjects.size() > 0) {
			boolean change = false;
			for (RoboticsObject ro : new HashSet<RoboticsObject>(initializedObjects)) {
				try {
					uninitialize(ro);
					change = true;
				} catch (InitializationException e) {
				}
			}
			if (!change) {
				throw new IllegalStateException("Can not destroy since some related objects can not be uninitialized");
			}
		}
	}

	@Override
	public synchronized void destroy() {
		checkDestroyed();

		// clear
		clearRoboticsObjects();

		// remove extensions
		for (Extension extension : new HashSet<Extension>(extensions)) {
			removeExtension(extension);
		}

		// remove extension consumers
		extensionHandlers.clear();

		destroyed = true;
	}

}
