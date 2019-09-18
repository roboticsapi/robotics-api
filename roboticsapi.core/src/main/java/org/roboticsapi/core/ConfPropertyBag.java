/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.roboticsapi.core.Dependency.InitializationState;
import org.roboticsapi.core.Dependency.ProgressException;
import org.roboticsapi.core.exception.InitializationException;

public class ConfPropertyBag<T> {

	private final Map<String, Dependency<? extends T>> configurationProperties = new HashMap<String, Dependency<? extends T>>();
	private final List<Dependency<? extends T>> successfullyInitializedConfigurationProperties = new ArrayList<Dependency<? extends T>>();

	final void add(Dependency<? extends T> confProperty) {
		String name = confProperty.getName();
		if (configurationProperties.containsKey(name)) {
			throw new IllegalArgumentException("Duplicate use of same property name '" + name + "'.");
		}
		configurationProperties.put(name, confProperty);
	}

	final Dependency<? extends T> get(String name) throws NoSuchElementException {
		if (!contains(name)) {
			throw new NoSuchElementException("Property with name '" + name + "' does not exist.");
		}
		return configurationProperties.get(name);
	}

	final boolean contains(String name) {
		return configurationProperties.containsKey(name);
	}

	final void initializeAll(RoboticsContext context, AbstractRoboticsObject parent) throws InitializationException {
		// ey configs, jetz geht's los!
		for (Dependency<? extends T> cf : configurationProperties.values()) {
			// TODO: muss das rekursiv auch auf deren Dependencies propagiert
			// werden?
			cf.setInitializationState(InitializationState.PROGRESS);
		}

		try {
			boolean success = true;
			while (success) {
				success = false;
				for (Dependency<? extends T> cf : configurationProperties.values()) {
					if (cf.isInitialized()) {
						continue;
					}
					try {
						cf.initialize(context, parent);
						successfullyInitializedConfigurationProperties.add(0, cf);
						success = true;
					} catch (ProgressException e) {
						// Recursive call to another configuration property
						// builder which is not already initialized
						continue;
					} catch (InitializationException e) {
						uninitializeAll();
						throw e;
					}
				}
			}

			// Not all successfully initialized? => cyclic configuration
			// property dependencies within builders
			if (configurationProperties.size() != successfullyInitializedConfigurationProperties.size()) {
				List<Dependency<?>> uninitializedCFs = new ArrayList<Dependency<?>>(configurationProperties.values());
				uninitializedCFs.removeAll(successfullyInitializedConfigurationProperties);

				uninitializeAll();
				String simpleName = parent.getClass().getSimpleName();
				String name = "".equals(simpleName) ? parent.getClass().getName() : simpleName;
				throw new ProgressException("Unable to initialize '" + name
						+ "'. There are cyclic initialization dependencies within the builders for "
						+ uninitializedCFs.toString() + ".");
			}

		} finally {
			for (Dependency<? extends T> cf : configurationProperties.values()) {
				cf.setInitializationState(InitializationState.IDLE);
			}
		}
	}

	final void uninitializeAll() {
		for (Iterator<Dependency<? extends T>> iterator = successfullyInitializedConfigurationProperties
				.iterator(); iterator.hasNext();) {
			Dependency<?> cf = iterator.next();
			cf.uninitialize();
			iterator.remove();
		}
	}

	final Set<RoboticsObject> getRemovedObjectsAfterPretendedUninitialization(Set<RoboticsObject> pretendedToBeRemoved)
			throws InitializationException {
		for (Dependency<?> successfullyInitializedConfProp : successfullyInitializedConfigurationProperties) {
			pretendedToBeRemoved = successfullyInitializedConfProp
					.getRemovedObjectsAfterPretendedUninitialization(pretendedToBeRemoved);
		}
		return pretendedToBeRemoved;
	}

	final void removeAll() {
		if (!successfullyInitializedConfigurationProperties.isEmpty()) {
			throw new IllegalStateException("There are still some initialized objects.");
		}
		configurationProperties.clear();
	}

}
