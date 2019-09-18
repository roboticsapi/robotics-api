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
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;
import org.roboticsapi.framework.multijoint.property.CommandedPositionProperty;
import org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty;
import org.roboticsapi.framework.multijoint.property.GoalProperty;
import org.roboticsapi.framework.robot.RobotArm;

public class CartesianMotionWithJointMetadata<T extends ActuatorInterface> extends ModifiedActuatorInterface<T> {

	private static final ActivityPropertyProvider<GoalProperty> CARTESIAN_GOALPOSE_2_JOINT_GOAL = new ActivityPropertyProvider<GoalProperty>() {

		@Override
		public Class<GoalProperty> getProvidedType() {
			return GoalProperty.class;
		}

		@Override
		public List<Class<? extends ActivityProperty>> getDependencies() {
			List<Class<? extends ActivityProperty>> ret = new ArrayList<Class<? extends ActivityProperty>>();
			ret.add(GoalPoseProperty.class);
			return ret;
		}

		@Override
		public GoalProperty provideProperty(Device device,
				Map<Class<? extends ActivityProperty>, ActivityProperty> properties) {
			RobotArm arm = (RobotArm) device;
			GoalPoseProperty pose = (GoalPoseProperty) properties.get(GoalPoseProperty.class);
			if (pose == null) {
				return null;
			}
			try {
				RealtimeTransformation absPose = new RealtimePoseCoincidence(pose.getPose(),
						pose.getMotionCenter().asRealtimeValue()).getTransformation(arm.getBase(), arm.getFlange(),
								World.getCommandedTopology());
				if (absPose.isConstant()) {
					return new GoalProperty(arm.getInverseKinematics(absPose.getCurrentValue(), arm.getJointAngles(),
							arm.getDefaultParameters().withParameters(new MotionCenterParameter(arm.getFlange()))));
				}
			} catch (RoboticsException e1) {
				return null;
			}
			return null;
		}
	};
	private static final ActivityPropertyProvider<CommandedPositionProperty> CARTESIAN_COMMANDEDPOSE_2_JOINT_COMMANDEDPOSITION = new ActivityPropertyProvider<CommandedPositionProperty>() {

		@Override
		public Class<CommandedPositionProperty> getProvidedType() {
			return CommandedPositionProperty.class;
		}

		@Override
		public List<Class<? extends ActivityProperty>> getDependencies() {
			List<Class<? extends ActivityProperty>> ret = new ArrayList<Class<? extends ActivityProperty>>();
			ret.add(CommandedPoseProperty.class);
			return ret;
		}

		@Override
		public CommandedPositionProperty provideProperty(Device device,
				Map<Class<? extends ActivityProperty>, ActivityProperty> properties) {
			RobotArm arm = (RobotArm) device;
			CommandedPoseProperty pose = (CommandedPoseProperty) properties.get(CommandedPoseProperty.class);
			if (pose == null) {
				return null;
			}
			try {
				RealtimeTransformation absPose = new RealtimePoseCoincidence(pose.getPose().asRealtimeValue(),
						pose.getMotionCenter().asRealtimeValue()).getTransformation(arm.getBase(), arm.getFlange(),
								World.getCommandedTopology());
				if (absPose.isConstant()) {
					return new CommandedPositionProperty(arm.getInverseKinematics(absPose.getCurrentValue(),
							arm.getJointAngles(),
							arm.getDefaultParameters().withParameters(new MotionCenterParameter(arm.getFlange()))));
				}
			} catch (RoboticsException e) {
			}
			return null;
		}
	};
	private static final ActivityPropertyProvider<CommandedVelocityProperty> CARTESIAN_COMMANDEDVELOCITY_2_JOINT_COMMANDEDVELOCITY = new ActivityPropertyProvider<CommandedVelocityProperty>() {

		@Override
		public Class<CommandedVelocityProperty> getProvidedType() {
			return CommandedVelocityProperty.class;
		}

		@Override
		public List<Class<? extends ActivityProperty>> getDependencies() {
			List<Class<? extends ActivityProperty>> ret = new ArrayList<Class<? extends ActivityProperty>>();
			ret.add(org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty.class);
			return ret;
		}

		@Override
		public CommandedVelocityProperty provideProperty(Device device,
				Map<Class<? extends ActivityProperty>, ActivityProperty> properties) {
			RobotArm arm = (RobotArm) device;
			org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty vel = (org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty) properties
					.get(org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty.class);
			if (vel == null) {
				return null;
			}

			if (arm.getBase().getRelationsTo(vel.getVelocity().getReferenceFrame(),
					World.getCommandedTopology().withoutDynamic()) == null) {
				return null;
			}
			if (arm.getFlange().getRelationsTo(vel.getMotionCenter().getReference(),
					World.getCommandedTopology().withoutDynamic()) == null) {
				return null;
			}

			if (vel.getVelocity().getTwist().getTransVel().isNullVector()
					&& vel.getVelocity().getTwist().getRotVel().isNullVector()) {
				return new CommandedVelocityProperty(new double[arm.getJointCount()]);
			}
			return null;
		}
	};

	public static void addProviders(Activity a) {
		a.addPropertyProvider(CARTESIAN_COMMANDEDPOSE_2_JOINT_COMMANDEDPOSITION);
		a.addPropertyProvider(CARTESIAN_COMMANDEDVELOCITY_2_JOINT_COMMANDEDVELOCITY);
		a.addPropertyProvider(CARTESIAN_GOALPOSE_2_JOINT_GOAL);
	}

	public CartesianMotionWithJointMetadata(T instance) {
		super(instance);
	}

}