/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.rpi.types.ArrayType;
import org.roboticsapi.runtime.rpi.types.Type;

public abstract class JAbstractArray<T extends Type, U extends ArrayType<T>> extends JPrimitive {
	private JOutPort<U> outArray = add("outArray", new JOutPort<U>());
	private JParameter<RPIint> propSize = add("Size", new JParameter<RPIint>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		outArray.set(createArray(propSize.get().get()));
	}

	protected abstract U createArray(int size);
}
