/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimeOrientation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

public class TransformationTest {

	RoboticsContext context;

	public TransformationTest() {
		context = new RoboticsContextImpl("dummy");
	}

	@Test
	public void testGetTransformationToCalculatesCorrectTransformation() throws RoboticsException {
		Frame frame1 = new Frame("Frame1");
		Frame frame2 = new Frame("Frame2");

		StaticPosition trans1_2 = new StaticPosition(frame1, frame2, new Transformation(1, 2, 3, -1, -3, -2));
		trans1_2.establish();

		Frame frame3 = new Frame("Frame3");

		StaticPosition trans2_3 = new StaticPosition(frame2, frame3, new Transformation(5, 1, -7, 5, -3, 8));
		trans2_3.establish();

		Frame frame5 = new Frame("Frame5");

		// Relation trans5_3 = new Placement(5, 7, 0, 1, 3, 4);
		StaticPosition trans5_3 = new StaticPosition(frame5, frame3, new Transformation(5, 7, 0, 1, 3, 4));
		trans5_3.establish();

		Transformation trans1_5 = frame1.getTransformationTo(frame5);

		Transformation expected = trans1_2.getTransformation().multiply(trans2_3.getTransformation())
				.multiply(trans5_3.getTransformation().invert()).getCurrentValue();

		Assert.assertTrue(trans1_5.isEqualTransformation(expected));
	}

	@Test
	public void testTwistsAndVelocities() throws RealtimeValueReadException, TransformationException {

		Frame origin = new Frame("Origin");
		Frame moving = new Frame("Moving");
		new StaticPosition(origin, moving, new Transformation(1, 0, 0, 0, 0, 0)).establish();

		RealtimeVelocity vel = RealtimeVelocity.createFromTwist(origin, origin.asPoint(), origin.asOrientation(),
				RealtimeTwist.createFromConstant(new Twist(0, 0, 0, 0, 0, Math.toRadians(90))));
		System.out.println(vel.getCurrentValue());
		Twist withMovingPivot = vel.getTwistForRepresentation(origin, null, origin.asRealtimeOrientation(),
				moving.asRealtimePose(), World.getCommandedTopology()).getCurrentValue();
		System.out.println(withMovingPivot);
		System.out.println(vel.getTwistForRepresentation(
				RealtimeOrientation.createFromConstant(origin, new Rotation(0, 0, Math.PI / 2)), null,
				moving.asRealtimePose(), World.getCommandedTopology()).getCurrentValue());
	}

	@Test
	public void testTransformationsAndPoses() throws TransformationException, RealtimeValueReadException {
		Frame origin = new Frame("Origin");
		Frame forward = new Frame("Forward");
		new StaticPosition(origin, forward, new Transformation(1, 0, 0, 0, 0, 0)).establish();
		Frame rotated = new Frame("Rotated");
		new StaticPosition(forward, rotated, new Transformation(0, 0, 0, Math.toRadians(90), 0, 0)).establish();
		Pose left = rotated.asPose().plus(1, 0, 0, 0, 0, 0);

		// System.out.println(origin.asPose().getCommandedTransformationTo(left));
		// RealtimePose leftPose =
		// left.asRealtimeValue().getTransformation(origin.asOrientation(), origin,
		// topology));
		// System.out.println(leftPose.getCurrentValue());
		// System.out.println(leftPose.withReference(forward).getCurrentValue());

		// RealtimeReferencedTransformation leftRT = origin
		// .getRealtimeReferencedTransformationOf(origin.getOrientation(),
		// left);
		// System.out.println(leftRT.getCurrentValue());
		// System.out.println(leftRT
		// .withOrientation(
		// new Orientation(origin, new Rotation(
		// Math.toRadians(90), 0, 0))).getCurrentValue());
		// System.out.println(leftRT.withReference(forward).getCurrentValue());

		// Pose bRot = origin.asPose().plus(0, 0, 0, 0, Math.PI / 2, 0);
		// Pose aRot = origin.asPose().plus(0, 0, 0, Math.PI / 2, 0, 0);
		// RealtimePose rrt =
		// bRot.asRealtimeValue().withReferenceAndOrientation(origin);
		// System.out.println(rrt.getCurrentValue());
		// RealtimePose rrl = rrt.withOrientation(aRot.asOrientation());
		// System.out.println(rrl.getCurrentValue());

	}

}
