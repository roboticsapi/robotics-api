/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Interface for device specific configuration options
 */
// TODO: write documentation for this interface
public interface DeviceParameters {
	// TODO: for documentation: @param boundingObject should compatible to the
	// type of the calling object
	boolean respectsBounds(DeviceParameters boundingObject);
}
