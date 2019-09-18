/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.gps;

public class WGS84Constants {
	public final static double EQUATORIAL_RADIUS = 6378.137 * 1000.0;
	public final static double FLATTENING = 1.0 / 298.257223563;
	public final static double E_2 = (FLATTENING * (2.0 - FLATTENING));

	public static double getLatitutdeRadius(double lat) {
		double sinLat2;

		sinLat2 = Math.sin(lat);
		sinLat2 = sinLat2 * sinLat2;

		double r1 = EQUATORIAL_RADIUS * (1.0 - E_2) / Math.pow(1.0 - (E_2 * sinLat2), (3.0 / 2.0));
		return r1;
	}

	public static double getLongitudeRadius(double lat) {
		double sinLat2;

		sinLat2 = Math.sin(lat);
		sinLat2 = sinLat2 * sinLat2;

		double r2 = EQUATORIAL_RADIUS / Math.sqrt(1.0 - (E_2 * sinLat2)) * Math.cos(lat);
		return r2;
	}
}
