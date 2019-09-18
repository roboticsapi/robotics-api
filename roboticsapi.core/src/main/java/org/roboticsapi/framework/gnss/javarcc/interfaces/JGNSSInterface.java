/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.javarcc.interfaces;

import org.roboticsapi.facet.javarcc.devices.JDevice;

public interface JGNSSInterface extends JDevice {

	double getLongitude();

	double getLatitude();

	double getAltitude();

	double getHDOP();

	double getVDOP();

	int getSatellitesVisible();

}
