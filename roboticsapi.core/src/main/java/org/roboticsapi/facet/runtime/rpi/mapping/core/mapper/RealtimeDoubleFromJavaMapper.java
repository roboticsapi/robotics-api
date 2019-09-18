/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.WritableRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.WriteDoubleToNet;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class RealtimeDoubleFromJavaMapper extends TypedRealtimeValueFragmentFactory<Double, WritableRealtimeDouble> {
	int nr = 0;

	public RealtimeDoubleFromJavaMapper() {
		super(WritableRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(WritableRealtimeDouble value)
			throws MappingException, RpiException {

		final WriteDoubleToNet netcomm = new WriteDoubleToNet("Double" + nr++, value.getCheapValue());
		RealtimeValueListener<Double> listener = newValue -> netcomm.getNetcomm()
				.setString(new RPIdouble(newValue).toString());
		try {
			value.addListener(listener);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		return new RealtimeDoubleFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated()) {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				value.removeListener(listener);
			}
		};
	}
}
