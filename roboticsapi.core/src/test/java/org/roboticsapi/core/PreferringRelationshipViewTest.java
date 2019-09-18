/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsObject.RelationshipListener;

public class PreferringRelationshipViewTest {

	private RelationshipView<Relationship> view;
	private RoboticsObject first;
	private RoboticsObject second;
	private NeutralRelationship n1, n2;
	private PreferredRelationship p1, p2;
	private DislikedRelationship d1, d2;
	private final List<Relationship> listenerList = new ArrayList<>();

	public class PreferredRelationship extends AbstractRelationship<RoboticsObject, RoboticsObject> {
		public PreferredRelationship(RoboticsObject from, RoboticsObject to) {
			super(from, to);
		}

		@Override
		public boolean canEstablish(RelationshipChangeSet situation) {
			return true;
		}

		@Override
		public boolean canRemove(RelationshipChangeSet situation) {
			return true;
		}
	}

	public class NeutralRelationship extends AbstractRelationship<RoboticsObject, RoboticsObject> {
		public NeutralRelationship(RoboticsObject from, RoboticsObject to) {
			super(from, to);
		}

		@Override
		public boolean canEstablish(RelationshipChangeSet situation) {
			return true;
		}

		@Override
		public boolean canRemove(RelationshipChangeSet situation) {
			return true;
		}
	}

	public class DislikedRelationship extends AbstractRelationship<RoboticsObject, RoboticsObject> {
		public DislikedRelationship(RoboticsObject from, RoboticsObject to) {
			super(from, to);
		}

		@Override
		public boolean canEstablish(RelationshipChangeSet situation) {
			return true;
		}

		@Override
		public boolean canRemove(RelationshipChangeSet situation) {
			return true;
		}
	}

	@Before
	public void setup() {
		view = RelationshipView.getUnmodified().preferringRelationships(PreferredRelationship.class,
				DislikedRelationship.class);

		first = new AbstractRoboticsObject() {
		};
		second = new AbstractRoboticsObject() {
		};

		n1 = new NeutralRelationship(first, second);
		n2 = new NeutralRelationship(first, second);

		p1 = new PreferredRelationship(first, second);
		p2 = new PreferredRelationship(first, second);

		d1 = new DislikedRelationship(first, second);
		d2 = new DislikedRelationship(first, second);

		view.addRelationshipListener(first, new RelationshipListener<Relationship>() {
			@Override
			public void onRemoved(Relationship r) {
				listenerList.remove(r);
			}

			@Override
			public void onAdded(Relationship r) {
				listenerList.add(r);
			}
		});
	}

	@Test
	public void testRelationshipViewPreferringNPDN() {
		checkSeen(0, 0, 0);
		n1.establish();
		checkSeen(0, 0, 1);
		p1.establish();
		checkSeen(1, 0, 1);
		d1.establish();
		checkSeen(1, 1, 1);
		n2.establish();
		checkSeen(1, 1, 2);
		n2.remove();
		checkSeen(1, 1, 1);
		d1.remove();
		checkSeen(1, 0, 1);
		n1.remove();
		checkSeen(1, 0, 0);
		p1.remove();
		checkSeen(0, 0, 0);
	}

	@Test
	public void testRelationshipViewPreferringDNP() {
		checkSeen(0, 0, 0);
		d1.establish();
		checkSeen(0, 1, 0);
		n1.establish();
		checkSeen(0, 1, 1);
		p1.establish();
		checkSeen(1, 1, 1);
		d1.remove();
		checkSeen(1, 0, 1);
		n1.remove();
		checkSeen(1, 0, 0);
		p1.remove();
		checkSeen(0, 0, 0);
	}

	@Test
	public void testRelationshipViewPreferringDPDP() {
		checkSeen(0, 0, 0);
		d1.establish();
		checkSeen(0, 1, 0);
		p1.establish();
		checkSeen(1, 1, 0);
		d2.establish();
		checkSeen(1, 2, 0);
		p2.establish();
		checkSeen(2, 2, 0);
		d1.remove();
		checkSeen(2, 1, 0);
		p1.remove();
		checkSeen(1, 1, 0);
		p2.remove();
		checkSeen(0, 1, 0);
		d2.remove();
		checkSeen(0, 0, 0);
	}

	@Test
	public void testRelationshipViewPreferringDPDPdpDP() {
		checkSeen(0, 0, 0);
		d1.establish();
		checkSeen(0, 1, 0);
		p1.establish();
		checkSeen(1, 1, 0);
		d2.establish();
		checkSeen(1, 2, 0);
		p2.establish();
		checkSeen(2, 2, 0);
		d1.remove();
		checkSeen(2, 1, 0);
		p1.remove();
		checkSeen(1, 1, 0);
		d1.establish();
		checkSeen(1, 2, 0);
		p1.establish();
		checkSeen(2, 2, 0);
		p2.remove();
		checkSeen(1, 2, 0);
		d2.remove();
		checkSeen(1, 1, 0);
	}

	private void checkSeen(int preferred, int disliked, int other) {
		checkSeen(view.getRelationships(first), "[getRelationships]", preferred, disliked, other);
		checkSeen(listenerList, "[addRelationshipListener]", preferred, disliked, other);
	}

	private void checkSeen(List<Relationship> relationships, String scope, int expectPreferred, int expectDisliked,
			int expectOther) {
		int preferredSeen = 0, dislikedSeen = 0, otherSeen = 0;
		for (Relationship r : relationships) {
			if (r instanceof PreferredRelationship && dislikedSeen > 0) {
				Assert.fail(scope + " We do not expect to see a disliked relationship before a preferred: "
						+ relationships);
			} else if (r instanceof PreferredRelationship) {
				preferredSeen++;
			} else if (r instanceof DislikedRelationship) {
				dislikedSeen++;
			} else {
				otherSeen++;
			}
		}
		Assert.assertEquals(scope + " Number of preferred relationships", expectPreferred, preferredSeen);
		Assert.assertEquals(scope + " Number of disliked relationships", expectDisliked, dislikedSeen);
		Assert.assertEquals(scope + " Number of other relationships", expectOther, otherSeen);
	}

}
