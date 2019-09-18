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

public class JDoubleBezier extends JPrimitive {
	JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inFrom = add("inFrom", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inFromVel = add("inFromVel", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inToVel = add("inToVel", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inTo = add("inTo", new JInPort<RPIdouble>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());

	JParameter<RPIdouble> propFrom = add("From", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propFromVel = add("FromVel", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propToVel = add("ToVel", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propTo = add("To", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inFrom, inTo, inFromVel, inToVel, inValue))
			return;
		RPIdouble from = inFrom.get(propFrom), to = inTo.get(propTo), fromVel = inFromVel.get(propFromVel),
				toVel = inToVel.get(propToVel), t = inValue.get();
		double control1 = from.get() + fromVel.get() / 3, control2 = to.get() - toVel.get() / 3;
		double a = from.get();
		double b = -3 * from.get() + 3 * control1;
		double c = 3 * from.get() - 6 * control1 + 3 * control2;
		double d = -from.get() + 3 * control1 - 3 * control2 + to.get();

		double value = a + t.get() * (b + t.get() * (c + d * t.get()));
		outValue.set(new RPIdouble(value));
	}
}
