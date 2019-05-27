/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives.generic;

import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JNet;
import org.roboticsapi.runtime.javarcc.JNetcommData;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.rpi.types.Type;

public class JGenericInterNetcommOut<T extends Type> extends JPrimitive {
	JNetcommData<T> data = null;
	JParameter<T> propValue = add("Value", new JParameter<T>());
	JInPort<T> inValue = add("inValue", new JInPort<T>());

	@Override
	protected void setNet(JNet net) {
		super.setNet(net);
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		data.update(inValue.get(propValue), getNet().getTime());
	}

	public void writeActuator() {
	};

	public void setNetcomm(JNetcommData<T> data) {
		this.data = data;
	}
}
