/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractEntity;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * A Robotics API world model relation (between two frames)
 */
public abstract class Relation extends AbstractEntity implements RoboticsObject, Entity {

	/** whether this relation is deletable */
	// protected boolean deletable;

	/** source and destination frame */
	private Frame from, to;

	/**
	 * Creates a new relation between two frames
	 *
	 * @param from           source frame
	 * @param to             destination frame
	 * @param transformation transformation between the frames
	 */
	protected Relation() { // boolean deletable) {
		// this.deletable = deletable;
	}

	protected void setFrames(Frame from, Frame to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Retrieves the transformation towards the given "to" frame
	 *
	 * @param to destination frame
	 * @return transformation to the frame
	 * @throws RoboticsException
	 */
	public Transformation getTransformationTo(final Frame to) throws TransformationException {
		if (to != getFrom()) {
			return getTransformation();
		} else {
			return getTransformation().invert();
		}
	}

	/**
	 * Retrieves the measured transformation towards the given "to" frame
	 *
	 * @param to destination frame
	 * @return measured transformation to the frame
	 */
	public Transformation getMeasuredTransformationTo(final Frame to) throws TransformationException {
		if (to != getFrom()) {
			return getMeasuredTransformation();
		} else {
			return getMeasuredTransformation().invert();
		}
	}

	/**
	 * Retrieves the source frame
	 *
	 * @return source frame
	 */
	public Frame getFrom() {
		return from;
	}

	/**
	 * Sets the source frame of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param from the source Frame
	 */
	@ConfigurationProperty
	public void setFrom(Frame from) {
		immutableWhenInitialized();
		this.from = from;
	}

	/**
	 * Retrieves the destination frame of this Relation.
	 *
	 * @return destination frame
	 */
	public Frame getTo() {
		return to;
	}

	/**
	 * Sets the destination Frame of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param to the new destination Frame
	 */
	@ConfigurationProperty
	public void setTo(Frame to) {
		immutableWhenInitialized();
		this.to = to;
	}

	/**
	 * Returns the Frame at the other "end" of this Relation w.r.t. to the given
	 * Frame. Returns null if the given Frame is neither "from" nor "to" of this
	 * Relation.
	 *
	 * @param f the given Frame
	 * @return the Frame at the other "end" of this Relation
	 */
	public Frame getOther(Frame f) {
		if (getFrom().equals(f)) {
			return getTo();
		} else if (f == null) {
			if (getTo() == null) {
				return getFrom();
			} else {
				return null;
			}
		} else if (f.equals(getTo())) {
			return getFrom();
		} else {
			return null;
		}
	}

	/**
	 * Checks if this Relation is deletable.
	 *
	 * Deletable Relations can be removed via
	 * {@link Frame#removeRelation(Relation)}.
	 *
	 * @return true, if is deletable
	 */
	public boolean isDeletable() {
		try {
			validateBeforeUninitialization();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void remove() throws InitializationException {
		getFrom().removeRelation(this);
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		if (getFrom() == null) {
			throw new ConfigurationException("from", "Source frame is missing");
		}
		if (!getFrom().isInitialized()) {
			throw new ConfigurationException("from", "Source frame is not initialized");
		}

		if (getTo() == null) {
			throw new ConfigurationException("to", "Destination frame is missing");
		}
		if (!getTo().isInitialized()) {
			throw new ConfigurationException("to", "Destination frame is not initialized");
		}
	}

	@Override
	protected void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();

		getFrom().addRelationInternally(this, getTo());
		getTo().addRelationInternally(this, getFrom());

		// getFrom().notifyRelationAdded(this, getTo());
		// getTo().notifyRelationAdded(this, getFrom());
	}

	@Override
	protected void afterInitialization() throws RoboticsException {
		super.afterInitialization();

		getFrom().notifyRelationAdded(this, getTo());
		getTo().notifyRelationAdded(this, getFrom());
	}

	@Override
	protected void validateBeforeUninitialization() throws InitializationException {
		super.validateBeforeUninitialization();

		ComposedEntity parent = getParent();

		if (parent != null) {
			if (!parent.canRemoveChild(this)) {
				throw new InitializationException("Relation belongs to an entity and cannot be removed.");
			}
		}
	}

	@Override
	protected void beforeUninitialization() throws RoboticsException {
		setParent(null);

		getFrom().removeRelationInternally(this, getTo());
		getTo().removeRelationInternally(this, getFrom());
		// getFrom().notifyRelationRemoved(this, getTo());
		// getTo().notifyRelationRemoved(this, getFrom());

		super.beforeUninitialization();
	}

	@Override
	protected void afterUninitialization() throws RoboticsException {
		getFrom().notifyRelationRemoved(this, getTo());
		getTo().notifyRelationRemoved(this, getFrom());

		super.afterUninitialization();
	}

	/**
	 * Gets the currently commanded {@link Transformation} between "from" and "to"
	 * {@link Frame}.
	 *
	 * 'Commanded' in this context means the desired value that the Transformation
	 * should have. In the context of actuators, this value can differ from the
	 * actual ('measured') Transformation value, which is obtainable by
	 * {@link Relation#getMeasuredTransformation()}.
	 *
	 * @return the current commanded {@link Transformation}
	 * @throws TransformationException thrown if current Transformation cannot be
	 *                                 determined
	 */
	public abstract Transformation getTransformation() throws TransformationException;

	/**
	 * Gets the currently measured {@link Transformation} between "from" and "to"
	 * {@link Frame}.
	 *
	 * 'Measured' in this context means the actual (measured) value that the
	 * Transformation has. In the context of actuators, this value can differ from
	 * the desired ('commanded') Transformation value, which is obtainable by
	 * {@link Relation#getTransformation()}.
	 *
	 * @return the current measured {@link Transformation}
	 * @throws TransformationException thrown if current Transformation cannot be
	 *                                 determined
	 */
	public abstract Transformation getMeasuredTransformation() throws TransformationException;

	/**
	 * Gets a {@link RelationSensor} that delivers the commanded
	 * {@link Transformation} between "from" and "to" {@link Frame}.
	 *
	 * 'Commanded' in this context means the desired value that the Transformation
	 * should have. In the context of actuators, this value can differ from the
	 * actual ('measured') Transformation value, which is obtainable by
	 * {@link Relation#getMeasuredRelationSensor()}.
	 *
	 * @return {@link RelationSensor} delivering the current commanded
	 *         {@link Transformation}
	 */
	public abstract RelationSensor getRelationSensor();

	/**
	 * Gets a {@link RelationSensor} that delivers the commanded
	 * {@link Transformation} to the given {@link Frame}. This Frame is required to
	 * be either this Relation's "from" or "to" Frame.
	 *
	 * 'Commanded' in this context means the desired value that the Transformation
	 * should have. In the context of actuators, this value can differ from the
	 * actual ('measured') Transformation value, which is obtainable by
	 * {@link Relation#getMeasuredRelationSensorTo(Frame))}.
	 *
	 * @return {@link RelationSensor} delivering the current commanded
	 *         {@link Transformation} to the given Frame
	 */
	public RelationSensor getRelationSensorTo(Frame to) {
		if (to == this.getTo()) {
			return getRelationSensor();
		}
		if (to == this.getFrom()) {
			return getRelationSensor().invert();
		}
		throw new IllegalArgumentException("Given Frame must belong to this Relation!");
	}

	/**
	 * Gets a {@link RelationSensor} that delivers the measured
	 * {@link Transformation} between "from" and "to" {@link Frame}.
	 *
	 * 'Measured' in this context means the actual value that the Transformation
	 * has. In the context of actuators, this value can differ from the desired
	 * ('commanded') Transformation value, which is obtainable by
	 * {@link Relation#getRelationSensor()}.
	 *
	 * @return {@link RelationSensor} delivering the current measured
	 *         {@link Transformation}
	 */
	public abstract RelationSensor getMeasuredRelationSensor();

	/**
	 * Gets a {@link RelationSensor} that delivers the measured
	 * {@link Transformation} to the given {@link Frame}. This Frame is required to
	 * be either this Relation's "from" or "to" Frame.
	 *
	 * 'Measured' in this context means the actual value that the Transformation
	 * has. In the context of actuators, this value can differ from the desired
	 * ('commanded') Transformation value, which is obtainable by
	 * {@link Relation#getRelationSensorTo(Frame))}.
	 *
	 * @return {@link RelationSensor} delivering the current commanded
	 *         {@link Transformation} to the given Frame
	 */
	public RelationSensor getMeasuredRelationSensorTo(Frame to) {
		if (to != this.getFrom()) {
			return getMeasuredRelationSensor();
		} else if (to == this.getFrom()) {
			return getMeasuredRelationSensor().invert();
		} else {
			throw new IllegalArgumentException("Given Frame must belong to this Relation!");
		}
	}

	/**
	 * Gets a {@link VelocitySensor} which delivers the commanded velocity of this
	 * Relation's "to" frame relative to its "from" Frame.
	 *
	 * The velocity is measured using the "from" Frame's {@link Orientation} and its
	 * {@link Point} as Pivot Point (see {@link VelocitySensor} and {@link Twist}).
	 *
	 * 'Commanded' in this context means the desired value that the velocity should
	 * have. In the context of actuators, this value can differ from the actual
	 * ('measured') velocity value, which is obtainable by
	 * {@link Relation#getMeasuredVelocitySensor()}.
	 *
	 * @return VelocitySensor delivering commanded velocity of "to" Frame relative
	 *         to "from" Frame
	 */
	public abstract VelocitySensor getVelocitySensor();

	/**
	 * Gets a {@link VelocitySensor} which delivers the commanded velocity of the
	 * given {@link Frame} relative to a source Frame. The given Frame is required
	 * to be either this Relation's "from" or "to" Frame. The source Frame is chosen
	 * as the other "end" of this Relation.
	 *
	 * The velocity is measured using the source Frame's {@link Orientation} and its
	 * {@link Point} as Pivot Point (see {@link VelocitySensor} and {@link Twist}).
	 *
	 * 'Commanded' in this context means the desired value that the velocity should
	 * have. In the context of actuators, this value can differ from the actual
	 * ('measured') velocity value, which is obtainable by
	 * {@link Relation#getMeasuredVelocitySensorTo(Frame)}.
	 *
	 * @return VelocitySensor measuring velocity of the given Frame relative to the
	 *         source Frame
	 */
	public VelocitySensor getVelocitySensorTo(Frame to) {
		if (to == this.getTo()) {
			return getVelocitySensor();
		} else if (to == this.getFrom()) {
			return getVelocitySensor().invert();
		} else {
			throw new IllegalArgumentException("Given Frame must belong to this Relation!");
		}
	}

	/**
	 * Gets a {@link VelocitySensor} which delivers the measured velocity of this
	 * Relation's "to" frame relative to its "from" Frame.
	 *
	 * The velocity is measured using the "from" Frame's {@link Orientation} and its
	 * {@link Point} as Pivot Point (see {@link VelocitySensor} and {@link Twist}).
	 *
	 * 'Measured' in this context means the actual value that the velocity has. In
	 * the context of actuators, this value can differ from the desired
	 * ('commanded') velocity value, which is obtainable by
	 * {@link Relation#getVelocitySensor()}.
	 *
	 * @return VelocitySensor delivering measured velocity of "to" Frame relative to
	 *         "from" Frame
	 */
	public abstract VelocitySensor getMeasuredVelocitySensor();

	/**
	 * Gets a {@link VelocitySensor} which delivers the measured velocity of the
	 * given {@link Frame} relative to a source Frame. The given Frame is required
	 * to be either this Relation's "from" or "to" Frame. The source Frame is chosen
	 * as the other "end" of this Relation.
	 *
	 * The velocity is measured using the source Frame's {@link Orientation} and its
	 * {@link Point} as Pivot Point (see {@link VelocitySensor} and {@link Twist}).
	 *
	 * 'Measured' in this context means the actual velocity value. In the context of
	 * actuators, this value can differ from the desired ('commanded') velocity
	 * value, which is obtainable by {@link Relation#getVelocitySensorTo(Frame)}.
	 *
	 * @return VelocitySensor measuring velocity of the given Frame relative to the
	 *         source Frame
	 */
	public VelocitySensor getMeasuredVelocitySensorTo(Frame to) {
		if (to == this.getTo()) {
			return getMeasuredVelocitySensor();
		} else if (to == this.getFrom()) {
			return getMeasuredVelocitySensor().invert();
		} else {
			throw new IllegalArgumentException("Given Frame must belong to this Relation!");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		String name = getName();

		if (name != null) {
			sb.append(" '");
			sb.append(name);
			sb.append("'");
		}
		sb.append(" from '");
		sb.append(name(getFrom()));
		sb.append("' to '");
		sb.append(name(getTo()));
		sb.append("'");

		return sb.toString();
	}

	private String name(Frame f) {
		if (f != null) {
			String name = f.getName();

			if (name != null) {
				return name;
			}
			return "<unnamed frame>";
		}
		return "<null frame>";
	}
}
