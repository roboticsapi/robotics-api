/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.rpi.types.Type;

public class JGenericSnapshot<T extends Type> extends JPrimitive {
	T value = null;
	JInPort<RPIbool> inActive = add("inActive", new JInPort<RPIbool>());
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
		if (inActive.get() == null || inActive.get().get()) {
			if (inSnapshot.get() != null && inSnapshot.get().get()) {
				value.consumeString(inValue.get().toString());
			}
			outValue.set(value);
		}
	}
}
