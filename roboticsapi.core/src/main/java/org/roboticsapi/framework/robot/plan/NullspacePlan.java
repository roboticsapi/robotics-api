/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.plan;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.framework.cartesianmotion.action.CartesianMotionPlan;
import org.roboticsapi.framework.multijoint.action.JointMotionPlan;
import org.roboticsapi.framework.robot.RobotArm;

public class NullspacePlan implements CartesianMotionPlan, JointMotionPlan {
	private final CartesianMotionPlan cartesianPlan;
	private final JointMotionPlan nullspacePlan;
	private final RobotArm robotArm;
	private final Pose motionCenter;

	public NullspacePlan(CartesianMotionPlan cartesianPlan, JointMotionPlan nullspacePlan, RobotArm robot,
			Pose motionCenter) {
		this.cartesianPlan = cartesianPlan;
		this.nullspacePlan = nullspacePlan;
		this.robotArm = robot;
		this.motionCenter = motionCenter;
	}

	@Override
	public double getTotalTime() {
		return cartesianPlan.getTotalTime();
	}

	@Override
	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
		return cartesianPlan.getTimeAtFirstOccurence(state);
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		try {
			Pose point = cartesianPlan.getBaseFrame().asPose().plus(cartesianPlan.getTransformationAt(time));
			return robotArm.getInverseKinematics(point, motionCenter, nullspacePlan.getJointPositionsAt(time));
		} catch (RoboticsException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		double dt = 0.01;
		double[] start = getJointPositionsAt(time);
		double[] next = getJointPositionsAt(time + dt);
		if (start == null || next == null) {
			return null;
		}
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = (next[i] - start[i]) / dt;
		}
		return ret;
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		double dt = 0.01;
		double[] start = getJointVelocitiesAt(time);
		double[] next = getJointVelocitiesAt(time + dt);
		if (start == null || next == null) {
			return null;
		}
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = (next[i] - start[i]) / dt;
		}
		return ret;
	}

	@Override
	public Frame getBaseFrame() {
		return cartesianPlan.getBaseFrame();
	}

	@Override
	public Transformation getTransformationAt(double time) {
		return cartesianPlan.getTransformationAt(time);
	}

	@Override
	public Twist getTwistAt(double time) {
		return cartesianPlan.getTwistAt(time);
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		return null;
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		return null;
	}

}