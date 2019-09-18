/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.AndRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanAnd;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class AndRealtimeBooleanMapper extends TypedRealtimeValueFragmentFactory<Boolean, AndRealtimeBoolean> {

	public AndRealtimeBooleanMapper() {
		super(AndRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(AndRealtimeBoolean value) throws MappingException {
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value);
		OutPort valuePort = null;
		for (int i = 0; i < value.getInnerValues().size(); i++) {
			FragmentInPort sensorPort = ret.addInPort("inValue" + i);
			ret.addDependency(value.getInnerValues().get(i), sensorPort);
			if (valuePort != null) {
				BooleanAnd and = ret.add(new BooleanAnd());
				ret.connect(valuePort, and.getInFirst());
				ret.connect(sensorPort.getInternalOutPort(), and.getInSecond());
				valuePort = and.getOutValue();
			} else {
				valuePort = sensorPort.getInternalOutPort();
			}
		}
		ret.defineResult(valuePort);
		return ret;
	}
}
