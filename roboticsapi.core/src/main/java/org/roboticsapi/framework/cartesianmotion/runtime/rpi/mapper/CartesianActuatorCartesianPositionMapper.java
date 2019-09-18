/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.framework.cartesianmotion.device.IllegalGoalException;
import org.roboticsapi.framework.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.primitives.CartesianPosition;

public class CartesianActuatorCartesianPositionMapper<DD extends NamedActuatorDriver<? extends CartesianMotionDevice>>
		implements ActuatorDriverMapper<DD, CartesianPositionActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(DD actuatorDriver, CartesianPositionActionResult actionResult,
			DeviceParameterBag parameters, MapperRegistry registry, RealtimeBoolean cancel, RealtimeDouble override,
			RealtimeDouble time) throws MappingException, RpiException {
		FrameTopology topology = World.getCommandedTopology().forRuntime(actuatorDriver.getRuntime());
		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, RealtimeBoolean.TRUE);
		RealtimePose motionCenter = parameters.get(MotionCenterParameter.class).getMotionCenter().asRealtimeValue();
		RealtimePose position = actionResult.getPosition();

		FrameProjectorParameter projector = parameters.get(FrameProjectorParameter.class);
		if (projector != null && projector.getProjector() != null) {
			try {
				position = projector.getProjector().project(position, motionCenter, parameters);
			} catch (TransformationException e) {
				throw new MappingException("Frame projector failed.", e);
			}
		}

		RealtimeTransformation transformation;
		try {
			transformation = new RealtimePoseCoincidence(position, motionCenter).getTransformation(
					actuatorDriver.getDevice().getReferenceFrame(), actuatorDriver.getDevice().getMovingFrame(),
					topology);
		} catch (TransformationException e) {
			throw new MappingException("Goal cannot be influenced by movements of the actuator.");
		}

		final CartesianPosition cartesianPosition = ret.add(new CartesianPosition(actuatorDriver.getRpiDeviceName()));
		ret.addDependency(transformation, "inPosition", cartesianPosition.getInPosition());

		ret.addException(ConcurrentAccessException.class, cartesianPosition.getOutErrorConcurrentAccess(),
				"outConcurrentAccess");
		ret.addException(ActuatorNotOperationalException.class, cartesianPosition.getOutErrorDeviceFailed(),
				"outDeviceFailed");
		ret.addException(IllegalGoalException.class, cartesianPosition.getOutErrorIllegalPosition(),
				"outIllegalPosition");

		return ret;
	}
}
