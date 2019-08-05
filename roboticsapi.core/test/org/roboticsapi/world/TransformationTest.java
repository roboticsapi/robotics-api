/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

public class TransformationTest {

	RoboticsContext context;

	public TransformationTest() {
		context = new RoboticsContextImpl("dummy");
	}

	@Test
	public void testGetTransformationToCalculatesCorrectTransformation() throws RoboticsException {
		Frame frame1 = new Frame("Frame1");
		Frame frame2 = new Frame("Frame2");

		StaticConnection trans1_2 = new StaticConnection(new Transformation(1, 2, 3, -1, -3, -2));
		context.initialize(frame1);
		frame1.addRelation(trans1_2, frame2);

		Frame frame3 = new Frame("Frame3");

		StaticConnection trans2_3 = new StaticConnection(new Transformation(5, 1, -7, 5, -3, 8));
		frame2.addRelation(trans2_3, frame3);

		Frame frame5 = new Frame("Frame5");
		// Relation trans5_3 = new Placement(5, 7, 0, 1, 3, 4);
		StaticConnection trans5_3 = new StaticConnection(new Transformation(5, 7, 0, 1, 3, 4));
		context.initialize(frame5);
		frame5.addRelation(trans5_3, frame3);

		Transformation trans1_5 = frame1.getTransformationTo(frame5);

		Transformation expected = trans1_2.getTransformation().multiply(trans2_3.getTransformation())
				.multiply(trans5_3.getTransformation().invert());

		Assert.assertTrue(trans1_5.isEqualTransformation(expected));
	}
}
