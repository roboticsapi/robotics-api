/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JLerp extends JPrimitive {
	JInPort<RPIdouble> inFrom = add("inFrom", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inTo = add("inTo", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inAmount = add("inAmount", new JInPort<RPIdouble>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propFrom = add("From", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propTo = add("To", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inAmount);
	}

	@Override
	public void updateData() {
		RPIdouble from = inFrom.get(propFrom), to = inTo.get(propTo);
		RPIdouble amount = inAmount.get();
		if (anyNull(from, to, amount))
			return;
		outValue.set(new RPIdouble(from.get() + amount.get() * (to.get() - from.get())));
	}
}
