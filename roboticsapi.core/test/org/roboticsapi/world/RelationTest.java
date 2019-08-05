/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Placement;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

public class RelationTest {

	RoboticsContext context;

	public RelationTest() {
		context = new RoboticsContextImpl("dummy");
	}

	List<StaticConnection> relations = new ArrayList<StaticConnection>();

	// private LWR lwr;

	@Before
	public void init() throws RoboticsException {
	}

	@After
	public void deinit() {
	}

	@Test
	public void testDeletableRelationCanBeDeleted() {
		Frame o = new Frame();
		Frame f = new Frame();

		Placement p = new Placement(new Transformation());

		try {
			context.initialize(o);
			o.addRelation(p, f);
		} catch (InitializationException e) {
			Assert.fail();
		}

		Assert.assertTrue(f.getRelations().contains(p));
		Assert.assertTrue(o.getRelations().contains(p));

		try {
			f.removeRelation(p);
		} catch (RoboticsException e) {
			Assert.fail();
		}

		Assert.assertFalse(f.getRelations().contains(p));
		Assert.assertFalse(o.getRelations().contains(p));

	}
}
