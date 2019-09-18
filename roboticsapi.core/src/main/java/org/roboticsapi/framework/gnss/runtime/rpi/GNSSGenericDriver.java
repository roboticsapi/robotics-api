/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.runtime.rpi;

import java.util.Map;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.core.realtimevalue.realtimedouble.DriverBasedRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimeinteger.DriverBasedRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractDeviceDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.SelfMappingRealtimeValue;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeIntegerFragment;
import org.roboticsapi.framework.gnss.GlobalNavigationSatelliteSystemInterface;
import org.roboticsapi.framework.gnss.runtime.rpi.primitives.SatelliteMonitor;

public class GNSSGenericDriver extends AbstractDeviceDriver<Device> {

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		return interfaces.containsKey("gnss");
	}

	public RealtimeDouble getLongitude() {
		return new LongitudeRealtimeDouble(this);
	}

	public RealtimeDouble getLatitude() {
		return new LatitudeRealtimeDouble(this);
	}

	public RealtimeDouble getAltitude() {
		return new AltitudeRealtimeDouble(this);
	}

	public RealtimeDouble getHDOP() {
		return new HDOPRealtimeDouble(this);
	}

	public RealtimeDouble getVDOP() {
		return new VDOPRealtimeDouble(this);
	}

	public RealtimeInteger getSatellitesVisible() {
		return new VisibleSatellitesRealtimeIntegert(this);
	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		collector.add(GlobalNavigationSatelliteSystemInterface.class,
				() -> new GlobalNavigationSatelliteSystemInterfaceImpl(this));
	}

	class LatitudeRealtimeDouble extends DriverBasedRealtimeDouble<GNSSGenericDriver>
			implements SelfMappingRealtimeValue<Double> {
		public LatitudeRealtimeDouble(GNSSGenericDriver driver) {
			super(driver);
		}

		@Override
		public RealtimeValueFragment<Double> createRealtimeValueFragment() throws MappingException {
			return new RealtimeDoubleFragment(this, new SatelliteMonitor(getRpiDeviceName()).getOutLatitude());
		}
	}

	class LongitudeRealtimeDouble extends DriverBasedRealtimeDouble<GNSSGenericDriver>
			implements SelfMappingRealtimeValue<Double> {
		public LongitudeRealtimeDouble(GNSSGenericDriver driver) {
			super(driver);
		}

		@Override
		public RealtimeValueFragment<Double> createRealtimeValueFragment() throws MappingException {
			return new RealtimeDoubleFragment(this, new SatelliteMonitor(getRpiDeviceName()).getOutLongitude());
		}
	}

	class AltitudeRealtimeDouble extends DriverBasedRealtimeDouble<GNSSGenericDriver>
			implements SelfMappingRealtimeValue<Double> {
		public AltitudeRealtimeDouble(GNSSGenericDriver driver) {
			super(driver);
		}

		@Override
		public RealtimeValueFragment<Double> createRealtimeValueFragment() throws MappingException {
			return new RealtimeDoubleFragment(this, new SatelliteMonitor(getRpiDeviceName()).getOutAlitutde());
		}
	}

	class HDOPRealtimeDouble extends DriverBasedRealtimeDouble<GNSSGenericDriver>
			implements SelfMappingRealtimeValue<Double> {
		public HDOPRealtimeDouble(GNSSGenericDriver driver) {
			super(driver);
		}

		@Override
		public RealtimeValueFragment<Double> createRealtimeValueFragment() throws MappingException {
			return new RealtimeDoubleFragment(this, new SatelliteMonitor(getRpiDeviceName()).getOutHDOP());
		}
	}

	class VDOPRealtimeDouble extends DriverBasedRealtimeDouble<GNSSGenericDriver>
			implements SelfMappingRealtimeValue<Double> {
		public VDOPRealtimeDouble(GNSSGenericDriver driver) {
			super(driver);
		}

		@Override
		public RealtimeValueFragment<Double> createRealtimeValueFragment() throws MappingException {
			return new RealtimeDoubleFragment(this, new SatelliteMonitor(getRpiDeviceName()).getOutVDOP());
		}
	}

	class VisibleSatellitesRealtimeIntegert extends DriverBasedRealtimeInteger<GNSSGenericDriver>
			implements SelfMappingRealtimeValue<Integer> {
		public VisibleSatellitesRealtimeIntegert(GNSSGenericDriver driver) {
			super(driver);
		}

		@Override
		public RealtimeValueFragment<Integer> createRealtimeValueFragment() throws MappingException {
			return new RealtimeIntegerFragment(this, new SatelliteMonitor(getRpiDeviceName()).getOutNumSatellites());
		}
	}

}
