/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.InvertedRealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class InvertedRealtimeTwistMapper extends TypedRealtimeValueAliasFactory<Twist, InvertedRealtimeTwist> {

	public InvertedRealtimeTwistMapper() {
		super(InvertedRealtimeTwist.class);
	}

	@Override
	protected RealtimeValue<Twist> createAlias(InvertedRealtimeTwist value) {
		return RealtimeTwist.createFromLinearAngular(value.getOther().getTranslationVelocity().invert(),
				value.getOther().getRotationVelocity().invert());
	}

}
