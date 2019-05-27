/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.parameter;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.robot.RedundancyStrategy;

/**
 * Device parameters setting a redundancy strategy.
 */
public class RedundancyDeviceParameter implements DeviceParameters {

	private final RedundancyStrategy redundancyStrategy;

	public RedundancyDeviceParameter(final RedundancyStrategy strategy) {
		redundancyStrategy = strategy;
	}

	/**
	 * Retrieves the redundancy strategy to use for the device
	 * 
	 * @return redundancy strategy to use
	 */
	public RedundancyStrategy getRedundancyStrategy() {
		return redundancyStrategy;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof RedundancyDeviceParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		return true;
	}
}
