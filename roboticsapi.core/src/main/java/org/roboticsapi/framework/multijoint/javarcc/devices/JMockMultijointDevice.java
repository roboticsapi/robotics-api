/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.devices;

import org.roboticsapi.facet.javarcc.devices.AbstractJDevice;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JMultijointInterface;

public class JMockMultijointDevice extends AbstractJDevice implements JMultijointInterface {
	private CyclicPositionMultijointDevice cpra;
	private int jointCount;
	private double[] maxAngle;
	private double[] minAngle;

	public JMockMultijointDevice(int jointCount, double[] minAngles, double[] maxAngles, double[] initialAngles,
			double[] maxVel, double[] maxAcc) {
		this.jointCount = jointCount;
		this.minAngle = minAngles;
		this.maxAngle = maxAngles;
		cpra = new CyclicPositionMultijointDevice(jointCount, 0);
		cpra.setMaximumVelocity(maxVel);
		cpra.setMaximumAcceleration(maxAcc);
		for (int i = 0; i < jointCount; i++) {
			cpra.setJointPositionStatic(i, initialAngles[i]);
		}
	}

	@Override
	public int getJointCount() {
		return jointCount;
	}

	@Override
	public int getJointError(int axis) {
		return 0;
	}

	@Override
	public double getMeasuredJointAcceleration(int axis) {
		return cpra.getCommandedJointAcceleration(axis);
	}

	@Override
	public double getMeasuredJointVelocity(int axis) {
		return cpra.getCommandedJointVelocity(axis);
	}

	@Override
	public double getMeasuredJointPosition(int axis) {
		return cpra.getCommandedJointPosition(axis);
	}

	@Override
	public double getCommandedJointAcceleration(int axis) {
		return cpra.getCommandedJointAcceleration(axis);
	}

	@Override
	public double getCommandedJointVelocity(int axis) {
		return cpra.getCommandedJointVelocity(axis);
	}

	@Override
	public double getCommandedJointPosition(int axis) {
		return cpra.getCommandedJointPosition(axis);
	}

	@Override
	public int checkJointPosition(int axis, double pos) {
		if (pos != pos) // NaN
			return 1;

		if (axis < 0 || axis >= getJointCount())
			return 1;

		if (pos > maxAngle[axis] || pos < minAngle[axis])
			return 2;

		return 0;
	}

	@Override
	public void setJointPosition(int axis, double pos, Long time) {
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
