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
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.CommandedPosition;

public class PoseCoincidenceTest {

	@Test
	public void testPoseGetRealtimeTwistOf() throws TransformationException, RealtimeValueReadException {
		Frame root = new Frame("root");
		Frame frame1 = new Frame("1");
		Frame frame2 = new Frame("2");
		Frame frame3 = new Frame("3");

		new CommandedPosition(root, frame1,
				RealtimeTransformation.createfromConstant(new Transformation(1, 0, 0, Math.PI, 0, 0)),
				RealtimeTwist.createFromConstant(new Twist(1, 0, 0, 0, 0, 0))).establish();
		new CommandedPosition(frame1, frame2,
				RealtimeTransformation.createfromConstant(new Transformation(1, 0, 0, Math.PI, 0, 0)),
				RealtimeTwist.createFromConstant(new Twist(1, 0, 0, 0, 0, 0))).establish();
		new CommandedPosition(root, frame3,
				RealtimeTransformation.createfromConstant(new Transformation(0, 0, 1, Math.PI, 0, 0)),
				RealtimeTwist.createFromConstant(new Twist(0, 0, 1, 0, 0, 1))).establish();

		RealtimePose rootPose = root.asRealtimePose();
		RealtimePose extraPose = rootPose.plus(0, 0, 0, Math.PI, 0, 0);
		RealtimeTwist rootTo1 = rootPose.getRealtimeTwistOf(frame1.asRealtimePose(), World.getCommandedTopology());
		RealtimeTwist extraTo1 = extraPose.getRealtimeTwistOf(frame1.asRealtimePose(), World.getCommandedTopology());
		RealtimeTwist rootTo2 = rootPose.getRealtimeTwistOf(frame2.asRealtimePose(), World.getCommandedTopology());

		Assert.assertTrue(new Twist(1, 0, 0, 0, 0, 0).isEqualTwist(rootTo1.getCurrentValue()));
		Assert.assertTrue(new Twist(-1, 0, 0, 0, 0, 0).isEqualTwist(extraTo1.getCurrentValue()));
		Assert.assertTrue(new Twist(0, 0, 0, 0, 0, 0).isEqualTwist(rootTo2.getCurrentValue()));
	}

}
