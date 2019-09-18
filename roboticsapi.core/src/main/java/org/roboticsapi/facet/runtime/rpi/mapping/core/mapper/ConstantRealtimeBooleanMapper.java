/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.ConstantRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanValue;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class ConstantRealtimeBooleanMapper extends TypedRealtimeValueFragmentFactory<Boolean, ConstantRealtimeBoolean> {

	public ConstantRealtimeBooleanMapper() {
		super(ConstantRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(ConstantRealtimeBoolean value) throws MappingException {
		return new RealtimeBooleanFragment(value, new BooleanValue(value.getValue()).getOutValue());
	}

}
