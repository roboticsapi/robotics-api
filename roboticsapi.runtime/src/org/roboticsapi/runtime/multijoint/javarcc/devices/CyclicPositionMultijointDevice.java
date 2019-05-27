/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.javarcc.devices;

public class CyclicPositionMultijointDevice {

	int jointCount;

	// data from RPI net
	double[] RPIPos, RPIVel, RPIAcc;
	long[] RPITicks;

	// data from RPI net at last CP cycle
	double[] RPIPos1;
	long[] RPITicks1;
	double[] RPIPos2;
	long[] RPITicks2;

	// data from last CP cycle
	double[] CPPos, CPVel;
	long lastCPTicks, lastRealTicks;
	long tickDiffSum;
	long[] tickDiffs;
	int tickDiffCount, tickDiffPos;

	double[] maxAcc, maxVel;

	public CyclicPositionMultijointDevice(int jointCount, int smoothLength) {
		this.jointCount = jointCount;

		RPIPos = new double[jointCount];
		RPIVel = new double[jointCount];
		RPIAcc = new double[jointCount];
		RPIPos1 = new double[jointCount];
		RPIPos2 = new double[jointCount];
		RPITicks = new long[jointCount];
		RPITicks1 = new long[jointCount];
		RPITicks2 = new long[jointCount];

		CPPos = new double[jointCount];
		CPVel = new double[jointCount];

		maxAcc = new double[jointCount];
		maxVel = new double[jointCount];

		lastCPTicks = lastRealTicks = 0L;

		tickDiffs = new long[smoothLength];
		for (int i = 0; i < smoothLength; i++) {
			tickDiffs[i] = 0L;
		}
		tickDiffCount = smoothLength;

		resetTimeHistory();
	}

	public void setJointPosition(int joint, double position, long time) {
		if (joint < 0 || joint >= jointCount)
			return;

		// calculate velocity and acceleration
		double dt = (time - RPITicks[joint]) / 1e3;
		double jointv = (position - RPIPos[joint]) / dt;
		double jointa = (jointv - RPIVel[joint]) / dt;

		RPIVel[joint] = jointv;
		RPIAcc[joint] = jointa;

		RPITicks[joint] = time;
		RPIPos[joint] = position;
	}

	public void setJointPositionStatic(int joint, double position) {
		if (joint < 0 || joint >= jointCount)
			return;
		CPPos[joint] = RPIPos[joint] = position;
		RPIVel[joint] = 0;
		RPIAcc[joint] = 0;
	}

	public double getCommandedJointPosition(int joint) {
		return RPIPos[joint];
	}

	public double getCommandedJointVelocity(int joint) {
		return RPIVel[joint];
	}

	public double getCommandedJointAcceleration(int joint) {
		return RPIAcc[joint];
	}

	public void setMaximumAcceleration(int joint, double maxAcc) {
		this.maxAcc[joint] = maxAcc;
	}

	public void setMaximumVelocity(int joint, double maxVel) {
		this.maxVel[joint] = maxVel;
	}

	public void setMaximumAcceleration(double... maxAccs) {
		for (int i = 0; i < maxAccs.length; i++)
			setMaximumAcceleration(i, maxAccs[i]);
	}

	public void setMaximumVelocity(double... maxVels) {
		for (int i = 0; i < maxVels.length; i++) {
			setMaximumVelocity(i, maxVels[i]);
		}
	}

	public double getMaximumAcceleration(int joint) {
		return maxAcc[joint];
	}

	public double getMaximumVelocity(int joint) {
		return maxVel[joint];
	}

	public void resetTimeHistory() {
		tickDiffPos = -1;
		tickDiffSum = 0L;
	}

	public void requestData() {
		requestData(System.currentTimeMillis());
	}

	double cycletime;

	public void requestData(long realTicks) {
		long CPTicks;
		if (tickDiffCount > 0) {
			if (tickDiffPos > -1) {
				long tickDiff = realTicks - lastRealTicks;
				if (tickDiffPos >= tickDiffCount)
					tickDiffSum -= tickDiffs[tickDiffPos % tickDiffCount];
				tickDiffs[tickDiffPos % tickDiffCount] = tickDiff;
				tickDiffSum += tickDiff;
				if (tickDiffPos >= tickDiffCount)
					CPTicks = lastCPTicks + tickDiffSum / tickDiffCount;
				else
					CPTicks = realTicks;
			} else {
				CPTicks = realTicks;
			}
		} else {
			CPTicks = realTicks;
		}
		cycletime = Math.max(1e-3, (CPTicks - lastCPTicks) / 1e3);
		lastRealTicks = realTicks;
		lastCPTicks = CPTicks;
		tickDiffPos++;

		for (int i = 0; i < jointCount; i++) {

			double lastCPPos = CPPos[i];

			if (RPITicks1[i] < RPITicks[i]) {
				RPITicks2[i] = RPITicks1[i];
				RPIPos2[i] = RPIPos1[i];
				RPITicks1[i] = RPITicks[i];
				RPIPos1[i] = RPIPos[i];
			}

			if (Math.abs(RPITicks1[i] - CPTicks) / 1e3 < cycletime * 10000) {
				// If we have values from RPI that are newer than last time,
				// and the values are somewhat recent (less than 10 cycles old),
				// use values from RPI net
				if (RPITicks1[i] != RPITicks2[i] && tickDiffCount > 0
						&& Math.abs(RPITicks1[i] - CPTicks) / 1e3 < cycletime * 10) {
					// we want smoothing
					CPPos[i] = RPIPos2[i]
							+ (RPIPos1[i] - RPIPos2[i]) * (CPTicks - RPITicks2[i]) / (RPITicks1[i] - RPITicks2[i]);

				} else {
					// just send the data from RPI
					CPPos[i] = RPIPos[i];
				}
			} else // no values have been provided by RPI net, brake to
					// standstill
			{
				double delta = getMaximumAcceleration(i) * cycletime * cycletime;

				if (Math.abs(CPVel[i] * cycletime) > delta) {
					double pos = lastCPPos + CPVel[i] * cycletime - delta * ((CPVel[i] > 0) ? 1 : -1);
					setJointPositionStatic(i, pos);
				} else {
					setJointPositionStatic(i, CPPos[i]);
				}
				RPIVel[i] = 0;
			}
			CPVel[i] = (CPPos[i] - lastCPPos) / cycletime;
		}

	}

	public double[] getVelocityToCommand() {
		return CPVel;
	}

	public double[] getPositionToCommand() {
		return CPPos;
	}

	public double getCycleTime() {
		return cycletime;
	}

}
