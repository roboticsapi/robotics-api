/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper;

import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.actuator.OverrideParameter.Scaling;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Clock;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.cartesianmotion.action.ExecutableCartesianMotionPlan;
import org.roboticsapi.framework.cartesianmotion.action.PathMotion;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;

public class ExecutablePathMotionMapper<A extends PathMotion<? extends ExecutableCartesianMotionPlan>>
		implements ActionMapper<A> {

	@Override
	public ActionResult map(final A action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, final RealtimeDouble time,
			Map<PlannedAction<?>, Plan> plans) throws MappingException, RpiException {

		final ExecutableCartesianMotionPlan plan = (ExecutableCartesianMotionPlan) plans.get(action);
		if (plan == null) {
			throw new MappingException(
					"Failed to retrieve ExecutableCartesianMotionPlan from action " + action.toString());
		}

		OverrideParameter op = parameters.get(OverrideParameter.class);
		if (op != null) {
			if (op.getScaling() == Scaling.ABSOLUTE) {
				override = op.getOverride();
			} else {
				override = override.multiply(op.getOverride());
			}
		}
		final RealtimeDouble finalOverride = override;

		// calculate time to decelerate from maximum velocity
		CartesianParameters cartesianParameters = parameters.get(CartesianParameters.class);
		if (cartesianParameters == null) {
			throw new MappingException("No CartesianParameters given for action " + action.toString());
		}
		double decelTime = 0;
		decelTime = cartesianParameters.getMaximumPositionVelocity()
				/ cartesianParameters.getMaximumPositionAcceleration();
		if (decelTime < cartesianParameters.getMaximumRotationVelocity()
				/ cartesianParameters.getMaximumRotationAcceleration()) {
			decelTime = cartesianParameters.getMaximumRotationVelocity()
					/ cartesianParameters.getMaximumRotationAcceleration();
		}

		final RealtimeDouble cancelOverride = RealtimeDouble
				.createConditional(cancel, RealtimeDouble.createFromConstant(0), RealtimeDouble.createFromConstant(1))
				.slidingAverage(decelTime);

		final IdentityRealtimeDouble effTime = new IdentityRealtimeDouble("<<Effective Time>>", time.getRuntime());

		RealtimeTwist twist = plan.getTwistSensorAt(effTime);
		twist = RealtimeTwist.createFromLinearAngular(
				twist.getTranslationVelocity().scale(cancelOverride.multiply(override)),
				twist.getRotationVelocity().scale(cancelOverride.multiply(override)));

		CartesianPositionActionResult result = new CartesianPositionActionResult(null, null,
				RealtimePose.createFromTransformation(plan.getBaseFrame(), plan.getTransformationSensorAt(effTime)),
				RealtimeVelocity.createFromTwist(plan.getBaseFrame(), null, plan.getBaseFrame().asOrientation(),
						twist));

		result.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(IdentityRealtimeDouble.class) {

					@Override
					protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value) {
						if (value == effTime) {
							Clock clock = new Clock();
							RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, clock.getOutValue());
							ret.addDependency(cancelOverride.multiply(finalOverride), "inOverride",
									clock.getInIncrement());
							return ret;
						}
						return null;
					}
				});

		result.addRealtimeValueAliasFactory(
				new TypedRealtimeValueAliasFactory<Boolean, ActionRealtimeBoolean>(ActionRealtimeBoolean.class) {
					@Override
					protected RealtimeValue<Boolean> createAlias(ActionRealtimeBoolean value) {
						if (value.getAction() != action) {
							return null;
						}
						if (value instanceof Action.CompletedRealtimeBoolean) {
							return cancelOverride.less(0.001).or(effTime.greater(plan.getTotalTime()));
						} else {
							return plan.getStateSensorAt(effTime, value);
						}
					}
				});
		return result;
	}
}
