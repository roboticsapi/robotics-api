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

public class JCubicBezier extends JPrimitive {
	JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inFrom = add("inFrom", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inControl1 = add("inControl1", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inControl2 = add("inControl2", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inTo = add("inTo", new JInPort<RPIdouble>());
	JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());

	JParameter<RPIdouble> propFrom = add("From", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propControl1 = add("Control1", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propControl2 = add("Control2", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propTo = add("To", new JParameter<RPIdouble>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inFrom, inTo, inControl1, inControl2))
			return;
		RPIdouble from = inFrom.get(propFrom), to = inTo.get(propTo), control1 = inControl1.get(propControl1),
				control2 = inControl2.get(propControl2), t = inValue.get();
		double a = from.get();
		double b = -3 * from.get() + 3 * control1.get();
		double c = 3 * from.get() - 6 * control1.get() + 3 * control2.get();
		double d = -from.get() + 3 * control1.get() - 3 * control2.get() + to.get();

		double value = a + t.get() * (b + t.get() * (c + d * t.get()));
		outValue.set(new RPIdouble(value));
	}
}
