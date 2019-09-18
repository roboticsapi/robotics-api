/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import org.roboticsapi.core.ParameterBag;

public class MockParameterBag<T> extends ParameterBag<T> {
	@Override
	protected ParameterBag<T> createNew() {
		return new MockParameterBag<T>();
	}

	@Override
	protected boolean isElementAssignableFrom(Class<?> target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T[] getArray() {
		// TODO Auto-generated method stub
		return null;
	}
}
