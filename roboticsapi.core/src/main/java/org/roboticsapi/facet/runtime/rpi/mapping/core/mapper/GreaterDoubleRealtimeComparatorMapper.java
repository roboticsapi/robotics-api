/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.GreaterDoubleRealtimeComparator;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleGreater;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class GreaterDoubleRealtimeComparatorMapper
		extends TypedRealtimeValueFragmentFactory<Boolean, GreaterDoubleRealtimeComparator> {
	public GreaterDoubleRealtimeComparatorMapper() {
		super(GreaterDoubleRealtimeComparator.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(GreaterDoubleRealtimeComparator value)
			throws MappingException, RpiException {
		DoubleGreater greater = new DoubleGreater();
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, greater.getOutValue());
		ret.addDependency(value.getLeft(), ret.addInPort("inFirst", greater.getInFirst()));
		ret.addDependency(value.getRight(), ret.addInPort("inSecond", greater.getInSecond()));
		return ret;
	}

}
