/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.CommandedPosition;
import org.roboticsapi.core.world.relation.StaticPosition;

public class RealtimeVelocityTest {

	@Test
	public void testConversion() throws RealtimeValueReadException {
		Frame a = new Frame("A");
		Frame b = new Frame("B");
		Frame c = new Frame("C");
		Frame d = new Frame("D");
		new StaticPosition(a, b, new Transformation(1, 0, 0, 0, 0, 0)).establish();
		new CommandedPosition(b, c, RealtimeTransformation.createFromConstantXYZABC(0, 0, 0, Math.PI / 2, 0, 0),
				RealtimeTwist.createFromConstant(new Twist(0, 0, 0, 0, 0, 1))).establish();
		new StaticPosition(c, d, new Transformation(1, 0, 0, 0, 0, 0)).establish();
		Twist bToC = b.getCommandedRealtimeTwistOf(c).getCurrentValue();
		Assert.assertTrue(bToC.isEqualTwist(new Twist(0, 0, 0, 0, 0, 1)));
		Twist aToD = a.getCommandedRealtimeTwistOf(d).getCurrentValue();
		Assert.assertTrue(aToD.isEqualTwist(new Twist(-1, 0, 0, 0, 0, 1)));
	}

}
