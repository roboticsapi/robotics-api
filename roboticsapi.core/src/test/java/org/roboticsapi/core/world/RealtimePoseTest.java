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
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.relation.StaticPosition;

public class RealtimePoseTest {

	@Test
	public void testGetLocalPointAsPose() throws TransformationException, RealtimeValueReadException {
		Frame a = new Frame("A");
		Frame b = new Frame("B");
		new StaticPosition(a, b, new Transformation(2, 0, 0, 0, 0, Math.PI)).establish();
		RealtimePose pose = RealtimePose.createFromTransformation(a,
				RealtimeTransformation.createFromConstantXYZABC(1, 0, 0, Math.PI / 2, 0, 0));

		Point p = new Point(null, new Vector(1, 0, 0));
		Point q = new Point(a, new Vector(1, 0, 0));
		Point r = new Point(b, new Vector(1, 0, 0));

		Pose poseP = pose.getLocalPointAsPose(p).getCurrentValue();
		Transformation transP = poseP.getTransformationForRepresentation(a.asOrientation(), a,
				World.getCommandedTopology());
		Assert.assertTrue(transP.isEqualTransformation(new Transformation(1, 1, 0, Math.PI / 2, 0, 0)));

		Pose poseQ = pose.getLocalPointAsPose(q).getCurrentValue();
		Transformation transQ = poseQ.getTransformationForRepresentation(a.asOrientation(), a,
				World.getCommandedTopology());
		Assert.assertTrue(transQ.isEqualTransformation(new Transformation(1, 0, 0, 0, 0, 0)));

		Pose poseR = pose.getLocalPointAsPose(r).getCurrentValue();
		Transformation transR = poseR.getTransformationForRepresentation(a.asOrientation(), a,
				World.getCommandedTopology());
		Assert.assertTrue(transR.isEqualTransformation(new Transformation(3, 0, 0, 0, 0, Math.PI)));

	}

}
