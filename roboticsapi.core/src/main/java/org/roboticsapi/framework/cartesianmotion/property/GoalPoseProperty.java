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
import org.roboticsapi.core.world.realtimevalue.RealtimePose;

public class GoalPoseProperty implements ActivityProperty {
	private final Pose motionCenter;
	private final RealtimePose pose;

	public GoalPoseProperty(RealtimePose pose, Pose motionCenter) {
		this.pose = pose;
		this.motionCenter = motionCenter;
	}

	public GoalPoseProperty(Frame reference, Transformation transformation, Pose motionCenter) {
		this(RealtimePose.createFromTransformation(reference, transformation.asRealtimeValue()), motionCenter);
	}

	public GoalPoseProperty(Frame pose, Frame motionCenter) {
		this(pose.asRealtimePose(), motionCenter.asPose());
	}

	public RealtimePose getPose() {
		return pose;
	}

	public Pose getMotionCenter() {
		return motionCenter;
	}
}
