/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.generic;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.ArrayType;
import org.roboticsapi.facet.runtime.rpi.Type;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

public abstract class JAbstractArraySet<T extends Type, U extends ArrayType<T>> extends JPrimitive {
	JInPort<U> inArray = add("inArray", new JInPort<U>());
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JOutPort<U> outArray = add("outArray", new JOutPort<U>());
	JParameter<RPIint> propSize = add("Size", new JParameter<RPIint>());
	JParameter<RPIint> propIndex = add("Index", new JParameter<RPIint>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inArray, inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inArray))
			return;
		U array = createArray(propSize.get().get());
		U oldArray = inArray.get();
		for (int i = 0; i < propSize.get().get(); i++)
			array.set(i, oldArray.get(i));
		array.set(propIndex.get().get(), inValue.get());
		outArray.set(array);
	}

	protected abstract U createArray(int size);
}
