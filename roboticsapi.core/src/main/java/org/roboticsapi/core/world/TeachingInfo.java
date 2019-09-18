/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.Device;

public class TeachingInfo {
	private final Device device;
	private final Pose goal;
	private final Pose motionCenter;
	private final double[] hintParameters;

	public TeachingInfo(Device device, Pose goal, Pose motionCenter, double[] hintParameters) {
		this.device = device;
		this.goal = goal;
		this.motionCenter = motionCenter;
		this.hintParameters = hintParameters.clone();
	}

	public Device getDevice() {
		return device;
	}

	public Pose getMotionCenter() {
		return motionCenter;
	}

	public Pose getGoal() {
		return goal;
	}

	public double[] getHintParameters() {
		return hintParameters;
	}
}
