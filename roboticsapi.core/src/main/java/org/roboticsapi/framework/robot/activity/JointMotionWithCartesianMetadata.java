/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.core.activity.ActivityPropertyProvider;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.activity.ModifiedActuatorInterface;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;
import org.roboticsapi.framework.multijoint.property.CommandedPositionProperty;
import org.roboticsapi.framework.multijoint.property.GoalProperty;
import org.roboticsapi.framework.robot.RobotArm;

public class JointMotionWithCartesianMetadata<T extends ActuatorInterface> extends ModifiedActuatorInterface<T> {

	private static final ActivityPropertyProvider<GoalPoseProperty> JOINT_GOAL_2_CARTESIAN_GOALPOSE = new ActivityPropertyProvider<GoalPoseProperty>() {

		@Override
		public Class<GoalPoseProperty> getProvidedType() {
			return GoalPoseProperty.class;
		}

		@Override
		public List<Class<? extends ActivityProperty>> getDependencies() {
			List<Class<? extends ActivityProperty>> ret = new ArrayList<Class<? extends ActivityProperty>>();
			ret.add(GoalProperty.class);
			return ret;
		}

		@Override
		public GoalPoseProperty provideProperty(Device device,
				Map<Class<? extends ActivityProperty>, ActivityProperty> properties) {
			RobotArm arm = (RobotArm) device;
			GoalProperty pose = (GoalProperty) properties.get(GoalProperty.class);
			if (pose == null) {
				return null;
			}
			double[] goal = pose.getGoal();
			try {
				return new GoalPoseProperty(arm.getBase(), arm.getForwardKinematics(goal), arm.getFlange().asPose());
			} catch (CommunicationException e) {
				return null;
			}
		}
	};

	private static final ActivityPropertyProvider<CommandedPoseProperty> JOINT_COMMANDEDPOSITION_2_CARTESIAN_COMMANDEDPOSE = new ActivityPropertyProvider<CommandedPoseProperty>() {
		@Override
		public Class<CommandedPoseProperty> getProvidedType() {
			return CommandedPoseProperty.class;
		}

		@Override
		public List<Class<? extends ActivityProperty>> getDependencies() {
			List<Class<? extends ActivityProperty>> ret = new ArrayList<Class<? extends ActivityProperty>>();
			ret.add(CommandedPositionProperty.class);
			return ret;
		}

		@Override
		public CommandedPoseProperty provideProperty(Device device,
				Map<Class<? extends ActivityProperty>, ActivityProperty> properties) {
			RobotArm arm = (RobotArm) device;
			CommandedPositionProperty pose = (CommandedPositionProperty) properties
					.get(CommandedPositionProperty.class);
			if (pose == null) {
				return null;
			}
			double[] goal = pose.getPosition();
			try {
				return new CommandedPoseProperty(arm.getBase(), arm.getForwardKinematics(goal),
						arm.getFlange().asPose());
			} catch (CommunicationException e) {
				return null;
			}
		}
	};

	private static final ActivityPropertyProvider<CommandedVelocityProperty> JOINT_COMMANDEDVELOCITY_2_CARTESIAN_COMMANDEDVELOCITY = new ActivityPropertyProvider<CommandedVelocityProperty>() {
		@Override
		public Class<CommandedVelocityProperty> getProvidedType() {
			return CommandedVelocityProperty.class;
		}

		@Override
		public List<Class<? extends ActivityProperty>> getDependencies() {
			List<Class<? extends ActivityProperty>> ret = new ArrayList<Class<? extends ActivityProperty>>();
			ret.add(org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty.class);
			return ret;
		}

		@Override
		public CommandedVelocityProperty provideProperty(Device device,
				Map<Class<? extends ActivityProperty>, ActivityProperty> properties) {
			RobotArm arm = (RobotArm) device;
			org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty pose = (org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty) properties
					.get(org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty.class);
			if (pose == null) {
				return null;
			}
			double[] goal = pose.getVelocity();
			for (double d : goal) {
				if (d != 0) {
					return null;
				}
			}
			return new CommandedVelocityProperty(
					new Velocity(arm.getBase(), null, arm.getBase().asOrientation(), new Twist()),
					arm.getFlange().asPose());
		}

	};

	public static void addProviders(Activity a) {
		a.addPropertyProvider(JOINT_COMMANDEDPOSITION_2_CARTESIAN_COMMANDEDPOSE);
		a.addPropertyProvider(JOINT_COMMANDEDVELOCITY_2_CARTESIAN_COMMANDEDVELOCITY);
		a.addPropertyProvider(JOINT_GOAL_2_CARTESIAN_GOALPOSE);
	}

	public JointMotionWithCartesianMetadata(T instance) {
		super(instance);
	}

}