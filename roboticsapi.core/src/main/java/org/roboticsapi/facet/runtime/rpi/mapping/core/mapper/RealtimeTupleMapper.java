/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.RealtimeTuple;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.CommandFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeTupleFragment;

public class RealtimeTupleMapper extends TypedRealtimeValueFragmentFactory<Object[], RealtimeTuple> {
	private CommandFragment cmd;

	public RealtimeTupleMapper(CommandFragment cmd) {
		super(RealtimeTuple.class);
		this.cmd = cmd;
	}

	@Override
	protected RealtimeValueFragment<Object[]> createFragment(RealtimeTuple value)
			throws MappingException, RpiException {
		RealtimeValueFragment<?>[] frags = new RealtimeValueFragment<?>[value.getValues().length];
		for (int i = 0; i < value.getValues().length; i++) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			RealtimeValue<?> effectiveSensor = value.getValues()[i].createObserver(new RealtimeValueListener() {
				@Override
				public void onValueChanged(Object newValue) {
				}
			}, RealtimeBoolean.TRUE, false).getSensor();
			frags[i] = cmd.getOrCreateRealtimeValueFragment(effectiveSensor);
		}
		return new RealtimeTupleFragment(value, frags);
	}

}
