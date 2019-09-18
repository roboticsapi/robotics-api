/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.ConditionalRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanConditional;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class ConditionalRealtimeBooleanMapper
		extends TypedRealtimeValueFragmentFactory<Boolean, ConditionalRealtimeBoolean> {

	public ConditionalRealtimeBooleanMapper() {
		super(ConditionalRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(ConditionalRealtimeBoolean value)
			throws MappingException, RpiException {
		BooleanConditional cond = new BooleanConditional();
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, cond.getOutValue());
		ret.addDependency(value.getCondition(), ret.addInPort("inCondition", cond.getInCondition()));
		ret.addDependency(value.getIfTrue(), ret.addInPort("inTrue", cond.getInTrue()));
		ret.addDependency(value.getIfFalse(), ret.addInPort("inFalse", cond.getInFalse()));
		return ret;

	}

}
