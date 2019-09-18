/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class CartesianVelocityToCartesianPositionMapper
		implements ActuatorDriverMapper<CartesianActuatorDriver, CartesianVelocityActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(CartesianActuatorDriver actuatorDriver,
			CartesianVelocityActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {
		FrameTopology topology = World.getCommandedTopology().forRuntime(actuatorDriver.getRuntime());
		RealtimePose frame = parameters.get(MotionCenterParameter.class).getMotionCenter().asRealtimeValue();
		try {
			Frame reference = actionResult.getVelocity().getReferenceFrame();
			RealtimeTwist twist = actionResult.getVelocity().getTwistForRepresentation(null, null, frame, topology);
			RealtimeTransformation start = reference.asRealtimePose().getRealtimeTransformationTo(frame, topology);

			RealtimePose goal = twist.integrate(start).asPose(reference);
			CartesianPositionActionResult result = new CartesianPositionActionResult(null, null, goal,
					actionResult.getVelocity());

			return registry.mapDriver(actuatorDriver, result, parameters, cancel, override, time);

		} catch (TransformationException e) {
			throw new MappingException(e);
		}
	}
}
