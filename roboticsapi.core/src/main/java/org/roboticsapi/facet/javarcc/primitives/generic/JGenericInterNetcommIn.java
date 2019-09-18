/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.generic;

import org.roboticsapi.facet.javarcc.JNet;
import org.roboticsapi.facet.javarcc.JNetcommData;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.Type;

public class JGenericInterNetcommIn<T extends Type> extends JPrimitive {
	JNetcommData<T> data;
	JOutPort<Long> outLastUpdated = add("outLastUpdated", new JOutPort<Long>());
	JOutPort<T> outValue = add("outValue", new JOutPort<T>());

	@Override
	protected void setNet(JNet net) {
		super.setNet(net);
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		Long lastUpdated = data.getLastUpdated();
		T value = data.getData();
		if (lastUpdated == -1) {
			outValue.set(null);
			outLastUpdated.set(null);
		} else {
			outLastUpdated.set(lastUpdated);
			outValue.set(value);
		}
	}

	public void setNetcomm(JNetcommData<T> data) {
		this.data = data;
	}
}
