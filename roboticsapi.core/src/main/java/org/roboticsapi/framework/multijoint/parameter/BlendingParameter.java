/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.parameter;

import org.roboticsapi.core.DeviceParameters;

/**
 * A BlendingParameter specifies at which progress a motion should be blended
 * into a successive motion.
 */
public class BlendingParameter implements DeviceParameters {

	private final double atProgress;

	/**
	 * Creates a new Blending Parameter that triggers blending at a specified motion
	 * progress.
	 * 
	 * @param atProgress the motion progress after which to start blending (range
	 *                   0...1)
	 */
	public BlendingParameter(double atProgress) {
		if (atProgress < 0 || atProgress > 1) {
			throw new IllegalArgumentException("Parameter 'atProgress' may only have values between 0 and 1");
		}

		this.atProgress = atProgress;

	}

	/**
	 * Gets the value indicating the motion progress at which blending should start.
	 * 
	 * @return the motion progress after which to start blending
	 */
	public double getAtProgress() {
		return atProgress;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof BlendingParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		BlendingParameter bound = (BlendingParameter) boundingObject;

		return atProgress <= bound.atProgress;
	}

}
