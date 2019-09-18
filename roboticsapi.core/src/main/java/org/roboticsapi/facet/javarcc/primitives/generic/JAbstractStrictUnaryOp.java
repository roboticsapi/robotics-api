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
import org.roboticsapi.facet.runtime.rpi.Type;

public abstract class JAbstractStrictUnaryOp<T extends Type, U extends Type> extends JPrimitive {
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JOutPort<U> outValue = add("outValue", new JOutPort<U>());

	JParameter<T> propValue = add("Value", new JParameter<T>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		T value = inValue.get(propValue);
		if (anyNull(value))
			return;
		outValue.set(op(value));
	}

	protected abstract U op(T value);
}
