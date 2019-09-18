/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.RelationshipChangeSet;
import org.roboticsapi.core.RelationshipView;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.CommandedPosition;
import org.roboticsapi.core.world.relation.MeasuredPosition;

/**
 * The Robotics API world model world (distinguished entity object)
 */
public class World extends AbstractPhysicalObject {

	/** world origin */
	private final Dependency<WorldOrigin> origin;

	/**
	 * Creates a new world ;)
	 */
	public World() {
		origin = createDependency("origin", new WorldOrigin());
	}

	/**
	 * Retrieves the 'commanded' {@link FrameTopology}. 'commanded' means a
	 * representation for the the instantaneous situation the control system
	 * currently tries to achieve.
	 *
	 * @see #getMeasuredTopology()
	 *
	 * @return the 'commanded' {@link FrameTopology}
	 */
	public static FrameTopology getCommandedTopology() {
		return getCommandedTopology(RelationshipChangeSet.getUnmodified());
	}

	/**
	 * Retrieves the 'commanded' {@link FrameTopology} for the {@link Relation}s
	 * present in a given {@link RelationshipChangeSet}. 'commanded' means a
	 * representation for the the instantaneous situation the control system
	 * currently tries to achieve.
	 *
	 * @param situation {@link Relation}s that should be available in the created
	 *                  {@link FrameTopology}
	 *
	 * @return the 'commanded' {@link FrameTopology} for the given
	 *         {@link RelationshipChangeSet}
	 */
	public static FrameTopology getCommandedTopology(RelationshipChangeSet situation) {
		return new FrameTopology(RelationshipView.get(situation).specializingInRelationships(Relation.class)
				.preferringRelationships(CommandedPosition.class, MeasuredPosition.class)) {
			@Override
			public RealtimeTwist getRealtimeTwist(Relation relation) {
				if (relation instanceof GeometricRelation) {
					return ((GeometricRelation) relation).getTwist();
				} else {
					return null;
				}
			}

			@Override
			public RealtimeTransformation getRealtimeTransformation(Relation relation) {
				if (relation instanceof GeometricRelation
						&& ((GeometricRelation) relation).getTransformation() != null) {
					return ((GeometricRelation) relation).getTransformation();
				} else {
					return null;
				}
			}
		};
	}

	/**
	 * Retrieves the 'measured' {@link FrameTopology}. 'measured' means the
	 * situation that is actually present at the moment (according to sensor data).
	 *
	 * @see #getCommandedTopology()
	 *
	 * @return the 'measured' {@link FrameTopology}
	 */
	public static FrameTopology getMeasuredTopology() {
		return getMeasuredTopology(RelationshipChangeSet.getUnmodified());
	}

	/**
	 * Retrieves the 'commanded' {@link FrameTopology} for the {@link Relation}s
	 * present in a given {@link RelationshipChangeSet}. 'measured' means the
	 * situation that is actually present at the moment (according to sensor data).
	 *
	 * @param situation {@link Relation}s that should be available in the created
	 *                  {@link FrameTopology}
	 *
	 * @return the 'measured' {@link FrameTopology} for the given
	 *         {@link RelationshipChangeSet}
	 */
	public static FrameTopology getMeasuredTopology(RelationshipChangeSet situation) {
		return new FrameTopology(RelationshipView.get(situation).specializingInRelationships(Relation.class)
				.preferringRelationships(MeasuredPosition.class, CommandedPosition.class)) {
			@Override
			public RealtimeTwist getRealtimeTwist(Relation relation) {
				if (relation instanceof GeometricRelation) {
					return ((GeometricRelation) relation).getTwist();
				} else {
					return null;
				}
			}

			@Override
			public RealtimeTransformation getRealtimeTransformation(Relation relation) {
				if (relation instanceof GeometricRelation
						&& ((GeometricRelation) relation).getTransformation() != null) {
					return ((GeometricRelation) relation).getTransformation();
				} else {
					return null;
				}
			}
		};
	}

	/**
	 * Retrieves the world origin
	 *
	 * @return world origin
	 */
	public WorldOrigin getOrigin() {
		return origin.get();
	}

}
