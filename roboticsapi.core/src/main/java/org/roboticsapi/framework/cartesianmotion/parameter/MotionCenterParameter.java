/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.parameter;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;

/**
 * Parameters for the center of a motion
 */
public class MotionCenterParameter implements DeviceParameters {

	private Pose motionCenter;

	/**
	 * Motion center parameters
	 *
	 * @param motionCenter center frame of the motion
	 */
	public MotionCenterParameter(final Frame motionCenter) {
		setMotionCenter(motionCenter.asPose());
	}

	public MotionCenterParameter(final Pose motionCenter) {
		setMotionCenter(motionCenter);
	}

	/**
	 * Retrieves the center of the motion
	 *
	 * @return motion center frame
	 */
	public Pose getMotionCenter() {
		return motionCenter;
	}

	public void setMotionCenter(final Pose motionCenter) {
		this.motionCenter = motionCenter;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof MotionCenterParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		return true;
	}
}
