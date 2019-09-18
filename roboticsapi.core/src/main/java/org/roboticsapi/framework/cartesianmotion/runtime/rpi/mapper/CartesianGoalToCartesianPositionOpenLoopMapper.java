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
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.IdentityRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.mapping.world.IdentityRealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameConditional;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameIsNull;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameOTG;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FramePre;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistConditional;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistPre;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class CartesianGoalToCartesianPositionOpenLoopMapper
		implements ActuatorDriverMapper<CartesianActuatorDriver, CartesianGoalActionResult> {

	private final double maxTransError;
	private final double maxRotError;
	private final double maxGoalTransVel;
	private final double maxGoalRotVel;

	public CartesianGoalToCartesianPositionOpenLoopMapper(double maxTransError, double maxRotError,
			double maxGoalTransVel, double maxGoalRotVel) {
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
			throw new MappingException("Robot cannot influence given Pose.", e);
		}

		final RealtimeTwist goalTwist = goalTrans.derive();

		final RealtimeTransformation curTrans = referenceFrame.getRealtimeTransformationTo(movingFrame, topology);

		final RealtimeTwist curTwist = referenceFrame.getCommandedRealtimeTwistOf(movingFrame);

		// cancel scales override to 0
		final CartesianParameters cp = parameters.get(CartesianParameters.class);
		double cancelDuration = Math.max(cp.getMaximumPositionVelocity() / cp.getMaximumPositionAcceleration(),
				cp.getMaximumRotationVelocity() / cp.getMaximumRotationAcceleration());
		RealtimeDouble overrideScale = RealtimeDouble
				.createConditional(cancel, RealtimeDouble.createFromConstant(0), RealtimeDouble.createFromConstant(1))
				.slidingAverage(cancelDuration);

		RealtimeBoolean isStopped = overrideScale.less(0.01);
		final RealtimeDouble scaledOverride = override.multiply(overrideScale);

		final IdentityRealtimeTransformation nextPos = new IdentityRealtimeTransformation(
				"<<nextPosition>>(" + goalTrans + ")");

		final IdentityRealtimeTwist nextVel = new IdentityRealtimeTwist("<<nextVelocity>>(" + goalTrans + ")");

		ActionResult result = new CartesianPositionActionResult(null, null,
				RealtimePose.createFromTransformation(referenceFrame, nextPos),
				RealtimeVelocity.createFromTwist(referenceFrame, null, referenceFrame.asOrientation(), nextVel));

		RealtimeValueConsumerFragment inner = registry.mapDriver(actuatorDriver, result, parameters, cancel, override,
				time);

		RealtimeTransformation curDif = curTrans.invert().multiply(goalTrans);

		RealtimeBoolean arrived = curDif.getTranslation().getLength().less(maxTransError)
				.and(curDif.getRotation().getAngle().less(maxRotError))
				.and(curTwist.getRotationVelocity().getLength().less(maxGoalTransVel))
				.and(curTwist.getRotationVelocity().getLength().less(maxGoalRotVel));

		RealtimeBoolean completed = arrived.or(isStopped);

		RealtimeValueConsumerFragment ret = new ActuatorFragment(actuatorDriver, completed);
		ret.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Transformation, IdentityRealtimeTransformation>(
						IdentityRealtimeTransformation.class) {
					@Override
					protected RealtimeValueFragment<Transformation> createFragment(IdentityRealtimeTransformation value)
							throws MappingException, RpiException {
						if (value != nextPos) {
							return null;
						}

						RealtimeTransformationFragment fragment = new RealtimeTransformationFragment(nextPos);

						FrameOTG otg = buildOTG(goalTrans, goalTwist, curTrans, curTwist, cp, scaledOverride, fragment);

						fragment.defineResult(otg.getOutPos());
						return fragment;
					}

				});

		ret.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Twist, IdentityRealtimeTwist>(IdentityRealtimeTwist.class) {
					@Override
					protected RealtimeValueFragment<Twist> createFragment(IdentityRealtimeTwist value)
							throws MappingException, RpiException {
						if (value != nextVel) {
							return null;
						}

						RealtimeTwistFragment fragment = new RealtimeTwistFragment(nextVel);

						FrameOTG otg = buildOTG(goalTrans, goalTwist, curTrans, curTwist, cp, scaledOverride, fragment);

						fragment.defineResult(otg.getOutVel());
						return fragment;
					}
				});

		ret.addWithDependencies(inner);

		return ret;
	}

	private FrameOTG buildOTG(final RealtimeTransformation goalTrans, RealtimeTwist goalTwist,
			final RealtimeTransformation curTrans, final RealtimeTwist curTwist, final CartesianParameters cp,
			final RealtimeDouble override, RealtimeValueFragment<?> fragment) throws MappingException {
		FrameOTG otg = fragment.add(new FrameOTG());
		otg.getInCurPos().setDebug(2);
		otg.getInCurVel().setDebug(2);
		otg.getInDestPos().setDebug(2);

		fragment.addDependency(override.multiply(cp.getMaximumPositionVelocity()), "inMaxTransVel",
				otg.getInMaxTransVel());
		fragment.addDependency(override.multiply(cp.getMaximumPositionAcceleration()), "inMaxTransAcc",
				otg.getInMaxTransAcc());
		fragment.addDependency(override.multiply(cp.getMaximumRotationVelocity()), "inMaxRotVel", otg.getInMaxRotVel());
		fragment.addDependency(override.multiply(cp.getMaximumRotationAcceleration()), "inMaxRotAcc",
				otg.getInMaxRotAcc());

		FramePre lastPos = fragment.add(new FramePre());
		fragment.connect(otg.getOutPos(), lastPos.getInValue());
		FrameIsNull lastNull = fragment.add(new FrameIsNull());
		fragment.connect(lastPos.getOutValue(), lastNull.getInValue());

		FrameConditional curPos = fragment.add(new FrameConditional());
		fragment.connect(lastNull.getOutValue(), curPos.getInCondition());
		fragment.connect(lastPos.getOutValue(), curPos.getInFalse());
		fragment.addDependency(curTrans, "inCurPos", curPos.getInTrue());
		fragment.connect(curPos.getOutValue(), otg.getInCurPos());

		TwistPre lastVel = fragment.add(new TwistPre());
		fragment.connect(otg.getOutVel(), lastVel.getInValue());
		TwistConditional curVel = fragment.add(new TwistConditional());
		fragment.connect(lastNull.getOutValue(), curVel.getInCondition());
		fragment.connect(lastVel.getOutValue(), curVel.getInFalse());
		fragment.addDependency(curTwist, "inCurVel", curVel.getInTrue());
		fragment.connect(curVel.getOutValue(), otg.getInCurVel());

		fragment.addDependency(goalTrans, "inDestPos", otg.getInDestPos());
		fragment.addDependency(goalTwist, "inDestVel", otg.getInDestVel());
		return otg;
	}
}
