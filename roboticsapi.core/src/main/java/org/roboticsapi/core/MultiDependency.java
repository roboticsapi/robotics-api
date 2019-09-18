/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;

/**
 * Represents an array of dependent objects of type T
 *
 * @param <T> type of the dependent objects
 */
public class MultiDependency<T> extends Dependency<Integer> {

	private Builder<T> builder;

	private final ConfPropertyBag<T> dependencyBag = new ConfPropertyBag<T>();
	private final Map<Integer, Dependency<T>> dependencies = new HashMap<Integer, Dependency<T>>();

	/**
	 * Creates a MultiDependency with the given name
	 */
	MultiDependency(String name) {
		super(name);
	}

	/**
	 * Creates a MultiDependency with the given name and default values
	 */
	@SafeVarargs
	MultiDependency(String name, T... values) {
		super(name, values.length);
		for (int i = 0; i < values.length; i++) {
			set(i, values[i]);
		}
	}

	/**
	 * Creates a MultiDependency with the given name and array size
	 */
	MultiDependency(String name, int size) {
		super(name, size);
	}

	/**
	 * Creates a MultiDependency with the given name and a Builder for array member
	 * elements
	 */
	MultiDependency(String name, Builder<T> builder) {
		super(name);
		this.builder = builder;
	}

	/**
	 * Creates a MultiDependency with the given name and size and a Builder for
	 * array member elements
	 */
	MultiDependency(String name, int size, Builder<T> builder) {
		super(name, size);
		this.builder = builder;
	}

	/**
	 * Creates a MultiDependency with the given name. Size and default values are
	 * provided by two respective Builders
	 */
	MultiDependency(String name, Dependency.Builder<Integer> sizeBuilder, Builder<T> builder) {
		super(name, sizeBuilder);
		this.builder = builder;
	}

	/**
	 * Sets the array size
	 */
	public final void setSize(int size) {
		set(size);
	}

	/**
	 * Size of the Dependency array
	 *
	 * @return number of entries
	 */
	public final int size() {
		return get();
	}

	/**
	 * Sets an Object as an element of the Dependency array
	 *
	 * @param index array index
	 * @param value Object to depend on
	 */
	public final void set(final int index, T value) {
		if (isInitialized() || getInitializationState() == InitializationState.PROGRESS) {
			throw new IllegalStateException("The value of dependency " + toString()
					+ " cannot be changed while the object is in initialized or initialization state.");
		}

		if (!dependencies.containsKey(index)) {
			Dependency<T> confProperty = createConfProperty(index);
			dependencies.put(index, confProperty);
		}
		dependencies.get(index).set(value);
	}

	private Dependency<T> createConfProperty(final int index) {
		Dependency<T> confProperty;
		if (builder == null) {
			confProperty = new Dependency<T>(getName() + "[" + index + "]");
		} else {
			confProperty = new Dependency<T>(getName() + "[" + index + "]", new Dependency.Builder<T>() {
				@Override
				public T create() {
					return builder.create(index);
				}
			});
		}
		return confProperty;
	}

	/**
	 * Retrieves a dependent Object from the Dependency array
	 *
	 * @param index index of the element
	 * @return the dependent object, or null if the object is not yet configured
	 * @throws NoSuchElementException if the dependency array is initialized and
	 *                                does not contain the given index
	 */
	public final T get(int index) throws NoSuchElementException {
		if (isInitialized()) {
			return dependencyBag.get(getName() + "[" + index + "]").get();
		}

		if (!dependencies.containsKey(index)) {
			get(); // Workaround to ensure we are not in initialization progress
					// (might throw a ProgressException)
			return null;
		}
		return dependencies.get(index).get();
	}

	/**
	 * Retrieves the index of a given object.
	 *
	 * @param object
	 * @return the index
	 * @throws NoSuchElementException
	 */
	public final int indexOf(T object) throws NoSuchElementException {
		for (int i = 0; i < get(); i++) {
			if (get(i) == object) {
				return i;
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * Retrieves all elements of the array
	 */
	public final List<T> getAll() {
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < get(); i++) {
			result.add(get(i));
		}
		return result;
	}

	@Override
	void initialize(RoboticsContext context, AbstractRoboticsObject parent)
			throws ProgressException, InitializationException {
		try {
			super.initialize(context, parent);
		} catch (ConfigurationException e) {
			throw new ConfigurationException(parent, this,
					"Problem with size of " + getClass().getSimpleName() + ": " + e.getPlainMessage());
		}

		int cnt = 0;
		for (int i = 0; i < get(); i++) {
			if (dependencies.containsKey(i)) {
				dependencyBag.add(dependencies.get(i));
				cnt++;
			} else {
				dependencyBag.add(createConfProperty(i));
			}
		}
		if (cnt < dependencies.size()) {
			super.uninitialize();
			throw new ConfigurationException(parent, this,
					"There are dependencies defined having their index out ouf bounds of the '"
							+ MultiDependency.class.getName() + "' with configured size.");
		}
		try {
			dependencyBag.initializeAll(context, parent);
		} catch (ProgressException e) {
			super.uninitialize();
			dependencyBag.removeAll();
			throw e;
		} catch (InitializationException e) {
			super.uninitialize();
			dependencyBag.removeAll();
			throw e;
		}
	}

	@Override
	void uninitialize() {
		dependencyBag.uninitializeAll();
		dependencyBag.removeAll();
		super.uninitialize();
	}

	@Override
	Set<RoboticsObject> getRemovedObjectsAfterPretendedUninitialization(Set<RoboticsObject> pretendedToBeRemoved)
			throws InitializationException {
		Set<RoboticsObject> result = super.getRemovedObjectsAfterPretendedUninitialization(pretendedToBeRemoved);
		return dependencyBag.getRemovedObjectsAfterPretendedUninitialization(result);
	}

	/**
	 * Builder for Dependency array elements
	 *
	 * @param <T> type of dependent objects
	 */
	public interface Builder<T> {
		/**
		 * Creates an element for a MultiDependency
		 *
		 * @param index index to create an element for
		 * @return the created object
		 */
		public T create(int index);
	}

}
