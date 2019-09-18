/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.FlipFlopRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Trigger;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class FlipFlopRealtimeBooleanMapper extends TypedRealtimeValueFragmentFactory<Boolean, FlipFlopRealtimeBoolean> {

	public FlipFlopRealtimeBooleanMapper() {
		super(FlipFlopRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(FlipFlopRealtimeBoolean value)
			throws MappingException, RpiException {
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value);
		final Trigger trig = ret.add(new Trigger());
		ret.addDependency(value.getActivatingValue(), "inOn", trig.getInOn());
		ret.addDependency(value.getDeactivatingValue(), "inOff", trig.getInOff());
		if (!value.isOneShot())
			ret.addDependency(value.getActivatingValue(), "inReset", trig.getInReset());
		ret.defineResult(trig.getOutActive());
		return ret;
	}

}
