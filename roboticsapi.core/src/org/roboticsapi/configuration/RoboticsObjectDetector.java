/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.configuration;

/**
 * A ConfigurationDetector looks for Configurations in its 'scope' (e.g., a
 * runtime). It should register newly found configurations at the
 * RoboticsFactory.
 */
public interface RoboticsObjectDetector {

	/**
	 * Tells this ConfigurationDetector to start looking for Configurations.
	 */
	public void startSearching(RoboticsObjectDetectorCallback callback);

}