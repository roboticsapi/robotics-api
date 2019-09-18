/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.PivotAdaptedRealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class PivotAdaptedRealtimeTwistMapper extends TypedRealtimeValueAliasFactory<Twist, PivotAdaptedRealtimeTwist> {

	public PivotAdaptedRealtimeTwistMapper() {
		super(PivotAdaptedRealtimeTwist.class);
	}

	@Override
	protected RealtimeValue<Twist> createAlias(PivotAdaptedRealtimeTwist value) {
		RealtimeTwist other = value.getOther();
		return RealtimeTwist.createFromLinearAngular(
				other.getTranslationVelocity().add(other.getRotationVelocity().cross(value.getPivotChange())),
				other.getRotationVelocity());
	}

}
