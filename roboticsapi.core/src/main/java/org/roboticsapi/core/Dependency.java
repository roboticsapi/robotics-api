/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.RAPILogger;

/**
 * @param <T> The type of value this dependency has.
 */
public class Dependency<T> {

	private final String name;
	private final boolean defaultValueSet;
	private T value = null;
	private final Builder<T> builder;

	private boolean autocreatedObject = false;
	private boolean autoinitializedObject = false;
	private ParentRelationship autoparentedObject = null;
	private boolean autoreferencedObject = false;
	private boolean autoaddedRelation = false;

	private boolean initialized = false;
	private InitializationState initializationState = InitializationState.IDLE;
	private RoboticsContext context;
	private AbstractRoboticsObject parent;

	private final List<ChangeListener<T>> changeListeners = new ArrayList<>();

	/**
	 * Creates a new dependency which is not optional. A value has necessarily to be
	 * set before this dependency can become initialized.
	 *
	 * @param name the name of this dependency.
	 */
	Dependency(String name) {
		this.name = name;
		this.value = null;
		this.builder = null;
		this.defaultValueSet = false;
	}

	/**
	 * Creates a new dependency which is optional. When this dependency is about
	 * being initialized and no value has been set, the value is retrieved by the
	 * given builder.
	 *
	 * @param name    the name of this dependency.
	 * @param builder the builder which provides a "default" value if none has been
	 *                set.
	 */
	Dependency(String name, Builder<T> builder) {
		this.name = name;
		this.builder = builder;
		this.defaultValueSet = builder == null;
	}

	/**
	 * Creates a new dependency which is optional. The value of this dependency can
	 * be overwritten or left as is.
	 *
	 * @param name         the name of this dependency.
	 * @param defaultValue the default value until it is overwritten.
	 */
	Dependency(String name, T defaultValue) {
		this.name = name;
		this.defaultValueSet = true;
		this.value = defaultValue;
		this.builder = null;
	}

	/**
	 * Returns the name of this property.
	 *
	 * @return the name of this property.
	 */
	public final String getName() {
		return name;
	}

	@Override
	public final String toString() {
		return getName();
	}

	/**
	 * Set or overwrite the current value of this property. This is only possible,
	 * if this dependency is NOT initialized or currently initializing.
	 *
	 * @param value the new value for this dependency.
	 * @throws IllegalStateException when this dependency is currently initialized
	 *                               or initializing.
	 */
	public final void set(T value) {
		if (isInitialized() || initializationState == InitializationState.PROGRESS) {
			throw new IllegalStateException("The dependency value of " + toString()
					+ " cannot be changed while the object is in initialized or initialization state.");
		}
		T oldValue = this.value;
		this.value = value;

		if (oldValue != value) {
			notifyChangeListeners(oldValue, value);
		}
	}

	/**
	 * Returns the current value of this dependency. A call to this function is NOT
	 * permitted in the case, that this property is currently initializing.
	 *
	 * @return the current value of this dependency.
	 * @throws ProgressException when this dependency is currently initializing.
	 */
	public final T get() {
		// Forbid other configuration property builders to use this property if
		// it is not initialized
		if (initializationState == InitializationState.PROGRESS && !isInitialized()) {
			throw new ProgressException("Dependency '" + getName() + "' "
					+ (parent != null ? "of object '" + parent.getClass().getSimpleName() + "' " : "")
					+ "is not accessible during initialization.");
		}
		return value;
	}

	/**
	 * Signalizes, if this dependency is initialized. That means, that the value of
	 * this dependency is fixed and can't be changed until this dependency gets
	 * uninitialized again.
	 *
	 * @return whether this dependency is initialized.
	 */
	final boolean isInitialized() {
		return initialized;
	}

	/**
	 * Initializes this dependency. This includes:
	 *
	 * (1) If this dependencies' value has not been set and there is no default
	 * value, create a new value by the specified builder.
	 *
	 * (2a) In case that this dependency's value is of type
	 * {@link AbstractRoboticsObject}, it gets initialized and gets added the given
	 * parent as reference.
	 *
	 * (2b) In case that this dependency's value is of type {@link Relationship},
	 * the relationship gets established.
	 *
	 * If initialization fails, all changes are made for initialization so far are
	 * rolled back.
	 *
	 * @param context the {@link RoboticsContext} to use for initialization.
	 * @param parent  the parent holding this dependency.
	 * @throws ProgressException       is thrown, when there is a cyclic connection
	 *                                 between two or more dependencies.
	 * @throws InitializationException is thrown, when auto-initialization of value
	 *                                 (of type {@link AbstractRoboticsObject})
	 *                                 fails.
	 * @throws ConfigurationException  is thrown, when a non-optional dependency
	 *                                 does not have a valid value, or when there is
	 *                                 another reason which prohibits initialization
	 *                                 of this dependency.
	 */
	void initialize(RoboticsContext context, AbstractRoboticsObject parent)
			throws ProgressException, InitializationException {
		this.context = context;
		this.parent = parent;
		T oldValue = value;

		// autobuild value
		if (!defaultValueSet && value == null) {
			if (builder == null) {
				throw new ConfigurationException(parent, this,
						"Initialization of required dependency failed since it has not been set.");
			}
			try {
				value = builder.create();
				autocreatedObject = true;
			} catch (ProgressException e) {
				uninitialize();
				throw e;
			} catch (Throwable e) {
				uninitialize();
				throw new ConfigurationException(parent, this,
						"Initialization of dependency failed due to internal errors.", e);
			}
		}

		// autoinitialize
		// autoreference
		if (value != null && value instanceof RoboticsObject) {
			RoboticsObject ro = (RoboticsObject) value;

			if (ro instanceof AbstractRoboticsObject) {
				AbstractRoboticsObject aro = (AbstractRoboticsObject) ro;

				// autoinitialize value
				if (!aro.isInitialized()) {
					try {
						context.initialize(aro);
					} catch (InitializationException e) {
						uninitialize();
						throw e;
					} catch (Throwable e) {
						uninitialize();
						throw new ConfigurationException(parent, this,
								"Unexpected error while initializing object '" + aro.toString() + "'.", e);
					}
					autoinitializedObject = true;
				} else if (!context.isInitialized(aro)) {
					uninitialize();
					throw new ConfigurationException(parent, this,
							"Object '" + ro.toString() + "' belongs to another context.");
				}

				// Set reference to all objects which are automatic and non
				// automatic
				aro.addReference(parent);
				autoreferencedObject = true;

			} else if (!ro.isInitialized()) {
				uninitialize();
				throw new ConfigurationException(parent, this,
						"Object '" + ro.toString() + "' has to be initialized beforehand since it is no "
								+ AbstractRoboticsObject.class.getName() + ".");
			}
		}

		// autoparent
		if (value != null && parent instanceof RoboticsEntity && value instanceof RoboticsEntity) {
			RoboticsEntity _parent = (RoboticsEntity) parent;
			RoboticsEntity _value = (RoboticsEntity) value;
			ParentRelationship autoparentedObject = new ParentRelationship(_parent, _value);
			try {
				autoparentedObject.establish();
				this.autoparentedObject = autoparentedObject;
			} catch (Throwable e) {
				// uninitialize();
				// throw new ConfigurationException(parent, this,
				// "Unexpected error while autoparenting object '" +
				// value.toString() + "': " + e.getMessage(), e);
			}
		}

		if (value != null && value instanceof Relationship) {
			try {
				((Relationship) value).establish();
				autoaddedRelation = true;
			} catch (IllegalStateException e) {
				throw new ConfigurationException(parent, this, "Cannot establish relationship '" + value + "'.", e);
			}
		}

		initialized = true;

		if (oldValue != value) {
			notifyChangeListeners(oldValue, value);
		}
	}

	/**
	 * Uninitializes this dependency. All automatic changes, which are made due to
	 * initialization, are revoked. A call to this method succeeds in any case. If
	 * something bad happens (parent element doesn't allow unparenting, etc.), it is
	 * logged into {@link RAPILogger}.
	 */
	void uninitialize() {

		if (autoaddedRelation) {
			((Relationship) value).remove();
			autoaddedRelation = false;
		}

		// autoparented?
		if (autoparentedObject != null) {
			try {
				autoparentedObject.remove();
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE,
						"Can not properly remove child-parent-relationship of dependency " + toString() + " of "
								+ parent.toString() + ".",
						e);
			}
			autoparentedObject = null;
		}

		// autoreferenced?
		if (autoreferencedObject) {
			try {
				((AbstractRoboticsObject) value).removeReference(parent);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE, "Can not properly remove reference of dependency "
						+ toString() + " of " + parent.toString() + ".", e);
			}
			autoreferencedObject = false;
		}

		// autoinitialized?
		if (autoinitializedObject) {
			try {
				context.uninitialize((AbstractRoboticsObject) value);
			} catch (Throwable e) {
				RAPILogger.getLogger(this).log(Level.SEVERE,
						"Can not uninitialize dependency " + toString() + " of " + parent.toString() + ".", e);
			}
			autoinitializedObject = false;
		}

		// autocreated?
		T oldValue = value;
		if (autocreatedObject) {
			value = null;
			autocreatedObject = false;
		}

		initialized = false;
		context = null;
		parent = null;

		if (oldValue != value) {
			notifyChangeListeners(oldValue, value);
		}
	}

	/**
	 * This method is internally used to check whether this dependency can be
	 * uninitialized. This dependency (or its value) holds references to
	 * auto-created or auto-initialized objects. Assumed, a set of
	 * {@link RoboticsObject}s is already uninitialized. Which of these references
	 * are then free of other references and could be uninitialized?
	 *
	 * @param pretendedToBeRemoved set of pretended uninitialized objects.
	 * @return set of possible uninitializable objects, pretending the specified set
	 *         of objects is already uninitialized.
	 * @throws InitializationException
	 */
	Set<RoboticsObject> getRemovedObjectsAfterPretendedUninitialization(Set<RoboticsObject> pretendedToBeRemoved)
			throws InitializationException {
		if (autoinitializedObject) {
			return ((AbstractRoboticsObject) value)
					.getRemovedObjectsAfterPretendedUninitialization(pretendedToBeRemoved);
		}
		return pretendedToBeRemoved;
	}

	/**
	 * Returns the current {@link InitializationState} of this dependency.
	 *
	 * @return
	 */
	final InitializationState getInitializationState() {
		return initializationState;
	}

	/**
	 * Sets the current {@link InitializationState} of this dependency.
	 *
	 * @return
	 */
	final void setInitializationState(InitializationState initializationState) {
		this.initializationState = initializationState;
	}

	public static interface ChangeListener<T> {
		public void valueChanged(T oldValue, T newValue);
	}

	/**
	 * Registers a listener which is notified about every value change of this
	 * property. If this property is automatically filled during initialization, the
	 * change listener is called after initialization finished.
	 *
	 * The change listener will be called immediately after registration with the
	 * current value of the property.
	 *
	 * @param changeListener
	 */
	public final void onChange(ChangeListener<T> changeListener) {
		changeListeners.add(changeListener);
		T value = get();
		notifyChangeListener(changeListener, value, value);
	}

	private final void notifyChangeListeners(T oldValue, T newValue) {
		for (ChangeListener<T> changeListener : changeListeners) {
			notifyChangeListener(changeListener, oldValue, newValue);
		}
	}

	private final void notifyChangeListener(ChangeListener<T> changeListener, T oldValue, T newValue) {
		try {
			changeListener.valueChanged(oldValue, newValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builder for {@link Dependency}s which allows for an optional dependency.
	 *
	 * @param <T> type of value which can be built.
	 */
	public interface Builder<T> {

		/**
		 * Creates a new value of the specified type.
		 *
		 * @return
		 */
		public T create();
	}

	/**
	 * the initialization state indicates, whether the change from uninitialized to
	 * initialized or vice versa is currently in progress.
	 */
	static enum InitializationState {
		IDLE, PROGRESS
	}

	static class ProgressException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ProgressException(String message) {
			super(message);

		}
	}

}
