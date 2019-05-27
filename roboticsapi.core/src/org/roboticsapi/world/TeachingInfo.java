/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.Device;

public class TeachingInfo {
	private final Device device;
	private final Frame motionCenter;
	private final double[] hintParameters;

	public TeachingInfo(Device device, Frame motionCenter, double[] hintParameters) {
		this.device = device;
		this.motionCenter = motionCenter;
		this.hintParameters = hintParameters.clone();
	}

	public Device getDevice() {
		return device;
	}

	public Frame getMotionCenter() {
		return motionCenter;
	}

	public double[] getHintParameters() {
		return hintParameters;
	}
}
