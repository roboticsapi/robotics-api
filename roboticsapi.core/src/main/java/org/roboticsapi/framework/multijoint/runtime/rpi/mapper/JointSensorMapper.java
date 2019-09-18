/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.multijoint.runtime.rpi.JointRealtimeDouble;
import org.roboticsapi.framework.multijoint.runtime.rpi.primitives.JointMonitor;

public class JointSensorMapper extends TypedRealtimeValueFragmentFactory<Double, JointRealtimeDouble> {

	public JointSensorMapper() {
		super(JointRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(JointRealtimeDouble value)
			throws MappingException, RpiException {
		JointMonitor monitor = new JointMonitor(value.getDriver().getRpiDeviceName(), value.getDriver().getIndex());
		OutPort out = null;
		for (OutPort p : monitor.getOutPorts()) {
			if (p.getName().equals(value.getPortName())) {
				out = p;
			}
		}
		return new RealtimeDoubleFragment(value, out);
	}
}
