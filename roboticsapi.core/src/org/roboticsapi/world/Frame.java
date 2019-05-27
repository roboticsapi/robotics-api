/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.roboticsapi.world.util.EntityPredicates.and;
import static org.roboticsapi.world.util.EntityPredicates.contains;
import static org.roboticsapi.world.util.EntityPredicates.isDynamicConnection;
import static org.roboticsapi.world.util.EntityPredicates.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.Predicate;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractEntity;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * A Robotics API world model frame (named point with orientation in space)
 */
public class Frame extends AbstractEntity implements RoboticsObject, Entity {

	/** relations from and to the frame */
	private final List<Relation> relations = new ArrayList<Relation>();

	private final List<RelationListener> relationListeners = new ArrayList<RelationListener>();

	private final Map<Device, List<TeachingInfo>> teachingInfos = new HashMap<Device, List<TeachingInfo>>();

	/**
	 * Creates a temporary, unnamed Frame.
	 */
	public Frame() {
	}

	/**
	 * Creates a temporary, named Frame.
	 *
	 * @param name the Frame's name
	 */
	public Frame(String name) {
		setName(name);
	}

	/**
	 * Creates a temporary, named Frame and connects it to a given reference Frame
	 * via a {@link TemporaryRelation}. This Placement is initialized with the given
	 * {@link Transformation}, where the reference Frame is assigned the Placement's
	 * "from" role and the newly created Frame is assigned the "to" role (see
	 * {@link Relation}).
	 *
	 * @param name           the Frame's name
	 * @param referenceFrame the reference Frame to connect the newly created Frame
	 *                       to
	 * @param transformation the Transformation from the reference Frame to the
	 *                       newly created Frame.
	 */
	public Frame(String name, Frame referenceFrame, Transformation transformation) {
		this(name);

		TemporaryRelation relation = new TemporaryRelation(transformation);
		relation.setFrames(referenceFrame, this);
		addRelationInternally(relation, referenceFrame);
		referenceFrame.addRelationInternally(relation, this);
		referenceFrame.notifyRelationAdded(relation, this);
	}

	/**
	 * Creates a temporary, unnamed Frame and connects it to a given reference Frame
	 * via a {@link TemporaryRelation}. This Placement is initialized with the given
	 * {@link Transformation}, where the reference Frame is assigned the Placement's
	 * "from" role and the newly created Frame is assigned the "to" role (see
	 * {@link Relation}).
	 *
	 * @param referenceFrame the reference Frame to connect the newly created Frame
	 *                       to
	 * @param transformation the Transformation from the reference Frame to the
	 *                       newly created Frame.
	 */
	public Frame(Frame referenceFrame, Transformation transformation) {
		this(null, referenceFrame, transformation);
	}

	/**
	 * Creates a temporary, named Frame and connects it to a given reference Frame
	 * via a {@link TemporaryRelation}. This Placement is initialized with a
	 * {@link Transformation} initialized with the given translation and rotation
	 * values, where the reference Frame is assigned the Placement's "from" role and
	 * the newly created Frame is assigned the "to" role (see {@link Relation}).
	 *
	 * @param name      the Frame's name
	 * @param reference the reference Frame to connect the newly created Frame to
	 * @param x         the X translation value from the reference Frame to the
	 *                  newly created Frame
	 * @param y         the Y translation value from the reference Frame to the
	 *                  newly created Frame
	 * @param z         the Z translation value from the reference Frame to the
	 *                  newly created Frame
	 * @param a         the A rotation value (i.e. rotation around Z axis) from the
	 *                  reference Frame to the newly created Frame
	 * @param b         the B rotation value (i.e. rotation around Y axis) from the
	 *                  reference Frame to the newly created Frame
	 * @param c         the C rotation value (i.e. rotation around X axis) from the
	 *                  reference Frame to the newly created Frame
	 */
	public Frame(String name, Frame reference, double x, double y, double z, double a, double b, double c) {
		this(name, reference, new Transformation(x, y, z, a, b, c));
	}

	/**
	 * Creates a temporary, unnamed Frame and connects it to a given reference Frame
	 * via a {@link TemporaryRelation}. This Placement is initialized with a
	 * {@link Transformation} initialized with the given translation and rotation
	 * values, where the reference Frame is assigned the Placement's "from" role and
	 * the newly created Frame is assigned the "to" role (see {@link Relation}).
	 *
	 * @param reference the reference Frame to connect the newly created Frame to
	 * @param x         the X translation value from the reference Frame to the
	 *                  newly created Frame
	 * @param y         the Y translation value from the reference Frame to the
	 *                  newly created Frame
	 * @param z         the Z translation value from the reference Frame to the
	 *                  newly created Frame
	 * @param a         the A rotation value (i.e. rotation around Z axis) from the
	 *                  reference Frame to the newly created Frame
	 * @param b         the B rotation value (i.e. rotation around Y axis) from the
	 *                  reference Frame to the newly created Frame
	 * @param c         the C rotation value (i.e. rotation around X axis) from the
	 *                  reference Frame to the newly created Frame
	 */
	public Frame(Frame reference, double x, double y, double z, double a, double b, double c) {
		this(reference, new Transformation(x, y, z, a, b, c));
	}

	/**
	 * Retrieves the relations of this frame
	 *
	 * @return read-only list of relations
	 */
	public List<Relation> getRelations() {
		synchronized (relations) {
			return new RelationList(relations);
		}
	}

	private class RelationList extends ArrayList<Relation> {
		private static final long serialVersionUID = 4003166285564520570L;
		private final Set<Frame> relatedFrames = new HashSet<Frame>();

		public RelationList(List<Relation> relations) {

			for (int i = 0; i < relations.size(); i++) {
				Relation r = relations.get(i);
				Frame from = r.getFrom();
				Frame to = r.getTo();
				if (from == null || to == null) {
					notifyRelationRemoved(r, null);
					relations.remove(i--);
				} else {
					relatedFrames.add(from);
					relatedFrames.add(to);
					add(r);
				}
			}
		}
	}

	/**
	 * Adds a relation to the frame
	 *
	 * @param relation relation to add
	 */
	public void addRelation(final Relation relation, Frame goal) throws InitializationException {
		if (!relations.contains(relation)) {
			if (!isInitialized()) {
				throw new InitializationException("The start frame is not initialized");
			}
			if (!goal.isInitialized()) {
				getContext().initialize(goal);
			}
			relation.setFrames(this, goal);
			getContext().initialize(relation);
		}
	}

	protected void addRelationInternally(final Relation relation, Frame goal) {
		synchronized (relations) {
			relations.add(relation);
		}
	}

	protected void removeRelationInternally(final Relation relation, Frame goal) {
		synchronized (relations) {
			relations.remove(relation);
		}
	}

	protected void notifyRelationAdded(final Relation relation, Frame to) {
		for (RelationListener l : relationListeners) {
			try {
				l.relationAdded(relation, to);
			} catch (Exception e) {
				RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
						e.getClass().getCanonicalName() + " in RelationListener " + l + ": " + e.getMessage(), e);
			}
		}
	}

	protected void notifyRelationRemoved(final Relation relation, Frame to) {
		for (RelationListener l : new ArrayList<RelationListener>(relationListeners)) {
			l.relationRemoved(relation, to);
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
	public void addTeachingInfo(Device device, Frame motionCenter, double[] hintParameters) {
		addTeachingInfo(new TeachingInfo(device, motionCenter, hintParameters));
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
	public TeachingInfo getTeachingInfo(Device device, Frame motionCenter) {
		if (teachingInfos.containsKey(device)) {
			for (TeachingInfo ti : teachingInfos.get(device)) {
				if (ti.getMotionCenter().equals(motionCenter)) {
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
	 * Removes a relation from the frame.
	 *
	 * @param relation relation to remove
	 * @param force    delete relation even if not deletable
	 * @throws RoboticsException
	 */
	public void removeRelation(final Relation relation, boolean force) throws InitializationException {
		if (!relation.isDeletable() && !force) {
			return;
		}

		if (relations.contains(relation)) {
			if (relation.isInitialized()) {
				getContext().uninitialize(relation);
			} else {
				Frame other = relation.getOther(this);
				removeRelationInternally(relation, other);
				if (other != null) {
					other.removeRelationInternally(relation, this);
				}

				notifyRelationRemoved(relation, other);
				if (other != null) {
					other.notifyRelationRemoved(relation, this);
				}
			}
		}
	}

	/**
	 * Removes a relation from the frame (if deletable)
	 *
	 * @param relation relation to remove
	 * @throws RoboticsException
	 */
	public void removeRelation(final Relation relation) throws InitializationException {
		removeRelation(relation, false);
	}

	protected void removeRelations() throws InitializationException {
		while (relations.size() > 0) {
			removeRelation(relations.get(0));
		}
	}

	/**
	 * Sets the name of the frame
	 *
	 * @param name name of the frame
	 */
	// void setName(final String name) {
	// this.name = name;
	// }

	// public void rename(final String newName) throws FrameException {
	// FrameFactory.renameFrame(this.getName(), newName);
	// }

	/**
	 * Retrieves the name of the frame
	 *
	 * @return name of the frame
	 */
	// public String getName() {
	// return name;
	// }

	/**
	 * Indicates whether this Frame is deletable.
	 *
	 * @return true, if Frame may be deleted, false otherwise
	 */
	public boolean isDeletable() {
		for (Relation r : relations) {
			if (r instanceof TemporaryRelation) {
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

	/**
	 * Gets a {@link RelationSensor} that measures the current commanded
	 * {@link Transformation} from this {@link Frame} to the given {@link Frame} .
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * goal values are considered instead of actual, measured values.
	 *
	 * @param to                 the target {@link Frame}
	 * @param forbiddenRelations Relations that must not be considered for
	 *                           determining the Transformation
	 * @return the {@link RelationSensor}
	 */
	public RelationSensor getRelationSensor(final Frame to, Relation... forbiddenRelations) {
		return getRelationSensor(to, false, true, null, forbiddenRelations);
	}

	/**
	 * Gets a {@link RelationSensor} that measures the current commanded
	 * {@link Transformation} from this {@link Frame} to the given {@link Frame} .
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * goal values are considered instead of actual, measured values.
	 *
	 * @param to                 the target {@link Frame}
	 * @param relationMap        transformations that should be used instead of the
	 *                           current value for some given relations
	 * @param forbiddenRelations Relations that must not be considered for
	 *                           determining the Transformation
	 * @return the {@link RelationSensor}
	 */
	public RelationSensor getRelationSensor(final Frame to, Map<Relation, RelationSensor> relationMap,
			Relation... forbiddenRelations) {
		return getRelationSensor(to, false, true, relationMap, forbiddenRelations);
	}

	/**
	 * Gets a {@link RelationSensor} that measures the currently measured
	 * {@link Transformation} from this {@link Frame} to the given {@link Frame}
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param to                 the target {@link Frame}
	 * @param forbiddenRelations Relations that must not be considered for
	 *                           determining the Transformation
	 * @return the {@link RelationSensor}
	 */
	public RelationSensor getMeasuredRelationSensor(final Frame to, Relation... forbiddenRelations) {
		return getRelationSensor(to, true, true, null, forbiddenRelations);
	}

	/**
	 * Gets a {@link RelationSensor} that measures the currently measured
	 * {@link Transformation} from this {@link Frame} to the given {@link Frame}
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param to                 the target {@link Frame}
	 * @param forbiddenRelations Relations that must not be considered for
	 *                           determining the Transformation
	 * @param relationMap        relation sensors that should be used instead of the
	 *                           current values for some given relations
	 * @return the {@link RelationSensor}
	 */
	public RelationSensor getMeasuredRelationSensor(final Frame to, Map<Relation, RelationSensor> relationMap,
			Relation... forbiddenRelations) {
		return getRelationSensor(to, true, true, relationMap, forbiddenRelations);
	}

	/**
	 * Gets a {@link RelationSensor} that measures the {@link Transformation} from
	 * this {@link Frame} to the given {@link Frame} .
	 *
	 * @param to                 the target {@link Frame}
	 * @param measured           whether measured values should be considered;
	 *                           otherwise, commanded values are considered
	 * @param relationMap        relation sensors that should be used instead of the
	 *                           current values for some given relations
	 * @param forbiddenRelations Relations that must not be considered for
	 *                           determining the Transformation
	 * @return the {@link RelationSensor}
	 */
	@SuppressWarnings("unchecked")
	private RelationSensor getRelationSensor(final Frame to, boolean measured, boolean allowDynamic,
			Map<Relation, RelationSensor> relationMap, Relation... forbiddenRelations) {
		Predicate<Entity> forbiddenPredicate = not(contains(forbiddenRelations));
		Predicate<Entity> dynamicPredicate = allowDynamic ? null : not(isDynamicConnection());

		return getRelationSensor(to, measured, relationMap, and(forbiddenPredicate, dynamicPredicate));
	}

	public RelationSensor getRelationSensor(final Frame to, boolean measured, Map<Relation, RelationSensor> relationMap,
			Predicate<Entity> filter) {
		List<Relation> way = getRelationsTo(to, filter);

		// check if we found a way between the two frames
		if (way != null) {
			// case: this == to
			if (way.isEmpty()) {
				return RelationSensor.fromConstant(this, to, new Transformation());
			}

			RelationSensor ret = null;
			Frame cur = this;

			for (Relation curRel : way) {
				cur = curRel.getOther(cur);
				RelationSensor toCur;

				// check relation map
				if (relationMap != null && relationMap.containsKey(curRel)) {
					toCur = relationMap.get(curRel);
					if (toCur.getTo() != cur) {
						toCur = toCur.invert();
					}
				} else {
					toCur = measured ? curRel.getMeasuredRelationSensorTo(cur) : curRel.getRelationSensorTo(cur);
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
	 * Gets a {@link VelocitySensor} that measures the current commanded velocity of
	 * the given {@link Frame} relative to this {@link Frame}.
	 *
	 * The given Frame's origin is defined as Pivot Point, while this Frame's
	 * orientation is used as the velocity Orientation (see {@link VelocitySensor}).
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * goal values are considered instead of actual, measured values.
	 *
	 * @param moving the moving {@link Frame}
	 * @return a {@link VelocitySensor} measuring moving's velocity
	 */
	public VelocitySensor getVelocitySensorOf(final Frame moving) {
		return getVelocitySensorOf(moving, moving.getPoint(), this.getOrientation());
	}

	/**
	 * Gets a {@link VelocitySensor} that measures the current commanded velocity of
	 * the given {@link Frame} relative to this {@link Frame} with respect to the
	 * given Pivot Point and Orientation (see {@link VelocitySensor}).
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * goal values are considered instead of actual, measured values.
	 *
	 * @param moving      the moving {@link Frame}
	 * @param pivotPoint  the Pivot Point
	 * @param orientation the Orientation
	 * @return a {@link VelocitySensor} measuring moving's velocity
	 */
	public VelocitySensor getVelocitySensorOf(final Frame moving, Point pivotPoint, Orientation orientation) {
		return getVelocitySensorOf(moving, pivotPoint, orientation, false);
	}

	/**
	 * Gets a {@link VelocitySensor} that measures the measured velocity of the
	 * given {@link Frame} relative to this {@link Frame}.
	 *
	 * The given Frame's origin is defined as Pivot Point, while this Frame's
	 * orientation is used as the velocity Orientation (see {@link VelocitySensor}).
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param moving the moving {@link Frame}
	 * @return a {@link VelocitySensor} measuring moving's velocity
	 */
	public VelocitySensor getMeasuredVelocitySensorOf(final Frame moving) {
		return getVelocitySensorOf(moving, moving.getPoint(), this.getOrientation());
	}

	/**
	 * Gets a {@link VelocitySensor} that measures the measured velocity of the
	 * given {@link Frame} relative to this {@link Frame}.
	 *
	 * The given Frame's origin is defined as Pivot Point, while this Frame's
	 * orientation is used as the velocity Orientation (see {@link VelocitySensor}).
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param moving      the moving {@link Frame}
	 * @param pivotPoint  the Pivot Point
	 * @param orientation the Orientation
	 * @return a {@link VelocitySensor} measuring moving's velocity
	 * @throws VelocityException thrown if VelocitySensor cannot be determined
	 */
	public VelocitySensor getMeasuredVelocitySensorOf(final Frame moving, Point pivotPoint, Orientation orientation)
			throws VelocityException {
		return getVelocitySensorOf(moving, pivotPoint, orientation, true);
	}

	/**
	 * Gets a {@link VelocitySensor} that measures the measured velocity of the
	 * given {@link Frame} relative to this {@link Frame}.
	 *
	 * The given Frame's origin is defined as Pivot Point, while this Frame's
	 * orientation is used as the velocity Orientation (see {@link VelocitySensor}).
	 *
	 * @param moving      the moving {@link Frame}
	 * @param pivotPoint  the Pivot Point
	 * @param orientation the Orientation
	 * @param measured    whether measured values should be considered; otherwise,
	 *                    commanded values are considered
	 * @return a {@link VelocitySensor} measuring moving's velocity
	 */
	private VelocitySensor getVelocitySensorOf(final Frame moving, Point pivotPoint, Orientation orientation,
			boolean measured) {
		List<Relation> way = getRelationsTo(moving);

		// check if we found a way between the two frames
		if (way != null) {
			// case: this == to
			if (way.isEmpty()) {
				return new ConstantVelocitySensor(moving, this, pivotPoint, orientation, new Twist());
			}

			VelocitySensor ret = null;
			Frame cur = this;

			for (Relation r : way) {
				cur = r.getOther(cur);

				VelocitySensor toCur = measured ? r.getMeasuredVelocitySensorTo(cur) : r.getVelocitySensorTo(cur);

				if (toCur == null) {
					return null;
				}
				toCur = toCur.changePivotPoint(pivotPoint).changeOrientation(orientation);

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
	 * Gets a list of all {@link Frame}s that are connected to this {@link Frame}
	 * via some {@link Relation}.
	 *
	 * @param forbiddenRelations Relations that should not be considered
	 * @return the connected frames
	 */
	public List<Frame> getConnectedFrames(Relation... forbiddenRelations) {
		return getConnectedFrames(true, true, forbiddenRelations);
	}

	/**
	 * Collects all Frames connected somehow to this Frame, except Frames whose only
	 * connection path from this Frame contains one of the given forbidden
	 * Relations.
	 *
	 * @param forbiddenRelations the relations that may not be considered
	 * @return the connected frames
	 */
	public List<Frame> getConnectedFrames(boolean withTemporary, Relation... forbiddenRelations) {
		return getConnectedFrames(withTemporary, true, forbiddenRelations);
	}

	/**
	 * Collects all Frames connected somehow to this Frame, except Frames whose only
	 * connection path from this Frame contains one of the given forbidden Relations
	 * or Frames that are connected via a DynamicConnection (if allowDynamic is set
	 * false).
	 *
	 *
	 * @param allowDynamic       whether DynamicConnections may be taken
	 * @param forbiddenRelations the relations that may not be considered
	 * @return the connected frames
	 */
	public List<Frame> getConnectedFrames(boolean withTemporary, boolean allowDynamic, Relation... forbiddenRelations) {
		List<Frame> todoList = new LinkedList<Frame>();
		List<Frame> ret = new LinkedList<Frame>();
		List<Relation> forbidden = Arrays.asList(forbiddenRelations);

		todoList.add(this);
		// dummy add to prevent it from being added later
		ret.add(this);

		while (!todoList.isEmpty()) {
			for (Frame f : new LinkedList<Frame>(todoList)) {

				todoList.remove(f);

				for (Relation r : f.getRelations()) {

					if (r instanceof DynamicConnection && !allowDynamic) {
						continue;
					}

					Frame other = r.getOther(f);

					if (other == null) {
						continue;
					}

					if (other.isTemporary() && !withTemporary) {
						continue;
					}

					if (!forbidden.contains(r) && !ret.contains(other)) {
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
	 * Retrieves the shortest relation path to another frame.
	 *
	 * @param to                 destination frame
	 * @param forbiddenRelations relations not allowed on the way to the destination
	 *                           frame
	 * @return list of relations on the way to the given frame, or <code>null</code>
	 *         if none found
	 */
	public List<Relation> getRelationsTo(final Frame to, Relation... forbiddenRelations) {
		return getRelationsTo(to, not(contains(forbiddenRelations)));
	}

	/**
	 * Retrieves the shortest relation path to another frame.
	 *
	 * @param to     Destination frame
	 * @param filter Optional predicate that includes relations on the way to the
	 *               destination frame if it evaluates to <code>true</code>.
	 * @return List of relations on the way to the given frame, or <code>null</code>
	 *         if none found
	 */
	public List<Relation> getRelationsTo(final Frame to, Predicate<Entity> filter) {
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

				for (final Relation relation : currentFrame.getRelations()) {
					if (filter != null && !filter.appliesTo(relation)) {
						continue;
					}

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

				for (final Relation relation : currentFrame.getRelations()) {
					if (filter != null && !filter.appliesTo(relation)) {
						continue;
					}

					// scan all relations from that element
					final Frame other = relation.getOther(currentFrame);

					if (other == this || other.getRelations().size() > 1) {
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
	 * Retrieves the transformation to another frame. In case the connection between
	 * the Frames is dynamic, the currently commanded (i.e. goal) value of this
	 * connection is considered.
	 *
	 * @param to destination frame
	 * @return transformation matrix to the other frame, or null if not connected
	 * @throws RoboticsException
	 */
	public Transformation getTransformationTo(final Frame to, Relation... forbiddenRelations)
			throws TransformationException {
		return getTransformationTo(to, true, forbiddenRelations);
	}

	/**
	 * Retrieves the transformation to another frame. In case the connection between
	 * the Frames is dynamic, the currently measured value of this connection is
	 * considered.
	 *
	 * @param to destination frame
	 * @return transformation matrix to the other frame, or null if not connected
	 * @throws RoboticsException
	 */
	public Transformation getMeasuredTransformationTo(final Frame to, Relation... forbiddenRelations)
			throws TransformationException {
		return getMeasuredTransformationTo(to, true, forbiddenRelations);
	}

	/**
	 * Retrieves the transformation to another frame. In case the connection between
	 * the Frames is dynamic, the currently commanded (i.e. goal) value of this
	 * connection is considered.
	 *
	 * @param to           destination frame
	 * @param allowDynamic may use current value of dynamic connections
	 * @return transformation matrix to the other frame, or null if not connected
	 * @throws RoboticsException
	 */
	public Transformation getTransformationTo(final Frame to, final boolean allowDynamic,
			Relation... forbiddenRelations) throws TransformationException {
		return getTransformationToInternal(to, allowDynamic, false, null, forbiddenRelations);
	}

	/**
	 * Retrieves the transformation to another frame. In case the connection between
	 * the Frames is dynamic, the currently commanded (i.e. goal) value of this
	 * connection is considered.
	 *
	 * @param to           destination frame
	 * @param allowDynamic may use current value of dynamic connections
	 * @param relationMap  relation sensors that should be used instead of the
	 *                     current values for some given relations
	 * @return transformation matrix to the other frame, or null if not connected
	 * @throws RoboticsException
	 */
	public Transformation getTransformationTo(final Frame to, final boolean allowDynamic,
			Map<Relation, RelationSensor> relationMap, Relation... forbiddenRelations) throws TransformationException {
		return getTransformationToInternal(to, allowDynamic, false, relationMap, forbiddenRelations);
	}

	/**
	 * Retrieves the transformation to another frame. In case the connection between
	 * the Frames is dynamic, the currently measured value of this connection is
	 * considered.
	 *
	 * @param to           destination frame
	 * @param allowDynamic may use current value of dynamic connections
	 * @return measured transformation matrix to the other frame, or null if not
	 *         connected
	 * @throws RoboticsException
	 */
	public Transformation getMeasuredTransformationTo(final Frame to, final boolean allowDynamic,
			Relation... forbiddenRelations) throws TransformationException {
		return getTransformationToInternal(to, allowDynamic, true, null, forbiddenRelations);
	}

	/**
	 * Retrieves the transformation to another frame. In case the connection between
	 * the Frames is dynamic, the currently measured value of this connection is
	 * considered.
	 *
	 * @param to           destination frame
	 * @param allowDynamic may use current value of dynamic connections
	 * @param relationMap  relation sensors that should be used instead of the
	 *                     current values for some given relations
	 * @return measured transformation matrix to the other frame, or null if not
	 *         connected
	 * @throws RoboticsException
	 */
	public Transformation getMeasuredTransformationTo(final Frame to, final boolean allowDynamic,
			Map<Relation, RelationSensor> relationMap, Relation... forbiddenRelations) throws TransformationException {
		return getTransformationToInternal(to, allowDynamic, true, relationMap, forbiddenRelations);
	}

	/**
	 * Retrieves the transformation to another frame
	 *
	 * @param to           destination frame
	 * @param allowDynamic may use current value of dynamic connections
	 * @param relationMap  relation sensors that should be used instead of the
	 *                     current values for some given relations
	 * @return transformation matrix to the other frame, or null if not connected
	 * @throws RoboticsException
	 */
	private Transformation getTransformationToInternal(final Frame to, final boolean allowDynamic, boolean measured,
			Map<Relation, RelationSensor> relationMap, Relation[] forbiddenRelations) throws TransformationException {
		// Identity transformation?
		if (this == to) {
			return new Transformation(new Rotation(0, 0, 0), new Vector(0, 0, 0));
		}

		try {
			RelationSensor relationSensor = getRelationSensor(to, measured, allowDynamic, relationMap,
					forbiddenRelations);

			if (relationSensor == null) {
				throw new TransformationException("No connection between Frames", this, to);
			}
			Transformation currentValue = relationSensor.getCurrentValue();

			return currentValue;
		} catch (SensorReadException e) {
			throw new TransformationException("Could not determine transformation", this, to, e);
		}
	}

	/**
	 * Returns a new Frame that "snapshots" (i.e., holds constant) the current
	 * (commanded) geometric state of this Frame with respect to a given reference
	 * Frame at the current point in time.
	 *
	 * Use this method to conserve the state of frames that are changing over time
	 * (like the robot flange frame).
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * target values are considered instead of actual, measured values.
	 *
	 * @param referenceFrame the reference frame used for the snapshot.
	 * @return new "snapshotted" version of this frame with respect to the given
	 *         reference frame.
	 * @throws RoboticsException
	 */
	public Frame snapshot(final Frame referenceFrame) throws RoboticsException {
		return new Frame(referenceFrame, referenceFrame.getTransformationTo(this));
	}

	/**
	 * Returns a new Frame that "snapshots" (i.e., holds constant) the current
	 * (measured) geometric state of this Frame with respect to a given reference
	 * Frame at the current point in time.
	 *
	 * Use this method to conserve the state of frames that are changing over time
	 * (like the robot flange frame).
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param referenceFrame the reference frame used for the snapshot.
	 * @return new "snapshotted" version of this frame with respect to the given
	 *         reference frame.
	 * @throws RoboticsException
	 */
	public Frame snapshotMeasured(final Frame referenceFrame) throws RoboticsException {
		return new Frame(referenceFrame, referenceFrame.getMeasuredTransformationTo(this));
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given values
	 * relative to this Frame.
	 *
	 * The new Frame is a temporary Frame that is linked to this Frame via a
	 * {@link Placement}, i.e. it follows motions of this Frame.
	 *
	 *
	 * @param x translation along x-axis of this Frame
	 * @param y translation along y-axis of this Frame
	 * @param z translation along z-axis of this Frame
	 * @param a rotation around z-axis of this Frame
	 * @param b rotation around y-axis of this Frame
	 * @param c rotation around x-axis of this Frame
	 * @return the newly constructed Frame
	 */
	public final Frame plus(double x, double y, double z, double a, double b, double c) {
		return plus(new Vector(x, y, z), new Rotation(a, b, c));
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given values
	 * relative to this Frame.
	 *
	 * The new Frame is a temporary Frame that is linked to this Frame via a
	 * {@link Placement}, i.e. it follows motions of this Frame.
	 *
	 * @param x translation along x-axis of this Frame
	 * @param y translation along y-axis of this Frame
	 * @param z translation along z-axis of this Frame
	 * @return the newly constructed Frame
	 *
	 */
	public final Frame plus(double x, double y, double z) {
		return plus(x, y, z, 0, 0, 0);
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given
	 * {@link Vector} relative to this Frame.
	 *
	 * The new Frame is a temporary Frame that is linked to this Frame via a
	 * {@link Placement}, i.e. it follows motions of this Frame.
	 *
	 * @param translation the translation vector
	 * @return the newly constructed Frame
	 */
	public final Frame plus(Vector translation) {
		return plus(translation, new Rotation());
	}

	/**
	 * Creates a new temporary {@link Frame} that is rotated by the given
	 * {@link Rotation} relative to this Frame.
	 *
	 * The new Frame is a temporary Frame that is linked to this Frame via a
	 * {@link Placement}, i.e. it follows motions of this Frame.
	 *
	 * @param rotation the Rotation
	 * @return the newly constructed Frame
	 */
	public final Frame plus(Rotation rotation) {
		return plus(Vector.getNullVector(), rotation);
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given Vector
	 * and the given {@link Rotation} relative to this Frame.
	 *
	 * The new Frame is a temporary Frame that is linked to this Frame via a
	 * {@link Placement}, i.e. it follows motions of this Frame.
	 *
	 * @param translation the translation vector
	 * @param rotation    the rotation
	 * @return the newly constructed Frame
	 */
	public final Frame plus(Vector translation, Rotation rotation) {
		return plus(new Transformation(rotation, translation));
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given
	 * {@link Transformation} relative to this Frame.
	 *
	 * The new Frame is a temporary Frame that is linked to this Frame via a
	 * {@link Placement}, i.e. it follows motions of this Frame.
	 *
	 * @param transformation the Transformation between this Frame and the new Frame
	 * @return the newly constructed Frame
	 *
	 * @throws IllegalArgumentException if the given {@link Transformation} is
	 *                                  <code>null</code>.
	 */
	public Frame plus(Transformation transformation) {
		if (transformation == null) {
			throw new IllegalArgumentException("The given transformation cannot be null.");
		}
		return new Frame(this, transformation);
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given
	 * {@link Vector} relative to this Frame. The displacement is interpreted with
	 * respect to the given {@link Orientation}.
	 *
	 * Example usage: lwr.getFlange().plus(new Vector(1, 0, 0),
	 * lwr.getBase().getOrientation());
	 *
	 * This creates a new Frame, attached to lwr's flange, which is displaced by 1m
	 * in x direction of lwr's base.
	 *
	 * @param translation the translational displacement
	 * @param reference   the reference orientation for applying the displacement
	 * @return the newly constructed Frame
	 *
	 * @throws RoboticsException        thrown if the effective displacement could
	 *                                  not be calculated
	 * @throws IllegalArgumentException if the given {@link Vector} or the given
	 *                                  {@link Orientation} is <code>null</code>.
	 */
	public final Frame plus(Vector translation, Orientation reference) throws RoboticsException {
		if (translation == null) {
			throw new IllegalArgumentException("The given vector cannot be null.");
		}
		if (reference == null) {
			throw new IllegalArgumentException("The given orientation cannot be null.");
		}

		Frame refFrame = reference.getReferenceFrame().plus(reference.getRotation());
		Transformation toThis = refFrame.getTransformationTo(this, false);

		Vector newTranslation = toThis.getTranslation().add(translation);

		return refFrame.plus(newTranslation, toThis.getRotation()).snapshot(this);
	}

	/**
	 * Creates a new temporary {@link Frame} that is displaced by the given
	 * translation values relative to this Frame. The displacement is interpreted
	 * with respect to the given {@link Orientation}.
	 *
	 * Example usage: lwr.getFlange().plus(1, 0, 0, lwr.getBase().getOrientation());
	 *
	 * This creates a new Frame, attached to lwr's flange, which is displaced by 1m
	 * in x direction of lwr's base.
	 *
	 * @param x         translation in x direction
	 * @param y         translation in y direction
	 * @param z         translation in z direction
	 * @param reference the reference orientation for applying the displacement
	 * @return the newly constructed Frame
	 * @throws RoboticsException thrown if the effective displacement could not be
	 *                           calculated
	 */
	public final Frame plus(double x, double y, double z, Orientation reference) throws RoboticsException {

		return plus(new Vector(x, y, z), reference);
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
	public Point getPoint() {
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
	public Orientation getOrientation() {
		return new Orientation(this, new Rotation());
	}

	@Override
	protected void validateBeforeUninitialization() throws InitializationException {
		super.validateBeforeUninitialization();

		ComposedEntity parent = getParent();

		if (parent != null) {
			if (!parent.canRemoveChild(this)) {
				throw new InitializationException("Frrame belongs to an entity and cannot be removed.");
			}
		}
		for (Relation r : relations) {
			if (r.isInitialized()) {
				throw new InitializationException("Frame has an initialized relation and cannot be removed.");
			}
		}
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

	@Override
	public String toString() {
		return getName() != null ? getName() : "<Unnamed Frame>";
	}

}
