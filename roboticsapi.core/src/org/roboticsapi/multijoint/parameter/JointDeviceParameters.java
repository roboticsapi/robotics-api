/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.parameter;

import org.roboticsapi.core.DeviceParameters;

/**
 * Device parameters for a device consisting of multiple joints
 */
public class JointDeviceParameters implements DeviceParameters {
	JointParameters[] jointParameters;

	/**
	 * Joint device parameters
	 * 
	 * @param jointParameters parameters of each individual joint
	 */
	public JointDeviceParameters(final JointParameters... jointParameters) {
		this.jointParameters = jointParameters;
	}

	/**
	 * Retrieves the number of joints
	 * 
	 * @return number of joints
	 */
	public int getJointCount() {
		return jointParameters.length;
	}

	/**
	 * Retrieves the parameters for a given joint
	 * 
	 * @param nr joint number
	 * @return joint parameters for this joint
	 */
	public JointParameters getJointParameters(final int nr) {
		return jointParameters[nr];
	}

	public double getMaximumCommonVelocity() {
		double vel = Double.MAX_VALUE;

		for (JointParameters p : jointParameters) {
			vel = Math.min(vel, p.getMaximumVelocity());
		}

		return vel;
	}

	public double getMaximumCommonAcceleration() {
		double acc = Double.MAX_VALUE;

		for (JointParameters p : jointParameters) {
			acc = Math.min(acc, p.getMaximumAcceleration());
		}

		return acc;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof JointDeviceParameters)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		JointDeviceParameters bounds = (JointDeviceParameters) boundingObject;

		int maxI = Math.max(getJointCount(), bounds.getJointCount());

		for (int i = 0; i < maxI; i++) {
			if (!(getJointParameters(i).respectsBounds(bounds.getJointParameters(i)))) {
				return false;
			}
		}

		return true;
	}
}
