/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableTwist;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;

public class JTwistAdd extends JPrimitive {
	private JInPort<RPITwist> inFirst = add("inFirst", new JInPort<RPITwist>());
	private JInPort<RPITwist> inSecond = add("inSecond", new JInPort<RPITwist>());
	private JOutPort<RPITwist> outValue = add("outValue", new JOutPort<RPITwist>());

	private MutableTwist first = RPICalc.twistCreate();
	private MutableTwist second = RPICalc.twistCreate();
	private RPITwist value = RPICalc.rpiTwistCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirst, inSecond);
	}

	@Override
	public void updateData() {
		if (anyNull(inFirst, inSecond))
			return;
		RPICalc.rpiToTwist(inFirst.get(), first);
		RPICalc.rpiToTwist(inSecond.get(), second);
		first.getTranslation().add(second.getTranslation());
		first.getRotation().add(second.getRotation());
		RPICalc.twistToRpi(first, value);
		outValue.set(value);
	}

};