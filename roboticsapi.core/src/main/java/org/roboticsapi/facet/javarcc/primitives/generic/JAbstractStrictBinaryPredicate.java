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
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;

public abstract class JAbstractStrictBinaryPredicate<T extends Type> extends JPrimitive {
	JInPort<T> inFirst = add("inFirst", new JInPort<T>());
	JInPort<T> inSecond = add("inSecond", new JInPort<T>());
	JOutPort<RPIbool> outValue = add("outValue", new JOutPort<RPIbool>());

	JParameter<T> propFirst = add("First", new JParameter<T>());
	JParameter<T> propSecond = add("Second", new JParameter<T>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		T first = inFirst.get(propFirst);
		T second = inSecond.get(propSecond);
		if (anyNull(first, second))
			return;
		outValue.set(pred(first, second));
	}

	protected abstract RPIbool pred(T first, T second);
}
