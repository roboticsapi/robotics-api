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
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.CycleTime;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class CartesianVelocityGoalToCartesianVelocityMapper
		implements ActuatorDriverMapper<CartesianActuatorDriver, CartesianVelocityGoalActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(CartesianActuatorDriver actuatorDriver,
			CartesianVelocityGoalActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {
		FrameTopology topology = World.getCommandedTopology().forRuntime(actuatorDriver.getRuntime());
		RealtimePose motionCenter = parameters.get(MotionCenterParameter.class).getMotionCenter().asRealtimeValue();
		CartesianParameters cp = parameters.get(CartesianParameters.class);
		try {
			Frame referenceFrame = actionResult.getVelocityGoal().getReferenceFrame();
			RealtimeTwist goalvel = actionResult.getVelocityGoal().getTwistForRepresentation(referenceFrame, null, null,
					motionCenter, topology);
			RealtimeTwist curvel = motionCenter.getRealtimeVelocity().getTwistForRepresentation(referenceFrame, null,
					null, motionCenter, World.getCommandedTopology());

			final IdentityRealtimeDouble dt = new IdentityRealtimeDouble("dt", time.getRuntime());
			RealtimeVector transDiff = goalvel.getTranslationVelocity().add(curvel.getTranslationVelocity().invert());
			RealtimeVector rotDiff = goalvel.getRotationVelocity().add(curvel.getRotationVelocity().invert());

			RealtimeDouble transFac = transDiff.getLength().divide(dt.multiply(cp.getMaximumPositionAcceleration()));
			RealtimeDouble rotFac = rotDiff.getLength().divide(dt.multiply(cp.getMaximumRotationAcceleration()));
			RealtimeDouble factor = RealtimeDouble.createFromConstant(1)
					.divide(RealtimeDouble.createMax(1, transFac, rotFac));

			RealtimeTwist nextVel = curvel
					.add(RealtimeTwist.createFromLinearAngular(transDiff.scale(factor), rotDiff.scale(factor)));

			RealtimeVelocity vel = RealtimeVelocity.createFromTwist(referenceFrame, null, null, nextVel);
			CartesianVelocityActionResult result = new CartesianVelocityActionResult(null, null, vel);
			RealtimeValueConsumerFragment ret = registry.mapDriver(actuatorDriver, result, parameters, cancel, override,
					time);

			ret.addRealtimeValueFragmentFactory(new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(
					IdentityRealtimeDouble.class) {
				@Override
				protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value)
						throws MappingException, RpiException {
					if (value != dt) {
						return null;
					}
					return new RealtimeDoubleFragment(dt, new CycleTime().getOutValue());
				}
			});
			return ret;
		} catch (TransformationException e) {
			throw new MappingException(e);
		}
	}
}
