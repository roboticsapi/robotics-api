/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.generic;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JNet;
import org.roboticsapi.facet.javarcc.JNetcommData;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.Type;

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
