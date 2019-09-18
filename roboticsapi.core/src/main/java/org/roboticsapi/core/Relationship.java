/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Interface for a relationship between two {@link RoboticsObject}s in the
 * Robotics API.
 */
public interface Relationship {

	/**
	 * The first, 'source' {@link RoboticsObject} of the {@link Relationship}
	 *
	 * @return the 'source' object
	 */
	RoboticsObject getFrom();

	/**
	 * The second, 'target' {@link RoboticsObject} of the {@link Relationship}
	 *
	 * @return the 'target' object
	 */
	RoboticsObject getTo();

	/**
	 * Either first, 'source' {@link RoboticsObject}, or second, 'target'
	 * {@link RoboticsObject} of the {@link Relationship}
	 *
	 * @param other
	 * @return
	 * @throws IllegalArgumentException
	 */
	RoboticsObject getOther(RoboticsObject other) throws IllegalArgumentException;

	/**
	 * Establishes the {@link Relationship}, i.e. causes it to be returned when
	 * querying the {@link Relationship} of a {@link RoboticsObject}.
	 */
	void establish();

	/**
	 * Checks if the {@link Relationship} can be established in the given situation
	 *
	 * @param situation situation to check the {@link Relationship} for
	 * @return <code>true</code> if the {@link Relationship} may be established,
	 *         <code>false</code> otherwise (e.g. if establishing it would lead to
	 *         an inconsistent situation)
	 */
	boolean canEstablish(RelationshipChangeSet situation);

	/**
	 * Removes the {@link Relationship}, i.e. it is no longer returned when querying
	 * the {@link Relationship} of a {@link RoboticsObject}.
	 */
	void remove();

	/**
	 * Checks if the {@link Relationship} can be removed in the given situation
	 *
	 * @param situation situation to check the {@link Relationship} for
	 * @return <code>true</code> if the {@link Relationship} may be removed,
	 *         <code>false</code> otherwise (e.g. if some external influence
	 *         enforces the {@link Relationship} to be present)
	 */
	boolean canRemove(RelationshipChangeSet situation);
}
