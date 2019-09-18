/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.estimation.ObservationManager.ObservationListener;
import org.roboticsapi.core.world.relation.Placement;
import org.roboticsapi.core.world.relation.StaticPosition;

public class ObservationManagerTest {

	@Test
	public void testRelationAddedAndRemoved() throws InitializationException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		Placement p1 = new Placement(f1, f2);
		StaticPosition sp = new StaticPosition(f1, f2, Transformation.IDENTITY);

		ObservationManager om = new ObservationManager(f1, World.getCommandedTopology());
		om.addListener(Mockito.mock(ObservationListener.class));

		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		p1.establish();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList(p1));
		sp.establish();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		sp.remove();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList(p1));
		p1.remove();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());

	}

	@Test
	public void testReverseStaticPosition() throws InitializationException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		Placement p1 = new Placement(f1, f2);
		StaticPosition sp = new StaticPosition(f2, f1, Transformation.IDENTITY);

		ObservationManager om = new ObservationManager(f1, World.getCommandedTopology());
		om.addListener(Mockito.mock(ObservationListener.class));

		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		p1.establish();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList(p1));
		sp.establish();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		sp.remove();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList(p1));
		p1.remove();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());

	}

	@Test
	public void testReverseOrder() throws InitializationException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		Placement p1 = new Placement(f1, f2);
		StaticPosition sp = new StaticPosition(f1, f2, Transformation.IDENTITY);

		ObservationManager om = new ObservationManager(f1, World.getCommandedTopology());
		om.addListener(Mockito.mock(ObservationListener.class));

		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		sp.establish();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		p1.establish();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());
		sp.remove();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList(p1));
		p1.remove();
		assertEquals(om.getUnresolvedLogicalRelations(), Arrays.asList());

	}

	// @Test
	public void testAddAndRemoveListener() throws InitializationException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");
		Placement p1 = new Placement(f1, f2);
		StaticPosition sp = new StaticPosition(f1, f2, Transformation.IDENTITY);

		ObservationListener listener = Mockito.mock(ObservationListener.class);
		ObservationManager om = new ObservationManager(f1, World.getCommandedTopology());
		om.addListener(listener);

		p1.establish();
		Mockito.verify(listener).logicalRelationAdded(p1);
		sp.establish();
		Mockito.verify(listener).logicalRelationRemoved(p1);
		sp.remove();
		Mockito.verify(listener).logicalRelationAdded(p1);
		p1.remove();
		Mockito.verify(listener).logicalRelationRemoved(p1);

		om.removeListener(listener);
	}

}
