/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public abstract class ParameterBag<T> {
	private final List<T> parameters = new Vector<T>();

	public ParameterBag<T> withParameters(final T... additionalDeviceParameters) {
		ParameterBag<T> newBag = createNew();
		newBag.addParameters(parameters);
		newBag.addParameters(additionalDeviceParameters);
		return newBag;
	}

	public ParameterBag<T> withParameters(final ParameterBag<T> additionalParameters) {

		return withParameters(additionalParameters.getArray());
	}

	protected abstract ParameterBag<T> createNew();

	private void addParameters(final T... additionalDeviceParameters) {
		for (final T param : additionalDeviceParameters) {
			addParameter(param);
		}
	}

	private void addParameters(final List<T> additionalDeviceParameters) {
		for (final T param : additionalDeviceParameters) {
			addParameter(param);
		}
	}

	private void addParameter(final T param) {
		parameters.add(param);

		for (int i = parameters.size() - 2; i >= 0; i--) {
			if (parameters.get(i).getClass().isAssignableFrom(param.getClass())) {
				parameters.remove(i);
			}
		}

		// final HashSet<Class<?>> implementedInterfaces = new
		// HashSet<Class<?>>();
		// for (int i = parameters.size() - 1; i >= 0; i--) {
		// boolean stillNeeded = false;
		// for (final Class<?> iface : collectParameterInterfaces(parameters
		// .get(i).getClass())) {
		// if (!implementedInterfaces.contains(iface)) {
		// implementedInterfaces.add(iface);
		// stillNeeded = true;
		// }
		// }
		// if (!stillNeeded) {
		// parameters.remove(i);
		// }
		// }
	}

	// private List<Class<?>> collectParameterInterfaces(final Class<?> target)
	// {
	// final List<Class<?>> ret = new Vector<Class<?>>();
	//
	// if (target.getSuperclass() != null &&
	// isElementAssignableFrom(target.getSuperclass())) {
	// ret.add(target);
	// ret.addAll(collectParameterInterfaces(target.getSuperclass()));
	// }
	// for (final Class<?> iface : target.getInterfaces()) {
	// if (isElementAssignableFrom(iface)) {
	// ret.add(iface);
	// }
	// ret.addAll(collectParameterInterfaces(iface));
	// }
	// return ret;
	// }

	protected abstract boolean isElementAssignableFrom(Class<?> target);

	@SuppressWarnings("unchecked")
	public <S extends T> S get(final Class<S> type) {
		for (int i = parameters.size() - 1; i >= 0; i--) {
			if (type.isAssignableFrom(parameters.get(i).getClass())) {
				return (S) parameters.get(i);
			}
		}
		// throw new InvalidParametersException("No device parameters of type "
		// + type.getSimpleName() + " given.");
		return null;
	}

	public List<T> getAll() {
		return Collections.unmodifiableList(parameters);
	}

	public abstract T[] getArray();

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (T p : getAll()) {
			sb.append(p.toString());
		}
		return sb.toString();
	}
}
