/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.observation.FrameObservation;
import org.roboticsapi.core.world.relation.Placement;
import org.roboticsapi.core.world.relation.StaticPosition;

public class EstimatorTest {

	@Test
	public void testOneRelationAndObservation() throws InitializationException {
		RoboticsContext ctx = new RoboticsContextImpl("Test");

		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		ctx.initialize(f1);
		ctx.initialize(f2);

		RoboticsRuntime runtime = new TestRuntime();
		ctx.initialize(runtime);

		SimpleEstimator est = new SimpleEstimator();
		est.setRuntime(runtime);
		est.setRoot(f1);
		ctx.initialize(est);

		Placement placement = new Placement(f1, f2);
		placement.establish();

		FrameObservation observation = new FrameObservation();
		observation.setFrom(f1);
		observation.setTo(f2);
		ctx.initialize(observation);

		assertEquals(3, f1.getRelations(World.getMeasuredTopology()).size());
		assertEquals(3, f2.getRelations(World.getMeasuredTopology()).size());
	}

	@Test
	public void testTwoRelationsAndObservation() throws InitializationException {
		RoboticsContext ctx = new RoboticsContextImpl("Test");

		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		Frame f3 = new Frame("f3");
		ctx.initialize(f1);
		ctx.initialize(f2);
		ctx.initialize(f3);

		RoboticsRuntime runtime = new TestRuntime();
		ctx.initialize(runtime);

		SimpleEstimator est = new SimpleEstimator();
		est.setRuntime(runtime);
		est.setRoot(f1);
		ctx.initialize(est);

		Placement p1 = new Placement(f1, f2);
		p1.establish();

		Placement p2 = new Placement(f2, f3);
		p2.establish();

		FrameObservation observation = new FrameObservation();
		observation.setFrom(f1);
		observation.setTo(f3);
		ctx.initialize(observation);

		assertEquals(3, f1.getRelations(World.getMeasuredTopology()).size());
		assertEquals(2, f2.getRelations(World.getMeasuredTopology()).size());
		assertEquals(3, f3.getRelations(World.getMeasuredTopology()).size());
	}

	@Test
	public void testGeometricLogicalGeometricRelationsAndObservation() throws InitializationException {
		RoboticsContext ctx = new RoboticsContextImpl("Test");

		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		Frame f3 = new Frame("f3");
		Frame f4 = new Frame("f4");
		ctx.initialize(f1);
		ctx.initialize(f2);
		ctx.initialize(f3);
		ctx.initialize(f4);

		RoboticsRuntime runtime = new TestRuntime();
		ctx.initialize(runtime);

		SimpleEstimator est = new SimpleEstimator();
		est.setRuntime(runtime);
		est.setRoot(f1);
		ctx.initialize(est);

		StaticPosition p1 = new StaticPosition(f1, f2, Transformation.IDENTITY);
		p1.establish();

		Placement p2 = new Placement(f2, f3);
		p2.establish();

		StaticPosition p3 = new StaticPosition(f3, f4, Transformation.IDENTITY);
		p3.establish();

		FrameObservation observation = new FrameObservation();
		observation.setFrom(f1);
		observation.setTo(f4);
		ctx.initialize(observation);

		assertEquals(2, f1.getRelations(World.getMeasuredTopology()).size());
		assertEquals(3, f2.getRelations(World.getMeasuredTopology()).size());
		assertEquals(3, f3.getRelations(World.getMeasuredTopology()).size());
		assertEquals(2, f4.getRelations(World.getMeasuredTopology()).size());
	}

}
