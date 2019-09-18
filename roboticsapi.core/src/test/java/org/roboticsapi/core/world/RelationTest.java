/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.world.relation.Placement;
import org.roboticsapi.core.world.relation.StaticConnection;

public class RelationTest {

	RoboticsContext context;

	public RelationTest() {
		context = new RoboticsContextImpl("dummy");
	}

	List<StaticConnection> relations = new ArrayList<StaticConnection>();

	@Test
	public void testDeletableRelationCanBeDeleted() {
		Frame o = new Frame();
		Frame f = new Frame();

		Placement p = new Placement(o, f);
		p.establish();

		Assert.assertTrue(f.getRelations(World.getCommandedTopology()).contains(p));
		Assert.assertTrue(o.getRelations(World.getCommandedTopology()).contains(p));

		p.remove();

		Assert.assertFalse(f.getRelations(World.getCommandedTopology()).contains(p));
		Assert.assertFalse(o.getRelations(World.getCommandedTopology()).contains(p));

	}

}
