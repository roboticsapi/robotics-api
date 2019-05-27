/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.rpi.types.ArrayType;
import org.roboticsapi.runtime.rpi.types.Type;

public class JGenericArrayGet<T extends Type, U extends ArrayType<T>> extends JPrimitive {
	JInPort<U> inArray = add("inArray", new JInPort<U>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	JParameter<RPIint> propSize = add("Size", new JParameter<RPIint>());
	JParameter<RPIint> propIndex = add("Index", new JParameter<RPIint>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inArray);
		if (propIndex.get().get() >= propSize.get().get())
			throw new IllegalArgumentException("index >= size");
	}

	@Override
	public void updateData() {
		if (anyNull(inArray))
			return;
		U array = inArray.get();
		outValue.set(array.get(propIndex.get().get()));
	}
}
