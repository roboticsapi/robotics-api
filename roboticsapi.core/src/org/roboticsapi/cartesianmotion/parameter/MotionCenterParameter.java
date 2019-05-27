/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.parameter;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.world.Frame;

/**
 * Parameters for the center of a motion
 */
public class MotionCenterParameter implements DeviceParameters {

	private Frame motionCenter;

	/**
	 * Motion center parameters
	 * 
	 * @param motionCenter center frame of the motion
	 */
	public MotionCenterParameter(final Frame motionCenter) {
		setMotionCenter(motionCenter);
	}

	/**
	 * Retrieves the center of the motion
	 * 
	 * @return motion center frame
	 */
	public Frame getMotionCenter() {
		return motionCenter;
	}

	@ConfigurationProperty
	public void setMotionCenter(final Frame motionCenter) {
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
