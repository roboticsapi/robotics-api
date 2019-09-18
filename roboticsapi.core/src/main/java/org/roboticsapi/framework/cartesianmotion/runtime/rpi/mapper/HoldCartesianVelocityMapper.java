/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import java.util.Map;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.framework.cartesianmotion.action.HoldCartesianVelocity;

public class HoldCartesianVelocityMapper implements ActionMapper<HoldCartesianVelocity> {

	@Override
	public ActionResult map(HoldCartesianVelocity action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {
		RealtimeDouble zeroIfCanceled = RealtimeDouble.createConditional(cancel, RealtimeDouble.ZERO,
				RealtimeDouble.ONE);
		RealtimeTwist twist = action.getVelocity().getTwist();
		RealtimeTwist scaledTwist = RealtimeTwist.createFromLinearAngular(
				twist.getTranslationVelocity().scale(override.multiply(zeroIfCanceled)),
				twist.getRotationVelocity().scale(override.multiply(zeroIfCanceled)));

		CartesianVelocityActionResult ret = new CartesianVelocityActionResult(action, cancel,
				RealtimeVelocity.createFromTwist(action.getVelocity().getReferenceFrame(),
						action.getVelocity().getPivotPoint(), action.getVelocity().getOrientation(), scaledTwist));

		return ret;
	}
}
