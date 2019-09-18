/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBooleanIsNull;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanIsNull;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class RealtimeBooleanIsNullMapper extends TypedRealtimeValueFragmentFactory<Boolean, RealtimeBooleanIsNull> {

	public RealtimeBooleanIsNullMapper() {
		super(RealtimeBooleanIsNull.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(RealtimeBooleanIsNull value)
			throws MappingException, RpiException {
		BooleanIsNull isNull = new BooleanIsNull();
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, isNull.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", isNull.getInValue()));
		return ret;
	}

}
