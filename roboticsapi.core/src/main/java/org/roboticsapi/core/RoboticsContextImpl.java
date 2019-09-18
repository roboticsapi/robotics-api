/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.extension.ActivatableExtension;
import org.roboticsapi.extension.Extension;
import org.roboticsapi.extension.ExtensionHandler;

public final class RoboticsContextImpl implements RoboticsContext {

	private boolean destroyed = false;
	private final String name;
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
		List<T> result = new ArrayList<>();
		for (RoboticsObject ro : initializedObjects) {
			if (type.isAssignableFrom(ro.getClass())) {
				result.add(type.cast(ro));
			}
		}
		return result;
	}

	@Override
	public synchronized void initialize(RoboticsObject object) throws InitializationException {
		checkDestroyed();
		if (object.isInitialized()) {
			throw new InitializationException("Object cannot be initialized because it is already initialized");
		}
		AbstractRoboticsObject aro = getHackyAro(object);
		aro.initialize(this);
		initializedObjects.add(aro);
		rolHandler.onAvailable(object);
	}

	@Override
	public synchronized void uninitialize(RoboticsObject object) throws InitializationException {
		checkDestroyed();
		if (!object.isInitialized()) {
			throw new InitializationException("Object cannot be uninitialized because it is not initialized");
		}
		AbstractRoboticsObject aro = getHackyAro(object);
		aro.uninitialize();
		initializedObjects.remove(aro);
		rolHandler.onUnavailable(object);
	}

	@Override
	public boolean isInitialized(RoboticsObject object) {
		checkDestroyed();
		return initializedObjects.contains(object);
	}

	private AbstractRoboticsObject getHackyAro(RoboticsObject roboticsObject) {
		// FIXME: initialization for RoboticsObjects or change signature!
		if (!(roboticsObject instanceof AbstractRoboticsObject)) {
			throw new RuntimeException("All RoboticsObjects need to be AbstractRoboticsObjects");
		}
		return (AbstractRoboticsObject) roboticsObject;
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
		return RAPILogger.getLogger(this);
	}

	@Override
	public synchronized void clearRoboticsObjects() {
		checkDestroyed();

		// uninitialize all
		while (initializedObjects.size() > 0) {
			boolean change = false;
			InitializationException exception = null;
			for (RoboticsObject ro : new HashSet<RoboticsObject>(initializedObjects)) {
				try {
					uninitialize(ro);
					change = true;
					exception = null;
				} catch (InitializationException e) {
					exception = e;
				}
			}
			if (!change) {
				throw new IllegalStateException("Can not destroy since some related objects can not be uninitialized: "
						+ initializedObjects.toString(), exception);
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
