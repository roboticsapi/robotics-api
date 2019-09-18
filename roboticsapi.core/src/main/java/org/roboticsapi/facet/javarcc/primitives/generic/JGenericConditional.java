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

public class JGenericConditional<T extends Type> extends JPrimitive {

	JInPort<RPIbool> inCondition = add("inCondition", new JInPort<RPIbool>());
	JInPort<T> inTrue = add("inTrue", new JInPort<T>());
	JInPort<T> inFalse = add("inFalse", new JInPort<T>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	JParameter<T> propTrue = add("True", new JParameter<T>());
	JParameter<T> propFalse = add("False", new JParameter<T>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inCondition);
	}

	@Override
	public void updateData() {
		RPIbool cond = inCondition.get();
		T t = inTrue.get(propTrue);
		T f = inFalse.get(propFalse);
		if (cond == null)
			outValue.set(null);
		else if (cond.get())
			outValue.set(t);
		else
			outValue.set(f);
	}
}
