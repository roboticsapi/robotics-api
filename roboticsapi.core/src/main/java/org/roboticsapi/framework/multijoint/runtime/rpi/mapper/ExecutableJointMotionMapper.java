/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
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
import org.roboticsapi.framework.multijoint.action.ExecutableJointMotionPlan;
import org.roboticsapi.framework.multijoint.action.JointMotion;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.multijoint.parameter.JointParameters;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;

public class ExecutableJointMotionMapper<A extends JointMotion<? extends ExecutableJointMotionPlan>>
		implements ActionMapper<A> {

	@Override
	public ActionResult map(final A action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, final RealtimeDouble override, RealtimeDouble time,
			Map<PlannedAction<?>, Plan> plans) throws MappingException, RpiException {

		if (plans.get(action) == null) {
			try {
				action.calculatePlan(plans, parameters);
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
		}
		final ExecutableJointMotionPlan plan = (ExecutableJointMotionPlan) plans.get(action);

		// calculate time to decelerate from maximum velocity
		JointDeviceParameters jointParameters = parameters.get(JointDeviceParameters.class);
		if (jointParameters == null) {
			throw new MappingException("No JointDeviceParameters given for action " + action.toString());
		}
		double decelTime = 0;
		for (int i = 0; i < jointParameters.getJointCount(); i++) {
			JointParameters parameter = jointParameters.getJointParameters(i);
			if (decelTime < parameter.getMaximumVelocity() / parameter.getMaximumAcceleration()) {
				decelTime = parameter.getMaximumVelocity() / parameter.getMaximumAcceleration();
			}
		}

		final RealtimeDouble cancelOverride = RealtimeDouble
				.createConditional(cancel, RealtimeDouble.createFromConstant(0), RealtimeDouble.createFromConstant(1))
				.slidingAverage(decelTime);

		final IdentityRealtimeDouble effTime = new IdentityRealtimeDouble("<<Effective Time>>", time.getRuntime());

		MultiJointActionResult result = new MultiJointActionResult(action,
				cancelOverride.less(0.001).or(effTime.greater(plan.getTotalTime())),
				Stream.of(plan.getJointPositionSensorAt(effTime))
						.map(pos -> new JointPositionActionResult(null, null, pos)).collect(Collectors.toList()));

		result.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(IdentityRealtimeDouble.class) {

					@Override
					protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value) {
						if (value == effTime) {
							Clock clock = new Clock();
							RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, clock.getOutValue());
							ret.addDependency(cancelOverride.multiply(override), "inOverride", clock.getInIncrement());
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
