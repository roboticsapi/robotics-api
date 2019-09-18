/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public interface GlobalNavigationSatelliteSystemInterface extends SensorInterface {
	/**
	 * Longitude [rad]
	 */
	RealtimeDouble getLongitude();

	/**
	 * Latitude [rad]
	 */
	RealtimeDouble getLatitude();

	/**
	 * Altitude [m]
	 */
	RealtimeDouble getAltitude();

	/**
	 * Horizontal dilution of position [m]
	 */
	RealtimeDouble getHDOP();

	/**
	 * Vertical dilution of position [m]
	 */
	RealtimeDouble getVDOP();

	/**
	 * Number of satellites visible
	 */
	RealtimeInteger getSatellitesVisible();

}
