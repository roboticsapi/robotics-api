/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.primitives.CartesianMonitor;
import org.roboticsapi.framework.cartesianmotion.sensor.CartesianMeasuredRealtimeTwist;

public class CartesianMeasuredVelocityMapper
		extends TypedRealtimeValueFragmentFactory<Twist, CartesianMeasuredRealtimeTwist> {

	public CartesianMeasuredVelocityMapper() {
		super(CartesianMeasuredRealtimeTwist.class);
	}

	@Override
	protected RealtimeValueFragment<Twist> createFragment(CartesianMeasuredRealtimeTwist value)
			throws MappingException, RpiException {
		return new RealtimeTwistFragment(value, new CartesianMonitor(value.getDeviceName()).getOutMeasuredVelocity());
	}
}
