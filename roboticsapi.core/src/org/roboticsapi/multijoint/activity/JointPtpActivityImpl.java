/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import java.util.List;

import org.roboticsapi.activity.AbstractPlannedRtActivity;
import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityStatus;
import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.multijoint.action.JointErrorCorrection;
import org.roboticsapi.multijoint.action.JointMotionPlan;
import org.roboticsapi.multijoint.action.PTP;
import org.roboticsapi.multijoint.action.PTPFromMotion;
import org.roboticsapi.multijoint.parameter.BlendingParameter;

abstract class JointPtpActivityImpl extends AbstractPlannedRtActivity<MultiJointDeviceDriver, MultiJointDevice>
		implements PlannedRtActivity {
	private final MultiJointDevice robot;
	private PTP regularPTP = null;
	private PTPFromMotion blendedPTP = null;

	private final DeviceParameterBag parameters;

	protected JointPtpActivityImpl(String name, MultiJointDevice robot, DeviceParameterBag parameters) {
		super(name, robot);
		this.robot = robot;
		this.parameters = parameters;
	}

	protected JointPtpActivityImpl(MultiJointDevice robot, DeviceParameterBag parameters) {
		this(robot.getName() + ".ptp()", robot, parameters);
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		boolean canTakeover = false;

		BlendingParameter blendingParameter = parameters.get(BlendingParameter.class);

		// event triggering start of blending
		State blendStartEvent = null;
		if (blendingParameter != null) {
			blendStartEvent = getMotionTimeProgress(blendingParameter.getAtProgress());
		}

		// if the predecessing Activity provides its motion goal, we prepare for
		// blending
		if (prevActivity != null && prevActivity.getProperty(getDevice(), JointGoalProperty.class) != null) {

			double[] prevGoal = prevActivity.getProperty(getDevice(), JointGoalProperty.class).getGoal();

			// we prepare one PTP that does regular motion without blending...
			regularPTP = new PTP(prevGoal, getTarget(prevGoal));

			// additionally, if the predecessing Activity provides
			// information about its state at the given blending condition,
			// we can actually use a planned blending PTP motion
			BlendingStartJointStatusProperty blendStart = prevActivity.getProperty(getDevice(),
					BlendingStartJointStatusProperty.class);
			if (blendStart != null) {
				blendedPTP = new PTPFromMotion(prevGoal, getTarget(prevGoal), blendStart.getPos(), blendStart.getVel());

				canTakeover = true;
			} else {
				// otherwise we cannot blend.
				canTakeover = false;
			}

		} else if (prevActivity != null && prevActivity.getStatus() == ActivityStatus.RUNNING) {

			// being scheduled after an activity that does not provide a motion
			// target is considered an error case at the moment
			throw new ActivityNotCompletedException(prevActivity);
		} else {

			// if there was no previous activity, we just take the robot's
			// current state as start point
			regularPTP = new PTP(robot.getJointAngles(), getTarget(robot.getJointAngles()));
		}

		// create Command for the regular PTP
		RuntimeCommand regularCmd = robot.getDriver().getRuntime().createRuntimeCommand(robot, regularPTP, parameters);

		// if we have prepared for blending over the predecessing activity,
		// we also process the PTPs for planned and spontaneous blending
		if (blendedPTP != null) {
			// we use a TransactionCommand for implementing a choice between
			// the three PTP Commands, depending on the actual state of things
			TransactionCommand trans = robot.getDriver().getRuntime().createTransactionCommand(regularCmd);

			// if we reached the start point of our regular PTP (i.e. the
			// previous motion was executed completely) this PTP is the one to
			// start
			BooleanSensor regularCondition = createPositionComparer(robot.getJoints(), regularPTP.getFrom());
			trans.addStartCommand(regularCondition, regularCmd);

			// process the planned PTP if we have one
			Command plannedBlendedCmd = robot.getDriver().getRuntime().createRuntimeCommand(robot,
					new JointErrorCorrection(blendedPTP, robot.getJoints(), blendedPTP.getRealStart()), parameters);
			// Command plannedBlendedCmd = robot.getRuntime()
			// .createRuntimeCommand(robot, blendedPTP, parameters);

			// for all MotionTimePercentStates that were requested by
			// anyone, we raise the according Action state
			bindProgressStates(blendedPTP);
			bindProgressStates(regularPTP);
			// for (MotionTimeProgressState s :
			// getRelevantMotionTimeProgressStates()) {
			// s.setOther(blendedPTP.getMotionTimeProgress((float) s.progress)
			// .or(regularPTP
			// .getMotionTimeProgress((float) s.progress)));
			// }

			// if we reached the start point of our planned PTP (i.e. the
			// previous motion was executed completely) this PTP is the one
			// to start
			trans.addStartCommand(regularCondition.not(), plannedBlendedCmd);

			// if we may be blended, allow this at Command level
			if (blendStartEvent != null) {
				trans.addTakeoverAllowedCondition(blendStartEvent.and(trans.getCancelState().not()));
			}

			// forward cancel to both inner Commands
			trans.addStateEnteredHandler(trans.getCancelState().and(regularCmd.getActiveState()),
					new CommandCanceller(regularCmd));
			trans.addStateEnteredHandler(trans.getCancelState().and(plannedBlendedCmd.getActiveState()),
					new CommandCanceller(plannedBlendedCmd));

			setCommand(trans, prevActivity);
		} else { // previous Activity does not allow to be taken over, so we
			// just prepare a standard PTP
			// for all MotionTimePercentStates that were requested by anyone,
			// we raise the according Action state
			bindProgressStates(regularPTP);
			// for (MotionTimeProgressState s :
			// getRelevantMotionTimeProgressStates()) {
			// s.setOther(regularPTP.getMotionTimeProgress((float) s.progress));
			// }

			// if we may be blended, allow this at Command level
			if (blendStartEvent != null) {
				regularCmd.addTakeoverAllowedCondition(blendStartEvent.and(regularCmd.getCancelState().not()));
			}

			setCommand(regularCmd, prevActivity);
		}

		finishProgressStates();

		// provide information about our own motion goal
		addProperty(robot, new JointGoalProperty(regularPTP.getTo()));

		BlendingStartJointStatusProperty jp = getBlendingStartJointStatusProperty(regularCmd.getPlan(regularPTP),
				blendingParameter);
		if (jp != null) {
			addProperty(robot, jp);
		}
		return canTakeover;
	}

	private BooleanSensor createPositionComparer(List<? extends Joint> joints, double[] from) {
		BooleanSensor sensor = null;

		for (int i = 0; i < joints.size(); i++) {

			BooleanSensor equals = joints.get(i).getCommandedPositionSensor().equals(DoubleSensor.fromValue(from[i]),
					0.001);

			if (sensor != null) {
				sensor = sensor.and(equals);
			} else {
				sensor = equals;
			}
		}

		return sensor;
	}

	protected BlendingStartJointStatusProperty getBlendingStartJointStatusProperty(JointMotionPlan plan,
			BlendingParameter blendingParameter) throws RoboticsException {
		if (plan == null) {
			return null;
		}
		if (blendingParameter == null) {
			return null;
		}

		Double blendStartTime = plan
				.getTimeAtFirstOccurence(regularPTP.getMotionTimeProgress((float) blendingParameter.getAtProgress()));

		if (blendStartTime == null) {
			return null;
		}

		final double[] blendPos = blendStartTime != null ? plan.getJointPositionsAt(blendStartTime) : null;
		final double[] blendVel = blendStartTime != null ? plan.getJointVelocitiesAt(blendStartTime) : null;
		return new BlendingStartJointStatusProperty(blendPos, blendVel);
	}

	protected abstract double[] getTarget(double[] from) throws RoboticsException;

}