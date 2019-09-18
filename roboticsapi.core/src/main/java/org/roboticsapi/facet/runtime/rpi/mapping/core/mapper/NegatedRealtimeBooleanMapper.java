/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.NegatedRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanNot;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class NegatedRealtimeBooleanMapper extends TypedRealtimeValueFragmentFactory<Boolean, NegatedRealtimeBoolean> {

	public NegatedRealtimeBooleanMapper() {
		super(NegatedRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(NegatedRealtimeBoolean value)
			throws MappingException, RpiException {
		BooleanNot not = new BooleanNot();
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, not.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", not.getInValue()));
		return ret;
	}

}
