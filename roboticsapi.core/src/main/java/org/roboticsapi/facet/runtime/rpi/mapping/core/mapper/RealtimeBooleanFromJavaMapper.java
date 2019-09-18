/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.WritableRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.WriteBooleanToNet;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class RealtimeBooleanFromJavaMapper extends TypedRealtimeValueFragmentFactory<Boolean, WritableRealtimeBoolean> {
	int nr = 0;

	public RealtimeBooleanFromJavaMapper() {
		super(WritableRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(WritableRealtimeBoolean value)
			throws MappingException, RpiException {

		final WriteBooleanToNet netcomm = new WriteBooleanToNet("Boolean" + nr++, value.getCheapValue());
		RealtimeValueListener<Boolean> listener = newValue -> netcomm.getNetcomm()
				.setString(new RPIbool(newValue).toString());
		try {
			value.addListener(listener);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		return new RealtimeBooleanFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated()) {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				value.removeListener(listener);
			}
		};
	}

}
