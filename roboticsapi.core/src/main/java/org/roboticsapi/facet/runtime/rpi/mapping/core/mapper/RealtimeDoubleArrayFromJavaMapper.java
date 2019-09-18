/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.WritableRealtimeDoubleArray;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.WriteDoubleArrayToNet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleArrayFragment;

public class RealtimeDoubleArrayFromJavaMapper
		extends TypedRealtimeValueFragmentFactory<Double[], WritableRealtimeDoubleArray> {
	int nr = 0;

	public RealtimeDoubleArrayFromJavaMapper() {
		super(WritableRealtimeDoubleArray.class);
	}

	@Override
	protected RealtimeValueFragment<Double[]> createFragment(WritableRealtimeDoubleArray value)
			throws MappingException, RpiException {

		final WriteDoubleArrayToNet netcomm = new WriteDoubleArrayToNet("DoubleA" + nr++, value.getCheapValue());
		RealtimeValueListener<Double[]> listener = newValue -> netcomm.getNetcomm()
				.setString(WriteDoubleArrayToNet.toString(newValue));
		try {
			value.addListener(listener);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		return new RealtimeDoubleArrayFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated()) {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				value.removeListener(listener);
			}
		};
	}

}
