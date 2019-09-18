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

public class JRampify extends JPrimitive {
	JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propConstant = add("Constant", new JParameter<RPIdouble>());
	double cons, acc, v, cs, ds;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
		cons = propConstant.get().get();
		if (cons > 1 || cons < 0)
			throw new IllegalArgumentException("Constant");

		acc = (1.0 - cons) / 2.0;
		v = 1.0 / (cons + acc);
		cs = acc / 2 * v;
		ds = cs + cons * v;

	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPIdouble tRel = inValue.get(), tOut = new RPIdouble(0.0);
		if (tRel == null) {
			tOut = null;
		} else if (tRel.get() < 0) {
			tOut = new RPIdouble(0.0);
		} else if (tRel.get() < acc) {
			tOut = new RPIdouble(v / acc * tRel.get() * tRel.get() / 2);
		} else if (tRel.get() < acc + cons) {
			tOut = new RPIdouble(cs + (tRel.get() - acc) * v);
		} else if (tRel.get() <= 1) {
			tOut = new RPIdouble(1 - (v / acc * (1 - tRel.get()) * (1 - tRel.get()) / 2));
		} else {
			tOut = new RPIdouble(1.0);
		}
		outValue.set(tOut);
	}
}
