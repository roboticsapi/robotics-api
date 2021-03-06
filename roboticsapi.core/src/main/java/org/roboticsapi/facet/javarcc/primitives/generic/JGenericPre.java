/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.generic;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;

public class JGenericPre<T> extends JPrimitive {
	T lastValue;
	JInPort<T> inValue = add("inValue", new JInPort<T>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void readSensor() {
		lastValue = inValue.get();
	}

	@Override
	public void updateData() {
		outValue.set(lastValue);
	}

	@Override
	protected Set<JPrimitive> getReferencedPrimitives() {
		return new HashSet<JPrimitive>();
	}
}
