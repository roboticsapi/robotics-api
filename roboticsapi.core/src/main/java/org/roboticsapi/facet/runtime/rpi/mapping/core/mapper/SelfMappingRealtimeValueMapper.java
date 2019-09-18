/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.SelfMappingRealtimeValue;

public class SelfMappingRealtimeValueMapper implements RealtimeValueFragmentFactory {

	@Override
	public RealtimeValueFragment<?> createRealtimeValueFragment(RealtimeValue<?> value) throws MappingException {
		if (value instanceof SelfMappingRealtimeValue<?>) {
			return ((SelfMappingRealtimeValue<?>) value).createRealtimeValueFragment();
		}
		return null;
	}

}
