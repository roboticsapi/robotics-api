/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.runtime.rpi;

import org.roboticsapi.core.activity.runtime.SensorInterfaceImpl;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;
import org.roboticsapi.framework.gnss.GlobalNavigationSatelliteSystemInterface;

public class GlobalNavigationSatelliteSystemInterfaceImpl extends SensorInterfaceImpl
		implements GlobalNavigationSatelliteSystemInterface {

	private GNSSGenericDriver driver;

	public GlobalNavigationSatelliteSystemInterfaceImpl(GNSSGenericDriver driver) {
		super(driver.getDevice(), driver.getRuntime());
		this.driver = driver;
	}

	@Override
	public RealtimeDouble getLongitude() {
		return driver.getLongitude();
	}

	@Override
	public RealtimeDouble getLatitude() {
		return driver.getLatitude();
	}

	@Override
	public RealtimeDouble getAltitude() {
		return driver.getAltitude();
	}

	@Override
	public RealtimeDouble getHDOP() {
		return driver.getHDOP();
	}

	@Override
	public RealtimeDouble getVDOP() {
		return driver.getVDOP();
	}

	@Override
	public RealtimeInteger getSatellitesVisible() {
		return driver.getSatellitesVisible();
	}

}
