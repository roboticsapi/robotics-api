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

public class JGenericSnapshot<T extends Type> extends JPrimitive {
	T value = null;
	JInPort<RPIbool> inSnapshot = add("inSnapshot", new JInPort<RPIbool>());
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());
	JParameter<T> propValue = add("Value", new JParameter<T>());

	public JGenericSnapshot(T value) {
		this.value = value;
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inSnapshot);
	}

	@Override
	public void updateData() {
		if (inSnapshot.get() != null && inSnapshot.get().get()) {
			value.consumeString(inValue.get().toString());
		}
		outValue.set(value);
	}
}
