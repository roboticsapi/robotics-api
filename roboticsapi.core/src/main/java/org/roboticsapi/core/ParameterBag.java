/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public abstract class ParameterBag<T> {
	private final List<T> parameters = new Vector<T>();

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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
	}

	protected abstract boolean isElementAssignableFrom(Class<?> target);

	@SuppressWarnings("unchecked")
	public <S extends T> S get(final Class<S> type) {
		for (int i = parameters.size() - 1; i >= 0; i--) {
			if (type.isAssignableFrom(parameters.get(i).getClass())) {
				return (S) parameters.get(i);
			}
		}
		return null;
	}

	public List<T> getAll() {
		return Collections.unmodifiableList(parameters);
	}

	public abstract T[] getArray();

	@Override
	public String toString() {
		return parameters.toString();
	}
}
