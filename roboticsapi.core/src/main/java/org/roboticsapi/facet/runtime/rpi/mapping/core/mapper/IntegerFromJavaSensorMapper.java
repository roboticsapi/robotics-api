/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeinteger.WritableRealtimeInteger;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.WriteIntToNet;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeIntegerFragment;

public class IntegerFromJavaSensorMapper extends TypedRealtimeValueFragmentFactory<Integer, WritableRealtimeInteger> {
	int nr = 0;

	public IntegerFromJavaSensorMapper() {
		super(WritableRealtimeInteger.class);
	}

	@Override
	protected RealtimeValueFragment<Integer> createFragment(WritableRealtimeInteger value)
			throws MappingException, RpiException {

		final WriteIntToNet netcomm = new WriteIntToNet("Int" + nr++, value.getCheapValue());
		RealtimeValueListener<Integer> listener = newValue -> netcomm.getNetcomm()
				.setString(new RPIint(newValue).toString());
		try {
			value.addListener(listener);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		return new RealtimeIntegerFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated()) {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				value.removeListener(listener);
			}
		};
	}

}
