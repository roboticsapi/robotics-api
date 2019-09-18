/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.roboticsapi.core.AbstractRoboticsEntity;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RelationshipChangeSet;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.observation.Observation;
import org.roboticsapi.core.world.realtimevalue.RealtimeOrientation;
import org.roboticsapi.core.world.realtimevalue.RealtimePoint;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * A Robotics API world model Frame (named point with orientation in space)
 */
public class Frame extends AbstractRoboticsEntity implements RoboticsEntity {

	private final Map<Device, List<TeachingInfo>> teachingInfos = new HashMap<Device, List<TeachingInfo>>();
	private final List<RelationListener> relationListeners = new ArrayList<RelationListener>();

	/**
	 * Creates a temporary, unnamed Frame.
	 */
	public Frame() {
		addRelationshipListener(Relation.class, new RelationshipListener<Relation>() {
			@Override
			public void onAdded(Relation r) {
				notifyRelationAdded(r, r.getOther(Frame.this));
			}

			@Override
			public void onRemoved(Relation r) {
				notifyRelationRemoved(r, r.getOther(Frame.this));
			}
		});
	}

	/**
	 * Creates a temporary, named Frame.
	 *
	 * @param name the Frame's name
	 */
	public Frame(String name) {
		this();
		setName(name);
	}

	/**
	 * Retrieves the relations of this frame
	 *
	 * @return read-only list of relations
	 */
	@Deprecated
	protected List<Relation> getRelations() {
		return getRelationships(Relation.class);
	}

	protected void notifyRelationAdded(final Relation relation, Frame to) {
		for (RelationListener l : new ArrayList<RelationListener>(relationListeners)) {
			try {
				l.relationAdded(relation, to);
			} catch (Exception e) {
				RAPILogger.getLogger(this).log(Level.WARNING,
						e.getClass().getCanonicalName() + " in RelationListener " + l + ": " + e.getMessage(), e);
			}
		}
	}

	protected void notifyRelationRemoved(final Relation relation, Frame to) {
		for (RelationListener l : new ArrayList<RelationListener>(relationListeners)) {
			try {
				l.relationRemoved(relation, to);
			} catch (Exception e) {
				RAPILogger.getLogger(this).log(Level.WARNING,
						e.getClass().getCanonicalName() + " in RelationListener " + l + ": " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Adds a {@link RelationListener} to this Frame which listens for any changes
	 * in the Relations of this Frame.
	 *
	 * @param listener the listener to add
	 */
	public void addRelationListener(RelationListener listener) {
		if (!relationListeners.contains(listener)) {
			relationListeners.add(listener);
		}
	}

	/**
	 * Removes a previously added {@link} RelationListener}.
	 *
	 * @param listener the listener to remove
	 */
	public void removeRelationListener(RelationListener listener) {
		relationListeners.remove(listener);
	}

	/**
	 * Augments this Frame with information about the exact pose used when it has
	 * been taught. This information may be used by path planning when planning
	 * motions with this Frame as target.
	 *
	 * @param device         the device that this Frame has been taught with
	 * @param motionCenter   the Motion Center Point that has been used for teaching
	 * @param hintParameters list of joint angles representing the pose during
	 *                       teaching
	 */
	public void addTeachingInfo(Device device, Pose goal, Pose motionCenter, double[] hintParameters) {
		addTeachingInfo(new TeachingInfo(device, goal, motionCenter, hintParameters));
	}

	/**
	 * Augments this Frame with information about the exact pose used when it has
	 * been taught. This information may be used by path planning when planning
	 * motions with this Frame as target.
	 *
	 * @param teachingInfo data about the pose used when teaching this Frame
	 */
	public void addTeachingInfo(TeachingInfo teachingInfo) {
		if (teachingInfos.containsKey(teachingInfo.getDevice())) {
			teachingInfos.get(teachingInfo.getDevice()).add(teachingInfo);
		} else {
			List<TeachingInfo> list = new ArrayList<TeachingInfo>();
			list.add(teachingInfo);
			teachingInfos.put(teachingInfo.getDevice(), list);
		}
	}

	/**
	 * Removes teaching information previously added to this Frame.
	 *
	 * @param info the teaching information to remove
	 */
	public void removeTeachingInfo(TeachingInfo info) {
		if (teachingInfos.containsKey(info.getDevice())) {
			for (TeachingInfo ti : new ArrayList<TeachingInfo>(teachingInfos.get(info.getDevice()))) {
				if (ti.getMotionCenter().equals(info.getMotionCenter())
						&& ti.getHintParameters().length == info.getHintParameters().length) {
					boolean equals = true;

					for (int i = 0; i < ti.getHintParameters().length; i++) {
						if (ti.getHintParameters()[i] != info.getHintParameters()[i]) {
							equals = false;
							break;
						}
					}

					if (equals) {
						teachingInfos.get(info.getDevice()).remove(ti);
					}
				}
			}

			if (teachingInfos.get(info.getDevice()).size() == 0) {
				teachingInfos.remove(info.getDevice());
			}
		}
	}

	/**
	 * Removes all teaching information regarding a certain Device from this Frame.
	 *
	 * @param device the Device for which to remove teaching information
	 */
	public void clearTeachingInfo(Device device) {
		teachingInfos.remove(device);
	}

	/**
	 * Removes all teaching information from this Frame.
	 *
	 */
	public void clearAllTeachingInfo() {
		teachingInfos.clear();
	}

	/**
	 * Retrieves teaching information from this Frame for a given Device and Motion
	 * Center Point, if any present.
	 *
	 * @param device       the device for which teaching information should be
	 *                     retrieved
	 * @param motionCenter the Motion Center Point for which teaching information
	 *                     should be retrieved
	 * @return teaching information, if any for this Device and Motion Center Point
	 */
	public TeachingInfo getTeachingInfo(Device device, Pose goal, Pose motionCenter) {
		if (teachingInfos.containsKey(device)) {
			for (TeachingInfo ti : teachingInfos.get(device)) {
				if (ti.getMotionCenter().equals(motionCenter) && ti.getGoal().equals(goal)) {
					return ti;
				}
			}
		}
		return null;
	}

	/**
	 * Retrieves teaching information from this Frame for a given Device and Motion
	 * Center Point, if any present.
	 *
	 * @param device the device for which teaching information should be retrieved
	 * @return teaching informations, if any for this Device
	 */
	public List<TeachingInfo> getTeachingInfo(Device device) {
		return teachingInfos.get(device);
	}

	/**
	 * Retrieves all teaching information stored for this Frame.
	 *
	 * @return all teaching information of this Frame
	 */
	public Map<Device, List<TeachingInfo>> getAllTeachingInfos() {
		return teachingInfos;
	}

	/**
	 * Indicates whether this Frame is deletable.
	 *
	 * @return true, if Frame may be deleted, false otherwise
	 */
	public boolean isDeletable() {
		for (Relation r : getRelations()) {
			if (r.canRemove(RelationshipChangeSet.getUnmodified())) {
				continue;
			}
			return false;
		}
		return true;
	}

	/**
	 * Indicates whether this Frame is a temporary Frame (which might be deleted
	 * automatically when it is not used anymore).
	 *
	 * @return true, if this Frame is temporary, false otherwise
	 */
	public boolean isTemporary() {
		return !isInitialized();
	}

	private List<Relation> getCompatibleRelationsTo(final Frame to, FrameTopology topology) {
		while (true) {
			List<Relation> way = getRelationsTo(to, topology);
			if (way == null) {
				return null;
			}

			for (Relation r : way) {
				final RoboticsRuntime rr = topology.getRealtimeTransformation(r).getRuntime();
				if (rr != null) {
					List<Relation> ret = getRelationsTo(to, topology.forRuntime(rr));
					if (ret != null) {
						return ret;
					}
					topology = topology.withoutRuntime(rr);
					break;
				}
			}
			return way;
		}
	}

	/**
	 * Retrieves the commanded {@link RealtimeTransformation} of another
	 * {@link Frame} relative to this {@link Frame}. Same as
	 * getCommandedRealtimeTransformationTo(Frame to).
	 *
	 * @param to destination frame
	 * @return {@link RealtimeTransformation} of the other {@link Frame} relative to
	 *         this {@link Frame}, or null if the other {@link Frame} is not
	 *         connected.
	 */
	public RealtimeTransformation getRealtimeTransformationTo(final Frame to) {
		return getCommandedRealtimeTransformationTo(to);
	}

	/**
	 * Retrieves the commanded {@link RealtimeTransformation} of another
	 * {@link Frame} relative to this {@link Frame}.
	 *
	 * Uses World.getCommandedTopology() to determine the transformation.
	 *
	 * @param to destination frame
	 * @return {@link RealtimeTransformation} of the other {@link Frame} relative to
	 *         this {@link Frame}, or null if the other {@link Frame} is not
	 *         connected.
	 */
	public RealtimeTransformation getCommandedRealtimeTransformationTo(final Frame to) {
		return getRealtimeTransformationTo(to, World.getCommandedTopology());
	}

	/**
	 * Retrieves the commanded {@link RealtimeTransformation} of another
	 * {@link Frame} relative to this {@link Frame}.
	 *
	 * Uses World.getMeasuredTopology() to determine the transformation.
	 *
	 * @param to destination frame
	 * @return {@link RealtimeTransformation} of the other {@link Frame} relative to
	 *         this {@link Frame}, or null if the other {@link Frame} is not
	 *         connected.
	 */
	public RealtimeTransformation getMeasuredRealtimeTransformationTo(final Frame to) {
		return getRealtimeTransformationTo(to, World.getMeasuredTopology());
	}

	/**
	 * Retrieves the {@link RealtimeTransformation} of another {@link Frame}
	 * relative to this {@link Frame}.
	 *
	 * @param to       destination frame
	 * @param topology frame topology to use
	 * @return {@link RealtimeTransformation} of the other {@link Frame} relative to
	 *         this {@link Frame}, or null if the other {@link Frame} is not
	 *         connected.
	 */
	public RealtimeTransformation getRealtimeTransformationTo(final Frame to, final FrameTopology topology) {
		List<Relation> way = getCompatibleRelationsTo(to, topology.withRelationFilter(new Predicate<Relation>() {

			@Override
			public boolean test(Relation d) {
				return topology.getRealtimeTransformation(d) != null;
			}
		}));

		// check if we found a way between the two frames
		if (way != null) {
			// case: this == to
			if (way.isEmpty()) {
				return RealtimeTransformation.createfromConstant(Transformation.IDENTITY);
			}

			RealtimeTransformation ret = null;
			Frame cur = this;

			for (Relation curRel : way) {
				cur = curRel.getOther(cur);
				RealtimeTransformation toCur;

				// check relation map
				toCur = topology.getRealtimeTransformation(curRel);
				if (curRel.getTo() != cur) {
					toCur = toCur.invert();
				}

				if (ret == null) {
					ret = toCur;
				} else {
					ret = ret.multiply(toCur);
				}
			}
			return ret;
		}
		return null;
	}

	/**
	 * Gets a {@link RealtimeTwist} that measures the current commanded velocity of
	 * the given {@link Frame} relative to this {@link Frame}.
	 *
	 * The given {@link Frame}'s {@link Point} is used as pivot point, while this
	 * {@link Frame}'s {@link Orientation} is used as the velocity
	 * {@link Orientation} (see {@link RealtimeVelocity}).
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * goal values are considered instead of actual, measured values. This method
	 * uses World.getCommandedTopology().
	 *
	 * @param moving the moving {@link Frame}
	 * @return a {@link RealtimeVelocity} measuring moving's velocity
	 */
	public RealtimeTwist getCommandedRealtimeTwistOf(final Frame moving) {
		return getRealtimeTwistOf(moving, World.getCommandedTopology());
	}

	/**
	 * Gets a {@link RealtimeTwist} that measures the current measured velocity of
	 * the given {@link Frame} relative to this {@link Frame}.
	 *
	 * The given {@link Frame}'s {@link Point} is used as pivot point, while this
	 * {@link Frame}'s {@link Orientation} is used as the velocity
	 * {@link Orientation} (see {@link RealtimeVelocity}).
	 *
	 * 'Measured' means that, in case of dynamic relations like a robot kinematics,
	 * goal values are considered instead of actual, measured values. This method
	 * uses World.getMeasuredTopology().
	 *
	 * @param moving the moving {@link Frame}
	 * @return a {@link RealtimeVelocity} measuring moving's velocity
	 */
	public RealtimeTwist getMeasuredRealtimeTwistOf(final Frame moving) {
		return getRealtimeTwistOf(moving, World.getMeasuredTopology());
	}

	/**
	 * Gets a {@link RealtimeTwist} that measures the velocity of the given
	 * {@link Frame} relative to this {@link Frame} in the given
	 * {@link FrameTopology}.
	 *
	 * The given {@link Frame}'s {@link Point} is used as pivot point, while this
	 * {@link Frame}'s {@link Orientation} is used as the velocity
	 * {@link Orientation} (see {@link RealtimeVelocity}).
	 *
	 * @param moving   the moving {@link Frame}
	 * @param topology {@link FrameTopology} to use
	 * @return a {@link RealtimeTwist} measuring moving's {@link Frame}'s velocity
	 * @throws TransformationException
	 */
	public RealtimeTwist getRealtimeTwistOf(final Frame moving, FrameTopology topology) {
		List<Relation> way = getCompatibleRelationsTo(moving, topology.specialized(GeometricRelation.class));

		// check if we found a way between the two frames
		if (way != null) {
			// case: this == to
			if (way.isEmpty()) {
				return RealtimeTwist.createFromConstant(new Twist());
			}

			RealtimeTwist ret = null;
			Frame cur = this;

			for (Relation r : way) {
				Frame from = cur;
				cur = r.getOther(cur);

				RealtimeTwist toCur = topology.getRealtimeTwist(r);
				if (r.getTo() != cur) {
					toCur = toCur.invert();
				}
				if (toCur == null) {
					return null;
				}
				try {
					RealtimePoint point = RealtimePoint.createFromVector(null, cur.asRealtimePose()
							.getRealtimeTransformationTo(moving.asRealtimePose(), topology).getTranslation());
					toCur = RealtimeVelocity.createFromTwist(from, null, from.asOrientation(), toCur)
							.getTwistForRepresentation(asRealtimeOrientation(), point, cur.asRealtimePose(), topology);
				} catch (TransformationException e) {

				}

				if (ret == null) {
					ret = toCur;
				} else {
					ret = ret.add(toCur);
				}
			}

			return ret;
		}
		return null;
	}

	/**
	 * Collects all {@link Frame}s connected to this {@link Frame} (including ones
	 * that are connected through a path of multiple {@link Relation}s) in the
	 * 'commanded' {@link FrameTopology}.
	 *
	 * @return the connected {@link Frame}s
	 */
	public List<Frame> getConnectedFrames() {
		return getConnectedFrames(World.getCommandedTopology());
	}

	/**
	 * Collects all {@link Frame}s connected to this {@link Frame} (including ones
	 * that are connected through a path of multiple {@link Relation}s) in the given
	 * {@link FrameTopology}.
	 *
	 * @return the connected {@link Frame}s
	 */
	public List<Frame> getConnectedFrames(FrameTopology topology) {
		List<Frame> todoList = new LinkedList<Frame>();
		List<Frame> ret = new LinkedList<Frame>();

		todoList.add(this);
		// dummy add to prevent it from being added later
		ret.add(this);

		while (!todoList.isEmpty()) {
			for (Frame f : new LinkedList<Frame>(todoList)) {

				todoList.remove(f);

				for (Relation r : topology.getRelations(f)) {
					Frame other = r.getOther(f);

					if (other == null) {
						continue;
					}

					if (!ret.contains(other)) {
						ret.add(other);
						todoList.add(other);
					}
				}
			}
		}

		ret.remove(this);
		return ret;
	}

	/**
	 * Retrieves the shortest relation path to another frame, using the 'commanded'
	 * {@link FrameTopology}.
	 *
	 * @param to Destination frame
	 * @return List of relations on the way to the given frame, or <code>null</code>
	 *         if none found
	 */
	public List<Relation> getRelationsTo(final Frame to) {
		return getRelationsTo(to, World.getCommandedTopology());
	}

	/**
	 * Retrieves the shortest relation path to another frame.
	 *
	 * @param to            Destination frame
	 * @param FrameTopology {@link FrameTopology} deciding which {@link Relation}s
	 *                      exist
	 * @return List of relations on the way to the given frame, or <code>null</code>
	 *         if none found
	 */
	public List<Relation> getRelationsTo(final Frame to, FrameTopology topology) {
		List<Relation> ret = new LinkedList<Relation>();

		// Identity transformation?
		if (this == to) {
			return ret;
		}

		// dictionary keeping the relation leading to (forward) or from
		// (backward) a given frame (also encodes "seen" information)
		final Hashtable<Frame, Relation> wayTo = new Hashtable<Frame, Relation>(),
				wayFrom = new Hashtable<Frame, Relation>();

		// queue of frames yet to scan
		final Queue<Frame> forwardList = new LinkedList<Frame>(), backwardList = new LinkedList<Frame>();
		forwardList.add(this);
		backwardList.add(to);

		// linking frame between the two of them
		Frame link = null;

		// while the queue is not empty
		while (!forwardList.isEmpty() || !backwardList.isEmpty()) {
			// forward search
			if (!forwardList.isEmpty()) {
				// process the first queue element
				final Frame currentFrame = forwardList.remove();

				for (final Relation relation : topology.getRelations(currentFrame)) {
					// scan all relations from that element
					final Frame other = relation.getOther(currentFrame);

					if (other == null) {
						continue;
					}

					if (other == to || other.getRelations().size() > 1) {
						// has their destination already been found?
						if (!wayTo.containsKey(other) && other != this) {
							// no -> store the relation leading to it, and
							// enqueue the frame for further processing
							wayTo.put(other, relation);
							forwardList.add(other);
						}

						// has the destination been found?
						if (wayFrom.containsKey(other) || other == to) {
							link = other;
							break;
						}
					}
				}
			}

			// backward search
			if (!backwardList.isEmpty()) {
				// backward part
				final Frame currentFrame = backwardList.remove();

				for (final Relation relation : topology.getRelations(currentFrame)) {
					// scan all relations from that element
					final Frame other = relation.getOther(currentFrame);

					if (other == this || topology.getRelations(other).size() > 1) {
						// has their destination already been found?
						if (!wayFrom.containsKey(other) && other != to) {
							// no -> store the relation leading to it, and
							// enqueue the frame for further processing
							wayFrom.put(other, relation);
							if (other.getRelations().size() > 1) {
								backwardList.add(other);
							}
						}

						// has the destination been found?
						if (wayTo.containsKey(other) || other == this) {
							link = other;
							break;
						}
					}
				}
			}
			// found a linking frame between forward and backward search
			if (link != null) {
				// collect relations from source to link (using wayTo)
				Frame cur = link;
				while (cur != this) {
					final Relation curRel = wayTo.get(cur);
					ret.add(0, curRel);
					// following the (backwards) relations stored in wayTo from
					// target to source
					cur = curRel.getFrom() == cur ? curRel.getTo() : curRel.getFrom();
				}
				// collect relations from link to destination (using wayFrom)
				cur = link;
				while (cur != to) {
					final Relation curRel = wayFrom.get(cur);
					ret.add(curRel);
					// following the (backwards) relations stored in wayto from
					// target to source
					cur = curRel.getFrom() == cur ? curRel.getTo() : curRel.getFrom();
				}

				// and return them
				return ret;
			}
		}
		// no way way found.
		return null;
	}

	/**
	 * Retrieves {@link Transformation} of another {@link Frame} relative to this
	 * {@link Frame}, according to the 'commanded' topology. Same as
	 * getCommandedTransformationTo(Frame to).
	 *
	 * @param to Destination frame
	 * @return current commanded {@link Transformation} of the given {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getTransformationTo(final Frame to) throws TransformationException {
		return getCommandedTransformationTo(to);
	}

	/**
	 * Retrieves {@link Transformation} of another {@link Frame} relative to this
	 * {@link Frame}, according to the 'commanded' topology.
	 *
	 * Uses World.getCommandedTopology() to determine the transformation.
	 *
	 * @param to Destination frame
	 * @return current commanded {@link Transformation} of the given {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getCommandedTransformationTo(final Frame to) throws TransformationException {
		return getTransformationTo(to, World.getCommandedTopology());
	}

	/**
	 * Retrieves {@link Transformation} of another {@link Frame} relative to this
	 * {@link Frame}, according to the 'measured' topology.
	 *
	 * Uses World.getMeasuredTopology() to determine the transformation.
	 *
	 * @param to Destination frame
	 * @return current commanded {@link Transformation} of the given {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getMeasuredTransformationTo(final Frame to) throws TransformationException {
		return getTransformationTo(to, World.getCommandedTopology());
	}

	/**
	 * Retrieves {@link Transformation} of another {@link Frame} relative to this
	 * {@link Frame}, according to the 'commanded' topology.
	 *
	 * Uses World.getCommandedTopology() to determine the transformation.
	 *
	 * @param to           Destination frame
	 * @param allowDynamic <code>true</code> allows the use of
	 *                     {@link GeometricRelation} that have non-constant
	 *                     transformations, <code>false</code> limits to constant
	 *                     {@link Transformation}s.
	 * @return current commanded {@link Transformation} of the given {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getCommandedTransformationTo(final Frame to, boolean allowDynamic)
			throws TransformationException {
		FrameTopology topology = World.getCommandedTopology();
		if (!allowDynamic) {
			topology = topology.withoutDynamic();
		}
		return getTransformationTo(to, topology);
	}

	/**
	 * Retrieves {@link Transformation} of another {@link Frame} relative to this
	 * {@link Frame}, according to the 'measured' topology.
	 *
	 * Uses World.getMeasuredTopology() to determine the transformation.
	 *
	 * @param to           Destination frame
	 * @param allowDynamic <code>true</code> allows the use of
	 *                     {@link GeometricRelation} that have non-constant
	 *                     transformations, <code>false</code> limits to constant
	 *                     {@link Transformation}s.
	 * @return current commanded {@link Transformation} of the given {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getMeasuredTransformationTo(final Frame to, boolean allowDynamic)
			throws TransformationException {
		FrameTopology topology = World.getMeasuredTopology();
		if (!allowDynamic) {
			topology = topology.withoutDynamic();
		}
		return getTransformationTo(to, topology);
	}

	/**
	 * Retrieves {@link Transformation} of another {@link Frame} relative to this
	 * {@link Frame}, according to the 'commanded' topology. Same as
	 * getCommandedTransformationTo(Frame to, boolean allowDynamic).
	 *
	 * @param to           Destination frame
	 * @param allowDynamic <code>true</code> allows the use of
	 *                     {@link GeometricRelation} that have non-constant
	 *                     transformations, <code>false</code> limits to constant
	 *                     {@link Transformation}s.
	 * @return current commanded {@link Transformation} of the given {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getTransformationTo(final Frame to, boolean allowDynamic) throws TransformationException {
		return getCommandedTransformationTo(to, allowDynamic);
	}

	/**
	 * Retrieves the {@link Transformation} of another {@link Frame} relative to
	 * this {@link Frame}.
	 *
	 * @param to       destination frame
	 * @param topology frame topology to use
	 * @return {@link Transformation} of the other {@link Frame} relative to this
	 *         {@link Frame}
	 * @throws TransformationException if the chosen {@link FrameTopology} contains
	 *                                 no path to the given {@link Frame}
	 */
	public Transformation getTransformationTo(final Frame to, FrameTopology topology) throws TransformationException {
		// Identity transformation?
		if (this == to) {
			return new Transformation(new Rotation(0, 0, 0), new Vector(0, 0, 0));
		}

		try {
			RealtimeTransformation relationSensor = getRealtimeTransformationTo(to, topology);

			if (relationSensor == null) {
				throw new TransformationException("No connection between Frames", this, to);
			}
			Transformation currentValue = relationSensor.getCurrentValue();

			return currentValue;
		} catch (RealtimeValueReadException e) {
			throw new TransformationException("Could not determine transformation", this, to, e);
		}
	}

	/**
	 * Creates a {@link Pose} representing this Frame
	 *
	 * @return Pose representing this Frame
	 */
	public Pose asPose() {
		return new Pose(this);
	}

	/**
	 * Creates a {@link RealtimePose} representing this Frame
	 *
	 * @return RealtimePose representing this Frame
	 */
	public RealtimePose asRealtimePose() {

		return RealtimePose.createFromTransformation(this, RealtimeTransformation.IDENTITY);
	}

	/**
	 * Copies all teaching information attached to this Frame to the given Frame.
	 *
	 * @param target the Frame to copy teaching information to
	 */
	public void copyTeachingInfoTo(Frame target) {
		for (List<TeachingInfo> tis : getAllTeachingInfos().values()) {
			for (TeachingInfo ti : tis) {
				target.addTeachingInfo(ti);
			}
		}
	}

	/**
	 * Gets the origin of this {@link Frame} as a {@link Point}.
	 *
	 * @return the Frame origin Point
	 */
	public Point asPoint() {
		return new Point(this, new Vector());
	}

	/**
	 * Gets a {@link Point} that is displaced from this {@link Frame}'s origin by
	 * the given values.
	 *
	 * @param x translation along x-axis of this Frame
	 * @param y translation along y-axis of this Frame
	 * @param z translation along z-axis of this Frame
	 * @return the point
	 */
	public Point getPoint(double x, double y, double z) {
		return new Point(this, new Vector(x, y, z));
	}

	/**
	 * Gets this {@link Frame}'s {@link Orientation}.
	 *
	 * @return the Orientation
	 */
	public Orientation asOrientation() {
		return new Orientation(this, Rotation.IDENTITY);
	}

	public RealtimeOrientation asRealtimeOrientation() {
		return asOrientation().asRealtimeValue();
	}

	/**
	 * Gets an {@link Orientation} that is displaced from this {@link Frame}'s
	 * orientation by the given rotation values.
	 *
	 * @param a rotation around the z-axis of this Frame
	 * @param b rotation around the y-axis of this Frame
	 * @param c rotation around the x-axis of this Frame
	 * @return the orientation
	 */
	public Orientation getOrientation(double a, double b, double c) {
		return new Orientation(this, new Rotation(a, b, c));
	}

	private static long globalNr = 1;
	private long localNr = -1;

	@Override
	public String toString() {
		if (getName() == null && localNr == -1) {
			localNr = globalNr++;
		}
		return getName() != null ? getName() : "<Unnamed Frame " + localNr + ">";
	}

	/**
	 * Retrieves all {@link Relation}s that connect this {@link Frame} to other
	 * {@link Frame}s in the given topology
	 *
	 * @param topology {@link FrameTopology} to use for the query
	 * @return list of all {@link Relation}s established in the given
	 *         {@link FrameTopology} that talk about this {@link Frame}
	 */
	public List<Relation> getRelations(FrameTopology topology) {
		return topology.getRelations(this);
	}

	/**
	 * Retrieves all {@link Relation}s of the given type that connect this
	 * {@link Frame} to other {@link Frame}s in the given topology
	 *
	 * @param type     type of {@link Relation} to search for
	 * @param topology {@link FrameTopology} to use for the query
	 * @return list of all {@link Relation}s established in the given
	 *         {@link FrameTopology} that talk about this {@link Frame}
	 */
	public <T extends Relation> List<T> getRelations(Class<T> type, FrameTopology topology) {
		List<T> ret = new ArrayList<>();
		for (Relation r : getRelations(topology)) {
			if (type.isAssignableFrom(r.getClass())) {
				ret.add(type.cast(r));
			}
		}
		return ret;
	}

	/**
	 * Retrieves all {@link GeometricRelation}s that connect this {@link Frame} to
	 * other {@link Frame}s in the given topology
	 *
	 * @param topology {@link FrameTopology} to use for the query
	 * @return list of all {@link GeometricRelation}s established in the given
	 *         {@link FrameTopology} that talk about this {@link Frame}
	 */
	public List<GeometricRelation> getGeometricRelations(FrameTopology topology) {
		return getRelations(GeometricRelation.class, topology);
	}

	/**
	 * Retrieves all {@link LogicalRelation}s that connect this {@link Frame} to
	 * other {@link Frame}s in the given topology
	 *
	 * @param topology {@link FrameTopology} to use for the query
	 * @return list of all {@link LogicalRelation}s established in the given
	 *         {@link FrameTopology} that talk about this {@link Frame}
	 */
	public List<LogicalRelation> getLogicalRelations(FrameTopology topology) {
		return getRelations(LogicalRelation.class, topology);
	}

	/**
	 * Retrieves all {@link Observation}s that connect this {@link Frame} to other
	 * {@link Frame}s in the given topology
	 *
	 * @param topology {@link FrameTopology} to use for the query
	 * @return list of all {@link Observation}s established in the given
	 *         {@link FrameTopology} that talk about this {@link Frame}
	 */
	public List<Observation> getObservations(FrameTopology topology) {
		return getRelations(Observation.class, topology);
	}

	public RealtimePoint asRealtimePoint() {
		return RealtimePoint.createFromVector(this, RealtimeVector.ZERO);
	}
}
