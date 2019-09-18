/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.AddedRealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class AddedRealtimeTwistMapper extends TypedRealtimeValueAliasFactory<Twist, AddedRealtimeTwist> {

	public AddedRealtimeTwistMapper() {
		super(AddedRealtimeTwist.class);
	}

	@Override
	protected RealtimeValue<Twist> createAlias(AddedRealtimeTwist value) {
		RealtimeTwist first = value.getLeft();
		RealtimeTwist second = value.getRight();

		return RealtimeTwist.createFromLinearAngular(
				first.getTranslationVelocity().add(second.getTranslationVelocity()),
				first.getRotationVelocity().add(second.getRotationVelocity()));
	}
}
