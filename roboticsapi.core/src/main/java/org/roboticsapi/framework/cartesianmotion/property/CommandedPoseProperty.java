/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.property;

import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;

public class CommandedPoseProperty implements ActivityProperty {
	private final Pose pose, motionCenter;

	public CommandedPoseProperty(Pose pose, Pose motionCenter) {
		this.pose = pose;
		this.motionCenter = motionCenter;
	}

	public CommandedPoseProperty(Frame reference, Transformation transformation, Pose motionCenter) {
		this(new Pose(reference, transformation), motionCenter);
	}

	public CommandedPoseProperty(Frame pose, Frame motionCenter) {
		this(pose.asPose(), motionCenter.asPose());
	}

	public Pose getPose() {
		return pose;
	}

	public Pose getMotionCenter() {
		return motionCenter;
	}

}
