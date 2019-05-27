/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import java.util.ArrayList;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.rpi.types.Type;

public class JGenericAtTime<T extends Type> extends JPrimitive {
	ArrayList<T> history = null;
	int historyIndex;
	int historySize;
	double maxAge;
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JInPort<RPIdouble> inAge = add("inAge", new JInPort<RPIdouble>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());
	JParameter<RPIdouble> propMaxAge = add("MaxAge", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propAge = add("Age", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
		maxAge = propMaxAge.get().get();
		historySize = (int) Math.ceil(maxAge / getNet().getCycleTime()) + 1;
		historyIndex = 0;
		history = new ArrayList<T>(historySize);
		for (int i = 0; i < historySize; i++)
			history.add(null);
	}

	@Override
	public void updateData() {
		if (anyNull(inAge.get(propAge)))
			return;
		history.set(historyIndex % historySize, inValue.get());
		double age = inAge.get(propAge).get();
		if (age < 0)
			age = 0; // don't allow asking about the future
		int pos = (int) Math.floor((age / maxAge) * (historySize - 1)) + 1;
		if (pos >= historySize)
			pos = historySize - 1;
		pos = historyIndex - pos;
		if (pos < 0) {
			outValue.set(null);
		} else {
			outValue.set(history.get(pos % historySize));
		}
		historyIndex++;
	}
}
