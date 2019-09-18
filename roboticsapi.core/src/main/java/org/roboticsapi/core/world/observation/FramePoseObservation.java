/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.observation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

public class FramePoseObservation extends Observation {

	private final RealtimePose pose;
	private final RealtimeVelocity twist;

	public FramePoseObservation(Frame from, Frame to, RealtimePose pose, RealtimeVelocity twist) {
		super(from, to);
		this.pose = pose;
		this.twist = twist;
	}

	public RealtimePose getPose() {
		return pose;
	}

	public RealtimeVelocity getTwist() {
		return twist;
	}

}
