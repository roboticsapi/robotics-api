/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityProperty;
import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.FromCommandActivity;
import org.roboticsapi.activity.ParallelRtActivity;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.action.JointReset;

public class SimulatedJointMotionInterfaceImpl<T extends MultiJointDevice> extends ActuatorInterfaceImpl<T>
		implements SimulatedJointMotionInterface {

	public SimulatedJointMotionInterfaceImpl(T device) {
		super(device);
	}

	@Override
	public final RtActivity resetJoints(final double[] newPositions, DeviceParameters... parameters)
			throws RoboticsException {
		ParallelRtActivity srta = new ParallelRtActivity("Reset all joints") {
			@Override
			protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
					throws RoboticsException, ActivityNotCompletedException {
				for (ActivityProperty prop : createProperties(newPositions)) {
					addProperty(getDevice(), prop);
				}
				Set<Device> prepareMultipleActivities = super.prepareMultipleActivities(prevActivities);
				prepareMultipleActivities.add(getDevice());
				return prepareMultipleActivities;
			}
		};
		for (int jointIndex = 0; jointIndex < newPositions.length; jointIndex++) {
			srta.addActivity(createResetJointActivity(jointIndex, newPositions[jointIndex],
					new ArrayList<ActivityProperty>(), parameters));
		}
		srta.addAdditionalAffectedDevice(getDevice());
		return srta;
	}

	private final RtActivity createResetJointActivity(final int jointIndex, final double newPosition,
			List<ActivityProperty> properties, DeviceParameters... parameters) throws RoboticsException {
		final Joint joint = getDevice().getJoint(jointIndex);
		Action action = new JointReset(newPosition);
		RuntimeCommand cmd = joint.getDriver().getRuntime().createRuntimeCommand(joint, action);
		return new FromCommandActivity(cmd, joint) {
			@Override
			public Set<Device> prepare(Map<Device, Activity> prevActivities)
					throws RoboticsException, ActivityNotCompletedException {
				Set<Device> prepare = super.prepare(prevActivities);
				if (prepare == null) {
					prepare = new HashSet<Device>();
				}
				prepare.add(joint);
				return prepare;
			}
		};
	}

	protected List<ActivityProperty> createProperties(double[] newPositions) throws RoboticsException {
		List<ActivityProperty> list = new ArrayList<ActivityProperty>();
		list.add(new JointGoalProperty(newPositions));
		return list;
	}

}
