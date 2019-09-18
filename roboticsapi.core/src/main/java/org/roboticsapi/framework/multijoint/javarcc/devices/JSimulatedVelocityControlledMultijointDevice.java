/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.devices;

import org.roboticsapi.facet.javarcc.devices.PeriodicJDevice;
import org.roboticsapi.facet.javarcc.simulation.SimulationHelper;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JMultijointInterface;

public class JSimulatedVelocityControlledMultijointDevice extends PeriodicJDevice implements JMultijointInterface {

	private final CyclicPositionMultijointDevice cpra;
	private final double[] minAngles, maxAngles;
	private final int jointCount;
	private final double[] msrPos, msrVel;
	private SIVelocityControlledMultijoint sim;
	private double[] P;

	@Override
	public void doPeriodicTask() {
		enterCriticalSection();
		cpra.requestData();
		double[] cmdPos = cpra.getPositionToCommand(), cmdVel = cpra.getVelocityToCommand();
		double cycleTime = cpra.getCycleTime();

		for (int i = 0; i < jointCount; i++) {
			final int ii = i;
			SimulationHelper.callSimulationItem(sim, () -> msrPos[ii] = sim.getMeasuredJointPosition(ii));
			SimulationHelper.callSimulationItem(sim, () -> msrVel[ii] = sim.getMeasuredJointVelocity(ii));
			double err = (msrPos[i] - cmdPos[i]) / cycleTime;

			if (Math.abs(msrPos[i] - cmdPos[i]) > 0.5) {
				SimulationHelper.callSimulationItem(sim, () -> sim.setJointVelocity(ii, 0));
				continue;
			}

			double vel = cmdVel[i];
			double speed = vel - err * P[i];
			SimulationHelper.callSimulationItem(sim, () -> sim.setJointVelocity(ii, speed));
		}
		leaveCriticalSection();
	}

	public JSimulatedVelocityControlledMultijointDevice(String name, int jointCount, double[] minAngles,
			double[] maxAngles, double[] maxVelocities, double[] maxAccelerations, final double[] P, int smoothLength,
			String simulation) {
		super(0.02);
		this.jointCount = jointCount;
		this.P = P;
		if (P.length != jointCount || minAngles.length != jointCount || maxAngles.length != jointCount)
			throw new IllegalArgumentException();

		msrPos = new double[jointCount];
		msrVel = new double[jointCount];
		this.minAngles = minAngles;
		this.maxAngles = maxAngles;
		cpra = new CyclicPositionMultijointDevice(jointCount, smoothLength);

		cpra.setMaximumVelocity(maxVelocities);
		cpra.setMaximumAcceleration(maxAccelerations);

		sim = SimulationHelper.getSimulationItem(SIVelocityControlledMultijoint.class, simulation);
		for (int i = 0; i < jointCount; i++) {
			final int ii = i;
			SimulationHelper.callSimulationItem(sim,
					() -> cpra.setJointPositionStatic(ii, sim.getMeasuredJointPosition(ii)));
		}
	}

	@Override
	public final int getJointCount() {
		return jointCount;
	}

	@Override
	public final int getJointError(int axis) {
		return 0; // TODO:
	}

	@Override
	public final double getMeasuredJointAcceleration(int axis) {
		return 0; // TODO:
	}

	@Override
	public final double getMeasuredJointVelocity(int axis) {
		return msrVel[axis];
	}

	@Override
	public final double getMeasuredJointPosition(int axis) {
		return msrPos[axis];
	}

	@Override
	public final double getCommandedJointAcceleration(int axis) {
		return cpra.getCommandedJointAcceleration(axis);
	}

	@Override
	public final double getCommandedJointVelocity(int axis) {
		return cpra.getCommandedJointVelocity(axis);
	}

	@Override
	public final double getCommandedJointPosition(int axis) {
		return cpra.getCommandedJointPosition(axis);
	}

	@Override
	public final int checkJointPosition(int axis, double pos) {
		if (pos != pos)
			return 1;

		if (axis < 0 || axis > getJointCount() - 1)
			return 1;

		if (pos > maxAngles[axis] || pos < minAngles[axis])
			return 2;

		return 0;
	}

	@Override
	public final void setJointPosition(int axis, double pos, Long time) {
		if (checkJointPosition(axis, pos) != 0)
			return;
		cpra.setJointPosition(axis, pos, time);
	}

	@Override
	public void setToolCOM(RPIVector com, int axis) {
	}

	@Override
	public void setToolMOI(RPIVector moi, int axis) {
	}

	@Override
	public void setToolMass(double massVal, int axis) {
	}

	@Override
	public int getToolError(int axis) {
		return 0;
	}

	@Override
	public boolean getToolFinished(int axis) {
		return true;
	}

}
