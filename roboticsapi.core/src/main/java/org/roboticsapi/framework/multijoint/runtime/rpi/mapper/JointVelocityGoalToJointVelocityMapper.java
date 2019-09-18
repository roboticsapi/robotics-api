/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.OTG;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.parameter.JointParameters;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointVelocityActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointVelocityGoalActionResult;

public class JointVelocityGoalToJointVelocityMapper
		implements ActuatorDriverMapper<NamedActuatorDriver<Joint>, JointVelocityGoalActionResult> {

	private final double maxError;

	public JointVelocityGoalToJointVelocityMapper(double maxError) {
		this.maxError = maxError;
	}

	@Override
	public RealtimeValueConsumerFragment map(NamedActuatorDriver<Joint> actuatorDriver,
			JointVelocityGoalActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		JointParameters jp = parameters.get(JointParameters.class);
		final double maxVel = jp.getMaximumVelocity();
		final double maxAcc = jp.getMaximumAcceleration();

		final RealtimeDouble goal = actionResult.getVelocityGoal();
		final RealtimeDouble vel = actuatorDriver.getDevice().getCommandedRealtimeVelocity();

		// cancel scales override to 0
		double cancelDuration = maxVel / maxAcc;
		RealtimeDouble overrideScale = RealtimeDouble
				.createConditional(cancel, RealtimeDouble.createFromConstant(0), RealtimeDouble.createFromConstant(1))
				.slidingAverage(cancelDuration);

		RealtimeBoolean isStopped = cancel.and(overrideScale.less(0.01));
		final RealtimeDouble scaledOverride = override.multiply(overrideScale);

		final IdentityRealtimeDouble nextVel = new IdentityRealtimeDouble("<<nextVelocity>>(" + goal + ")",
				time.getRuntime());

		ActionResult result = new JointVelocityActionResult(null, null, nextVel);

		RealtimeValueConsumerFragment inner = registry.mapDriver(actuatorDriver, result, parameters, cancel, override,
				time);

		RealtimeBoolean arrived = goal.add(vel.negate()).abs().less(maxError);
		RealtimeBoolean completed = arrived.or(isStopped);

		RealtimeValueConsumerFragment ret = new ActuatorFragment(actuatorDriver, completed);
		ret.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(IdentityRealtimeDouble.class) {
					@Override
					protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value)
							throws MappingException, RpiException {
						if (value != nextVel) {
							return null;
						}

						RealtimeDoubleFragment fragment = new RealtimeDoubleFragment(nextVel);

						// OTG
						OTG otg = fragment.add(new OTG());
						fragment.addDependency(scaledOverride.multiply(maxVel), "inMaxVel", otg.getInMaxVel());
						fragment.addDependency(scaledOverride.multiply(maxAcc), "inMaxAcc", otg.getInMaxAcc());
						fragment.addDependency(vel, "inPos", otg.getInCurPos());
						fragment.addDependency(goal, "inGoal", otg.getInDestPos());

						fragment.defineResult(otg.getOutPos());
						return fragment;
					}
				});

		ret.addWithDependencies(inner);
		return ret;
	}
}
