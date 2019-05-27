/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.xml;

public class TeachingInfoWithFrame {
	private final String deviceName;
	private final String motionCenterName;
	private final double[] hintParameters;
	private final String frame;

	public TeachingInfoWithFrame(String deviceName, String motionCenterName, double[] hintParameters, String frame) {
		this.deviceName = deviceName;
		this.motionCenterName = motionCenterName;
		this.hintParameters = hintParameters;
		this.frame = frame;

	}

	public String getFrame() {
		return frame;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getMotionCenterName() {
		return motionCenterName;
	}

	public double[] getHintParameters() {
		return hintParameters;
	}
}
