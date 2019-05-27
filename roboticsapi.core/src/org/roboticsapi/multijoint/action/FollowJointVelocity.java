/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.MultiJointDevice;

/**
 * The {@link FollowJointVelocity} action lets an {@link MultiJointDevice}
 * follow the joint velocities given by a set of {@link DoubleSensor}s. An
 * online planner is used to interpolate the movement. The list of
 * {@link Sensor}s given is mapped to the actuator's joints in the order they
 * appear.
 */
public class FollowJointVelocity extends Action implements ProcessAction, JointSpaceAction {

	private final DoubleSensor[] _sensors;

	private final Double[] _limits;

	public FollowJointVelocity(DoubleSensor[] sensors) {
		this(sensors, null);
	}

	public FollowJointVelocity(DoubleSensor[] sensors, double[] limits) {
		super(0);

		this._sensors = new DoubleSensor[sensors.length];
		this._limits = new Double[sensors.length];

		for (int i = 0; i < sensors.length; i++) {
			_sensors[i] = sensors[i];

			if (limits != null) {
				_limits[i] = Math.abs(limits[i]);
			} else {
				_limits[i] = null;
			}
		}
	}

	public int getJointCount() {
		return _sensors.length;
	}

	public DoubleSensor getSensor(int axis) {
		return _sensors[axis];
	}

	public Double getLimit(int axis) {
		return _limits[axis];
	}

}
