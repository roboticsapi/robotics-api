/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.ActivityProperty;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Twist;

public class BlendingStartCartesianStatusProperty implements ActivityProperty {

	private final Frame motionCenter;
	private final Frame frame;
	private final Twist twist;

	/**
	 * Creates a new blending start cartesian status property.
	 * 
	 * @param motionCenter the motion center whose status is described
	 * @param frame        the Frame the motion center will be at
	 * @param twist        the twist, expressed relative to frame, the motion center
	 *                     will have
	 */
	public BlendingStartCartesianStatusProperty(Frame motionCenter, Frame frame, Twist twist) {
		this.motionCenter = motionCenter;
		this.frame = frame;
		this.twist = twist;

	}

	public Frame getFrame() {
		return frame;
	}

	public Twist getTwist() {
		return twist;
	}

	public Frame getMotionCenter() {
		return motionCenter;
	}
}
