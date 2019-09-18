/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.framework.gnss.javarcc.interfaces.JGNSSInterface;

/**
 * Monitor for Global Navigation Satellite System
 */
public class JSatelliteMonitor extends JPrimitive {

	/** Longitude [rad] */
	final JOutPort<RPIdouble> outLongitude = add("outLongitude", new JOutPort<RPIdouble>());

	/** Latitude [rad] */
	final JOutPort<RPIdouble> outLatitude = add("outLatitude", new JOutPort<RPIdouble>());

	/** Altitude [m] */
	final JOutPort<RPIdouble> outAlitutde = add("outAlitutde", new JOutPort<RPIdouble>());

	/** Horizontal Dilution of Precision [m] */
	final JOutPort<RPIdouble> outHDOP = add("outHDOP", new JOutPort<RPIdouble>());

	/** Vertical Dilution of Precision [m] */
	final JOutPort<RPIdouble> outVDOP = add("outVDOP", new JOutPort<RPIdouble>());

	/** Number of satellites visible */
	final JOutPort<RPIint> outNumSatellites = add("outNumSatellites", new JOutPort<RPIint>());

	/** Device name */
	final JParameter<RPIstring> propDevice = add("Device", new JParameter<RPIstring>(new RPIstring("")));

	private JGNSSInterface gnss;

	private double latitude;

	private double longitude;

	private double altitude;

	private double hdop;

	private double vdop;

	private int satellitesVisible;

	@Override
	public Set<JDevice> getSensors() {
		return deviceSet(gnss);
	}

	@Override
	public void readSensor() {
		latitude = gnss.getLatitude();
		longitude = gnss.getLongitude();
		altitude = gnss.getAltitude();

		hdop = gnss.getHDOP();
		vdop = gnss.getVDOP();
		satellitesVisible = gnss.getSatellitesVisible();
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
		gnss = device(propDevice, JGNSSInterface.class);
	}

	@Override
	public void updateData() {
		outLatitude.set(new RPIdouble(latitude));
		outLongitude.set(new RPIdouble(longitude));
		outAlitutde.set(new RPIdouble(altitude));
		outHDOP.set(new RPIdouble(hdop));
		outVDOP.set(new RPIdouble(vdop));
		outNumSatellites.set(new RPIint(satellitesVisible));
	}

}
