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
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleGreater;
import org.roboticsapi.facet.runtime.rpi.core.primitives.EdgeDetection;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Interval;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Rampify;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.IdentityRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameConditional;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameFromPosRot;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameLerp;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameSnapshot;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameTransform;
import org.roboticsapi.framework.cartesianmotion.action.CartesianErrorCorrection;

public class CartesianErrorCorrectionMapper implements ActionMapper<CartesianErrorCorrection> {

	@Override
	public ActionResult map(final CartesianErrorCorrection action, DeviceParameterBag parameters,
			MapperRegistry registry, RealtimeBoolean cancel, RealtimeDouble override, final RealtimeDouble time,
			Map<PlannedAction<?>, Plan> plans) throws MappingException, RpiException {

		ActionResult innerResult = registry.mapAction(action.getWrappedMotion(), parameters, cancel, override, time,
				plans);

		if (innerResult instanceof CartesianPositionActionResult) {
			CartesianPositionActionResult inner = (CartesianPositionActionResult) innerResult;
			Pose startPose = action.getStartPose();
			Pose motionCenter = action.getMotionCenter();
			try {
				Frame ref = startPose.getReference();
				final RealtimeTransformation realPosition = ref.asRealtimePose()
						.getRealtimeTransformationTo(motionCenter.asRealtimeValue(), action.getTopology());
				final Transformation expectedPosition = startPose
						.getTransformationForRepresentation(ref.asOrientation());
				final RealtimeTransformation innerPosition = inner.getPosition().getTransformationForRepresentation(ref,
						action.getTopology());

				final double duration = plans.get(action.getWrappedMotion()) == null ? 1
						: plans.get(action.getWrappedMotion()).getTotalTime() * 0.45;

				final IdentityRealtimeTransformation trans = new IdentityRealtimeTransformation(
						"<<resynchronize>>(" + innerPosition + ")");
				CartesianPositionActionResult ret = new CartesianPositionActionResult(action, RealtimeBoolean.TRUE,
						innerResult, RealtimePose.createFromTransformation(ref, trans));

				ret.addRealtimeValueSource(innerResult);
				ret.addRealtimeValueFragmentFactory(
						new TypedRealtimeValueFragmentFactory<Transformation, IdentityRealtimeTransformation>(
								IdentityRealtimeTransformation.class) {
							@Override
							protected RealtimeValueFragment<Transformation> createFragment(
									IdentityRealtimeTransformation value) throws MappingException, RpiException {
								if (value != trans) {
									return null;
								}

								Transformation start = expectedPosition;
								RealtimeTransformation realPos = realPosition;

								double resyncTime = duration;
								double starttime = 0;

								RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value);
								FragmentInPort inTime = ret.addInPort("inTime");
								ret.addDependency(time, inTime);

								FragmentInPort inPos = ret.addInPort("inPos");
								ret.addDependency(innerPosition, inPos);

								// snapshot time
								DoubleGreater t = ret.add(new DoubleGreater(0.0, starttime));
								ret.connect(inTime.getInternalOutPort(), t.getInFirst());

								EdgeDetection snapshot = ret.add(new EdgeDetection(true));
								ret.connect(t.getOutValue(), snapshot.getInValue());

								// resynchronize
								Interval interval = ret.add(new Interval(starttime, starttime + resyncTime));
								ret.connect(inTime.getInternalOutPort(), interval.getInValue());

								Rampify ramp = ret.add(new Rampify(0.0));
								ret.connect(interval.getOutValue(), ramp.getInValue());

								// calculate position for each axis
								FrameSnapshot ssp = ret.add(new FrameSnapshot());
								ret.connect(snapshot.getOutValue(), ssp.getInSnapshot());
								ret.addDependency(realPos, "inRealPos", ssp.getInValue());

								Transformation minusStart = start.invert();
								FrameFromPosRot expected = ret.add(new FrameFromPosRot(
										minusStart.getTranslation().getX(), minusStart.getTranslation().getY(),
										minusStart.getTranslation().getZ(), minusStart.getRotation().getA(),
										minusStart.getRotation().getB(), minusStart.getRotation().getC()));

								FrameTransform diff = ret.add(new FrameTransform());
								ret.connect(ssp.getOutValue(), diff.getInFirst());
								ret.connect(expected.getOutValue(), diff.getInSecond());

								FrameFromPosRot zero = ret.add(new FrameFromPosRot(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

								FrameLerp delta = ret.add(new FrameLerp());
								ret.connect(diff.getOutValue(), delta.getInFrom());
								ret.connect(zero.getOutValue(), delta.getInTo());
								ret.connect(ramp.getOutValue(), delta.getInAmount());

								FrameTransform add = ret.add(new FrameTransform());
								ret.connect(delta.getOutValue(), add.getInFirst());

								FrameConditional result = ret.add(new FrameConditional());
								ret.connect(t.getOutValue(), result.getInCondition());
								ret.connect(add.getOutValue(), result.getInTrue());

								ret.connect(inPos.getInternalOutPort(), add.getInSecond());
								ret.connect(inPos.getInternalOutPort(), result.getInFalse());

								ret.defineResult(result.getOutValue());

								return ret;

							}
						});

				return ret;

			} catch (TransformationException e) {
				throw new MappingException(e);
			}

		} else {
			throw new MappingException("Mapping of inner action did not return CartesianPositionActionResult.");
		}
	}
}
