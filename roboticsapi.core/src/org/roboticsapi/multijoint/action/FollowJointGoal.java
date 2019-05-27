/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.sensor.DoubleSensor;

/**
 * The FollowJointGoal Action lets an Actuator follow the joint positions given
 * by a set of Sensors. An online planner is used to interpolate the movement.
 * The list of Sensors given is mapped to the actuator's joints in the order
 * they appear.
 */
public class FollowJointGoal extends Action implements GoalAction, JointSpaceAction {

	private final DoubleSensor[] sensors;

	/**
	 * Instantiates a new FollowJointGoal action.
	 * 
	 * @param sensors the sensors that determine the axis target positions.
	 */
	public FollowJointGoal(DoubleSensor... sensors) {
		super(0);
		if (sensors == null) {
			throw new IllegalArgumentException("sensors");
		}
		this.sensors = sensors;
	}

	/**
	 * Gets the sensors used in this FollowJointGoal.
	 * 
	 * @return the sensors
	 */
	public DoubleSensor[] getSensors() {
		return sensors;
	}

	@Override
	public List<ActionRealtimeException> defineActionExceptions() {
		List<ActionRealtimeException> exceptions = super.defineActionExceptions();
		exceptions.add(new ActionCancelledException(this));

		return exceptions;
	}

}
