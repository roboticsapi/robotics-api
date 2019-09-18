/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.RelationshipView;
import org.roboticsapi.core.RoboticsObject.RelationshipListener;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.world.observation.Observation;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.core.world.relation.MeasuredPosition;

/**
 * Concept describing a situation in the world model: The existing
 * {@link Relation}s, along with their current {@link RealtimeTransformation}.
 *
 * {@link FrameTopology}s can describe the two typical flavors of world model
 * views: {@link World#getCommandedTopology()} provides a representation for the
 * the instantaneous situation the control system currently tries to achieve,
 * while {@link World#getMeasuredTopology()} describes the situation that is
 * actually present at the moment (according to sensor data).
 *
 * Further {@link FrameTopology}s can e.g. describe the goal situations of
 * certain {@link Action}s or {@link Command}s by including 'topology changes',
 * i.e. {@link Relation}s that are added or removed in the course of action, and
 * thus allow to reason about the result state.
 */
public abstract class FrameTopology {

	protected final RelationshipView<Relation> relations;

	public RelationshipView<Relation> getRelationshipView() {
		return relations;
	}

	/**
	 * Adds a RelationListener for a given Frame
	 *
	 * @param frame    {@link Frame} to listen for relation chagnes
	 * @param listener listener to be informed
	 */
	public void addRelationListener(Frame frame, RelationListener listener) {
		getRelationshipView().addRelationshipListener(frame, new RelationshipListenerImplementation(frame, listener));
	}

	public void removeRelationListener(Frame frame, RelationListener listener) {
		getRelationshipView().removeRelationshipListener(frame,
				new RelationshipListenerImplementation(frame, listener));
	}

	/**
	 * Retrieves the {@link Relation}s that talk about a given {@link Frame}.
	 *
	 * @param frame {@link Frame} to get the {@link Relation}s for
	 * @return list of {@link Relation}s that go from or to the given {@link Frame}.
	 */
	public final List<Relation> getRelations(Frame frame) {
		return relations.getRelationships(frame);
	}

	/**
	 * Creates a {@link FrameTopology} based on a {@link RelationshipView}.
	 *
	 * @param relations {@link RelationshipView} that describes which
	 *                  {@link Relation}s are present.
	 */
	public FrameTopology(RelationshipView<Relation> relations) {
		this.relations = relations;
	}

	/**
	 * Creates new {@link FrameTopology} that works based on another set of present
	 * {@link Relation}s, but places the existing {@link Frame}s at the same
	 * position and velocity as this {@link FrameTopology}.
	 *
	 * @param relations {@link RelationshipView} that describes which
	 *                  {@link Relation}s are present
	 * @return new {@link FrameTopology} with a different set of contained
	 *         {@link Relation}s.
	 */
	private FrameTopology withRelations(RelationshipView<Relation> relations) {
		return new FrameTopology(relations) {
			@Override
			public RealtimeTwist getRealtimeTwist(Relation relation) {
				return FrameTopology.this.getRealtimeTwist(relation);
			}

			@Override
			public RealtimeTransformation getRealtimeTransformation(Relation relation) {
				return FrameTopology.this.getRealtimeTransformation(relation);
			}
		};
	}

	/**
	 * Creates new {@link FrameTopology} that is specialized to a certain kind of
	 * {@link Relation}s, but otherwise identical to this {@link FrameTopology}.
	 *
	 * @param type type of {@link Relation}s to include
	 * @return new {@link FrameTopology} that only contains {@link Relation}s of the
	 *         given type.
	 */
	public FrameTopology specialized(final Class<? extends Relation> type) {
		return withRelations(relations.hidingRelationships(new Predicate<Relation>() {

			@Override
			public boolean test(Relation r) {
				return !type.isAssignableFrom(r.getClass());
			}
		}));
	}

	/**
	 * Provides the {@link RealtimePose} for a given {@link Relation}
	 *
	 * @param relation {@link Relation} to get the {@link RealtimeTransformation}
	 *                 for
	 * @return {@link RealtimeTransformation} describing the {@link Transformation}
	 *         of the given {@link Relation#getTo()} {@link Frame} relative to the
	 *         {@link Relation#getFrom()} {@link Frame}
	 */
	public abstract RealtimeTransformation getRealtimeTransformation(Relation relation);

	/**
	 * Provides the {@link RealtimeTwist} of the given {@link Relation}
	 *
	 * @param relation {@link Relation} to get the {@link RealtimeTwist} for
	 * @return current speed of the {@link Relation#getTo()} {@link Frame} relative
	 *         to the {@link Relation#getFrom()} {@link Frame}, using
	 *         {@link Relation#getTo()} as pivot {@link Point} and
	 *         {@link Relation#getFrom()} as {@link Orientation}.
	 */
	public abstract RealtimeTwist getRealtimeTwist(Relation relation);

	/**
	 * Creates a new {@link FrameTopology} that only contains {@link Relation}s
	 * where the predicate applies to start and goal
	 *
	 * @param predicate filter for {@link Frame}s in the {@link FrameTopology}
	 * @return new {@link FrameTopology} that only includes {@link Relation}s where
	 *         the given predicate applies to {@link Relation#getFrom()} and
	 *         {@link Relation#getTo()}
	 */
	public FrameTopology withFrameFilter(final Predicate<Frame> predicate) {
		return withRelations(relations.hidingRelationships(new Predicate<Relation>() {
			@Override
			public boolean test(Relation d) {
				return !(predicate.test(d.getFrom()) && predicate.test(d.getTo()));
			}
		}));
	}

	/**
	 * Creates a new {@link FrameTopology} that only contains {@link Relation}s the
	 * predicate applies to
	 *
	 * @param predicate filter for the contained {@link Relation}s
	 * @return new {@link FrameTopology} that only includes {@link Relation}s the
	 *         given predicate applies to
	 */
	public FrameTopology withRelationFilter(final Predicate<Relation> predicate) {
		return withRelations(relations.hidingRelationships(new Predicate<Relation>() {
			@Override
			public boolean test(Relation d) {
				return !predicate.test(d);
			}
		}));
	}

	/**
	 * Creates a new {@link FrameTopology} in which the position and velocity of
	 * certain {@link Relation}s is replaced by the given values.
	 *
	 * @param transformations map describing the desired
	 *                        {@link RealtimeTransformation} for {@link Relation}s
	 *                        to update
	 * @param velocities      map describing the desired {@link RealtimeVelocity}
	 *                        for {@link Relation}s to update
	 * @return new {@link FrameTopology} that includes the same {@link Relation}s,
	 *         however with some {@link Relation}'s position or velocity changed
	 *         according to the given maps
	 */
	public FrameTopology withSubstitution(final Map<Relation, RealtimeTransformation> transformations,
			final Map<Relation, RealtimeTwist> velocities) {

		List<Relation> syntheticRelations = new ArrayList<>();
		for (Relation r : transformations.keySet()) {
			syntheticRelations.add(new MeasuredPosition(r.getFrom(), r.getTo(), transformations.get(r),
					velocities == null ? null : velocities.get(r)));
		}

		return new FrameTopology(relations.addingRelationships(syntheticRelations)) {
			@Override
			public RealtimeTwist getRealtimeTwist(Relation relation) {
				Relation substitutedRelation = findSubstitution(relation);
				if (substitutedRelation != null && velocities.containsKey(substitutedRelation)) {
					return velocities.get(substitutedRelation);
				} else {
					return FrameTopology.this.getRealtimeTwist(relation);
				}
			}

			private Relation findSubstitution(Relation relation) {
				if (transformations != null && transformations.containsKey(relation)) {
					return relation;
				}
				for (Relation r : transformations.keySet()) {
					if (r instanceof LogicalRelation && r.getFrom() == relation.getFrom()
							&& r.getTo() == relation.getTo()) {
						return r;
					}
				}
				return null;
			}

			@Override
			public RealtimeTransformation getRealtimeTransformation(Relation relation) {
				Relation substitutedRelation = findSubstitution(relation);
				if (substitutedRelation != null && transformations.containsKey(substitutedRelation)) {
					return transformations.get(substitutedRelation);
				} else {
					return FrameTopology.this.getRealtimeTransformation(relation);
				}
			}
		};
	}

	public FrameTopology snapshot() {
		return null;
	}

	/**
	 * Creates a new {@link FrameTopology} that is filtered to values available in a
	 * given {@link RoboticsRuntime}.
	 *
	 * @param runtime {@link RoboticsRuntime} to filter the {@link FrameTopology}
	 *                for
	 * @return new {@link FrameTopology} that only contains {@link Relation}s that
	 *         have a value in the given {@link RoboticsRuntime}
	 */
	public FrameTopology forRuntime(final RoboticsRuntime runtime) {
		return withRelationFilter(new Predicate<Relation>() {
			@Override
			public boolean test(Relation d) {
				RealtimeTransformation rr = getRealtimeTransformation(d);
				if (rr != null && rr.getRuntime() != null && runtime != null && rr.getRuntime() != runtime) {
					return false;
				}
				return true;
			}
		});
	}

	/**
	 * Creates a new {@link FrameTopology} that does not include the given list of
	 * {@link Frame}s
	 *
	 * @param frames {@link Frame}s to exclude from the {@link FrameTopology}
	 * @return new {@link FrameTopology} that does not include the given
	 *         {@link Frame}s
	 */
	public FrameTopology without(Frame... frames) {
		return withoutFrames(Arrays.asList(frames));
	}

	/**
	 * Creates a new {@link FrameTopology} that does not include the given list of
	 * {@link Relation}s
	 *
	 * @param relations {@link Relation}s to exclude from the {@link FrameTopology}
	 * @return new {@link FrameTopology} that does not include the given
	 *         {@link Relation}s
	 */
	public FrameTopology without(Relation... relations) {
		return withoutRelations(Arrays.asList(relations));
	}

	/**
	 * Creates a new {@link FrameTopology} that does not include the given list of
	 * {@link Frame}s
	 *
	 * @param frames {@link Frame}s to exclude from the {@link FrameTopology}
	 * @return new {@link FrameTopology} that does not include the given
	 *         {@link Frame}s
	 */
	public FrameTopology withoutFrames(final Collection<? extends Frame> frames) {
		return withFrameFilter(new Predicate<Frame>() {
			@Override
			public boolean test(Frame r) {
				if (frames.contains(r)) {
					return false;
				}
				return true;
			}
		});
	}

	/**
	 * Creates a new {@link FrameTopology} that does not include the given list of
	 * {@link Relation}s
	 *
	 * @param relations {@link Relation}s to exclude from the {@link FrameTopology}
	 * @return new {@link FrameTopology} that does not include the given
	 *         {@link Relation}s
	 */
	public FrameTopology withoutRelations(final Collection<? extends Relation> relations) {
		return withRelationFilter(new Predicate<Relation>() {
			@Override
			public boolean test(Relation r) {
				if (relations.contains(r)) {
					return false;
				}
				return true;
			}
		});
	}

	/**
	 * Creates a new {@link FrameTopology} without dynamic {@link Relation}s. For
	 * {@link GeometricRelation}, {@link GeometricRelation#isConstant()} has to be
	 * <code>true</code>, while for {@link LogicalRelation}
	 * {@link LogicalRelation#isVariable()} has to be <code>false</code>
	 *
	 * @return new {@link FrameTopology} that does not include dynamic
	 *         {@link Relation}s.
	 */
	public FrameTopology withoutDynamic() {
		return withRelationFilter(new Predicate<Relation>() {
			@Override
			public boolean test(Relation r) {
				if (r instanceof GeometricRelation && !((GeometricRelation) r).isConstant()) {
					return false;
				}
				if (r instanceof LogicalRelation && ((LogicalRelation) r).isVariable()) {
					return false;
				}
				if (r instanceof Observation) {
					return false;
				}
				return true;
			}
		});
	}

	/**
	 * Creates a new {@link FrameTopology} that excludes values that belong to the
	 * given {@link RoboticsRuntime}.
	 *
	 * @param runtime {@link RoboticsRuntime} to filter the {@link FrameTopology}
	 *                for
	 * @return new {@link FrameTopology} that only contains {@link Relation}s that
	 *         do not belong to the given {@link RoboticsRuntime}
	 */
	public FrameTopology withoutRuntime(final RoboticsRuntime runtime) {
		return withRelationFilter(new Predicate<Relation>() {
			@Override
			public boolean test(Relation d) {
				RoboticsRuntime rr = getRealtimeTransformation(d).getRuntime();
				if (rr == runtime) {
					return false;
				}
				return true;
			}
		});
	}

	private final class RelationshipListenerImplementation implements RelationshipListener<Relation> {

		private final Frame frame;
		private final RelationListener listener;

		public RelationshipListenerImplementation(Frame frame, RelationListener listener) {
			this.frame = frame;
			this.listener = listener;
		}

		@Override
		public void onRemoved(Relation r) {
			listener.relationRemoved(r, r.getOther(frame));
		}

		@Override
		public void onAdded(Relation r) {
			listener.relationAdded(r, r.getOther(frame));
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj.getClass() == getClass()
					&& frame.equals(((RelationshipListenerImplementation) obj).frame)
					&& listener.equals(((RelationshipListenerImplementation) obj).listener);
		}
	}

	/**
	 * Creates a {@link FrameTopology} that serves as a lazy snapshot, i.e. provides
	 * a well-formed set of {@link Relation}s that is not necessarily temporally
	 * consistent (see {@link RelationshipView#lazySnapshot()}), but rather provides
	 * the {@link Transformation} from the time the corresponding {@link Relation}
	 * is requested for the first time.
	 *
	 * @return lazy snapshot {@link FrameTopology}
	 */
	public FrameTopology lazySnapshot() {
		return new FrameTopology(relations.lazySnapshot()) {
			private final Map<Relation, RealtimeTwist> velocityCache = new HashMap<>();
			private final Map<Relation, RealtimeTransformation> poseCache = new HashMap<>();

			@Override
			public RealtimeTwist getRealtimeTwist(Relation relation) {
				if (velocityCache.containsKey(relation)) {
					return velocityCache.get(relation);
				}
				try {
					RealtimeTwist vel = FrameTopology.this.getRealtimeTwist(relation);
					if (vel != null) {
						velocityCache.put(relation, RealtimeTwist.createFromConstant(vel.getCurrentValue()));
						return velocityCache.get(relation);
					}
				} catch (RealtimeValueReadException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public RealtimeTransformation getRealtimeTransformation(Relation relation) {
				if (poseCache.containsKey(relation)) {
					return poseCache.get(relation);
				}
				try {
					RealtimeTransformation pose = FrameTopology.this.getRealtimeTransformation(relation);
					if (pose != null) {
						poseCache.put(relation, RealtimeTransformation.createfromConstant(pose.getCurrentValue()));
						return poseCache.get(relation);
					}
				} catch (RealtimeValueReadException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}

	// private static class SnapshotFrameTopology extends FrameTopology {
	//
	// Map<GeometricRelation, RealtimePose> poses;
	// Map<GeometricRelation, RealtimePose> velocities;
	//
	// public SnapshotFrameTopology(RoboticsObjectView<RoboticsObject>
	// objectView,
	// RelationshipView<Relation> relationView) {
	// super(relationView);
	//
	// Set<Relation> relations = new HashSet<>();
	// Set<RoboticsObject> objects = objectView.getObjects();
	//
	// for (RoboticsObject roboticsObject : objects) {
	// List<Relation> list = relationView.getRelationships(roboticsObject);
	// relations.addAll(list);
	// }
	//
	// }
	//
	// @Override
	// public RealtimePose getRealtimePose(Relation relation) {
	// return poses.get(relation);
	// }
	//
	// @Override
	// public RealtimeVelocity getVelocity(Relation relation) {
	// return null;
	// }
	//
	// }

}
