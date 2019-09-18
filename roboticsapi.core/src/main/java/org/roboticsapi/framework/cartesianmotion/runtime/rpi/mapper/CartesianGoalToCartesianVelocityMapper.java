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
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleMultiply;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueAliasFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.IdentityRealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameOTG;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class CartesianGoalToCartesianVelocityMapper
		implements ActuatorDriverMapper<CartesianActuatorDriver, CartesianGoalActionResult> {

	private final double maxTransError;
	private final double maxRotError;
	private final double maxGoalTransVel;
	private final double maxGoalRotVel;

	public CartesianGoalToCartesianVelocityMapper(double maxTransError, double maxRotError, double maxGoalTransVel,
			double maxGoalRotVel) {
		this.maxTransError = maxTransError;
		this.maxRotError = maxRotError;
		this.maxGoalTransVel = maxGoalTransVel;
		this.maxGoalRotVel = maxGoalRotVel;
	}

	@Override
	public RealtimeValueConsumerFragment map(CartesianActuatorDriver actuatorDriver,
			CartesianGoalActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		FrameTopology topology = World.getCommandedTopology().forRuntime(actuatorDriver.getRuntime());
		Frame referenceFrame = actuatorDriver.getDevice().getReferenceFrame();
		Frame movingFrame = actuatorDriver.getDevice().getMovingFrame();

		RealtimePose motionCenter = parameters.get(MotionCenterParameter.class).getMotionCenter().asRealtimeValue();
		RealtimePose goal = actionResult.getGoal();
		FrameProjectorParameter frameProjector = parameters.get(FrameProjectorParameter.class);
		if (frameProjector != null && frameProjector.getProjector() != null) {
			try {
				goal = frameProjector.getProjector().project(goal, motionCenter, parameters);
			} catch (TransformationException e) {
				throw new MappingException("Failed to apply frame projector.", e);
			}
		}

		// find current and goal information
		final RealtimeTransformation goalTrans;
		try {
			goalTrans = new RealtimePoseCoincidence(goal, motionCenter).getTransformation(referenceFrame, movingFrame,
					topology);
		} catch (TransformationException e) {
			throw new MappingException(e);
		}
		final RealtimeTwist goalTwist = goalTrans.derive();

		final RealtimeTransformation curTrans = referenceFrame.getRealtimeTransformationTo(movingFrame, topology);

		final RealtimeTwist curTwist = referenceFrame.getRealtimeTwistOf(movingFrame, topology);

		// cancel scales override to 0
		final CartesianParameters cp = parameters.get(CartesianParameters.class);
		double cancelDuration = Math.max(cp.getMaximumPositionVelocity() / cp.getMaximumPositionAcceleration(),
				cp.getMaximumRotationVelocity() / cp.getMaximumRotationAcceleration());
		RealtimeDouble overrideScale = RealtimeDouble
				.createConditional(cancel, RealtimeDouble.createFromConstant(0), RealtimeDouble.createFromConstant(1))
				.slidingAverage(cancelDuration);

		RealtimeBoolean isStopped = overrideScale.less(0.01);
		final RealtimeDouble scaledOverride = override.multiply(overrideScale);

		final IdentityRealtimeTwist vel = new IdentityRealtimeTwist("<<nextVelocity>>(" + goalTrans + ")");

		ActionResult result = new CartesianVelocityActionResult(null, null,
				RealtimeVelocity.createFromTwist(referenceFrame, null, referenceFrame.asOrientation(), vel));

		RealtimeValueConsumerFragment inner = registry.mapDriver(actuatorDriver, result, parameters, cancel, override,
				time);

		RealtimeTransformation curDif = curTrans.invert().multiply(goalTrans);

		RealtimeBoolean arrived = curDif.getTranslation().getLength().less(maxTransError)
				.and(curDif.getRotation().getAngle().less(maxRotError))
				.and(curTwist.getRotationVelocity().getLength().less(maxGoalTransVel))
				.and(curTwist.getRotationVelocity().getLength().less(maxGoalRotVel));

		RealtimeBoolean completed = arrived.or(isStopped);

		RealtimeValueConsumerFragment ret = new ActuatorFragment(actuatorDriver, completed);
		ret.addWithDependencies(inner);

		ret.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Twist, IdentityRealtimeTwist>(IdentityRealtimeTwist.class) {
					@Override
					protected RealtimeValueFragment<Twist> createFragment(IdentityRealtimeTwist value)
							throws MappingException, RpiException {
						if (value != vel) {
							return null;
						}

						RealtimeTwistFragment fragment = new RealtimeTwistFragment(vel);

						// OTG
						FrameOTG otg = fragment.add(new FrameOTG());
						otg.getInCurPos().setDebug(2);
						otg.getInCurVel().setDebug(2);
						otg.getInDestPos().setDebug(2);

						FragmentInPort overridePort = fragment.addInPort("inOverride");
						fragment.addDependency(scaledOverride, overridePort);

						DoubleMultiply posVel = fragment.add(new DoubleMultiply(cp.getMaximumPositionVelocity(), 0.0));
						fragment.connect(overridePort.getInternalOutPort(), posVel.getInSecond());
						fragment.connect(posVel.getOutValue(), otg.getInMaxTransVel());

						DoubleMultiply posAcc = fragment
								.add(new DoubleMultiply(cp.getMaximumPositionAcceleration(), 0.0));
						fragment.connect(overridePort.getInternalOutPort(), posAcc.getInSecond());
						fragment.connect(posAcc.getOutValue(), otg.getInMaxTransAcc());

						DoubleMultiply rotVel = fragment.add(new DoubleMultiply(cp.getMaximumRotationVelocity(), 0.0));
						fragment.connect(overridePort.getInternalOutPort(), rotVel.getInSecond());
						fragment.connect(rotVel.getOutValue(), otg.getInMaxRotVel());

						DoubleMultiply rotAcc = fragment
								.add(new DoubleMultiply(cp.getMaximumRotationAcceleration(), 0.0));
						fragment.connect(overridePort.getInternalOutPort(), rotAcc.getInSecond());
						fragment.connect(rotAcc.getOutValue(), otg.getInMaxRotAcc());

						fragment.addDependency(curTrans, "inCurPos", otg.getInCurPos());
						fragment.addDependency(curTwist, "inCurVel", otg.getInCurVel());
						fragment.addDependency(goalTrans, "inDestPos", otg.getInDestPos());
						fragment.addDependency(goalTwist, "inDestVel", otg.getInDestVel());
						fragment.defineResult(otg.getOutVel());
						return fragment;
					}
				});

		for (RealtimeValueAliasFactory af : inner.getRealtimeValueAliasFactories()) {
			ret.addRealtimeValueAliasFactory(af);
		}
		for (RealtimeValueFragmentFactory ff : inner.getRealtimeValueFragmentFactories()) {
			ret.addRealtimeValueFragmentFactory(ff);
		}

		return ret;

	}
}
