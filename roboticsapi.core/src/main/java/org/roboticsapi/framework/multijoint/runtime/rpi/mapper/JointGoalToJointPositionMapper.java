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
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleMultiply;
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
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionGoalActionResult;

public class JointGoalToJointPositionMapper
		implements ActuatorDriverMapper<NamedActuatorDriver<Joint>, JointPositionGoalActionResult> {

	private final double maxError;

	public JointGoalToJointPositionMapper(double maxError) {
		this.maxError = maxError;
	}

	@Override
	public RealtimeValueConsumerFragment map(NamedActuatorDriver<Joint> actuatorDriver,
			JointPositionGoalActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		JointParameters jp = parameters.get(JointParameters.class);
		final double maxVel = jp.getMaximumVelocity();
		final double maxAcc = jp.getMaximumAcceleration();

		final RealtimeDouble goal = actionResult.getGoal();
		final RealtimeDouble pos = actuatorDriver.getDevice().getCommandedRealtimePosition();
		final RealtimeDouble vel = actuatorDriver.getDevice().getCommandedRealtimeVelocity();

		// cancel scales override to 0
		double cancelDuration = maxVel / maxAcc;
		RealtimeDouble overrideScale = RealtimeDouble
				.createConditional(cancel, RealtimeDouble.createFromConstant(0), RealtimeDouble.createFromConstant(1))
				.slidingAverage(cancelDuration);

		RealtimeBoolean isStopped = cancel.and(overrideScale.less(0.01));
		final RealtimeDouble scaledOverride = override.multiply(overrideScale);

		final IdentityRealtimeDouble nextPos = new IdentityRealtimeDouble("<<nextPosition>>(" + goal + ")",
				time.getRuntime());

		final IdentityRealtimeDouble nextVel = new IdentityRealtimeDouble("<<nextVelocity>>(" + goal + ")",
				time.getRuntime());

		ActionResult result = new JointPositionActionResult(null, null, nextPos, nextVel);

		RealtimeValueConsumerFragment inner = registry.mapDriver(actuatorDriver, result, parameters, cancel, override,
				time);

		RealtimeBoolean arrived = goal.add(pos.negate()).abs().less(maxError);
		RealtimeBoolean completed = arrived.or(isStopped);

		RealtimeValueConsumerFragment ret = new ActuatorFragment(actuatorDriver, completed);
		ret.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(IdentityRealtimeDouble.class) {
					@Override
					protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value)
							throws MappingException, RpiException {
						if (value != nextPos && value != nextVel) {
							return null;
						}

						RealtimeDoubleFragment fragment = new RealtimeDoubleFragment(nextPos);

						// OTG
						OTG otg = fragment.add(new OTG());
						otg.getInCurPos().setDebug(2);
						otg.getInCurVel().setDebug(2);
						otg.getInDestPos().setDebug(2);

						FragmentInPort overridePort = fragment.addInPort("inOverride");
						fragment.addDependency(scaledOverride, overridePort);

						DoubleMultiply posVel = fragment.add(new DoubleMultiply(maxVel, 0.0));
						fragment.connect(overridePort.getInternalOutPort(), posVel.getInSecond());
						fragment.connect(posVel.getOutValue(), otg.getInMaxVel());

						DoubleMultiply posAcc = fragment.add(new DoubleMultiply(maxAcc, 0.0));
						fragment.connect(overridePort.getInternalOutPort(), posAcc.getInSecond());
						fragment.connect(posAcc.getOutValue(), otg.getInMaxAcc());

						fragment.addDependency(pos, "inPos", otg.getInCurPos());
						fragment.addDependency(vel, "inVel", otg.getInCurVel());
						fragment.addDependency(goal, "inGoal", otg.getInDestPos());

						if (value == nextPos) {
							fragment.defineResult(otg.getOutPos());
						} else {
							fragment.defineResult(otg.getOutVel());
						}
						return fragment;
					}
				});

		ret.addWithDependencies(inner);
		return ret;
	}
}
