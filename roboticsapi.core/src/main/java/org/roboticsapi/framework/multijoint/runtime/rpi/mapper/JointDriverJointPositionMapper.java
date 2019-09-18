/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.CycleTime;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.IndexedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.multijoint.IllegalJointValueException;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.parameter.JointParameters;
import org.roboticsapi.framework.multijoint.runtime.rpi.primitives.JointPosition;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;

public class JointDriverJointPositionMapper
		implements ActuatorDriverMapper<IndexedActuatorDriver<Joint>, JointPositionActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(IndexedActuatorDriver<Joint> actuatorDriver,
			JointPositionActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, RealtimeBoolean.TRUE);

		JointParameters jointParameters = parameters.get(JointParameters.class);
		RealtimeDouble cur = actionResult.getPosition();

		final IdentityRealtimeDouble dt = new IdentityRealtimeDouble("dt", time.getRuntime());
		if (jointParameters != null) {

			// joint limits
			RealtimeDouble upperLimit = RealtimeDouble.createFromConstant(jointParameters.getMaximumPosition());
			RealtimeDouble lowerLimit = RealtimeDouble.createFromConstant(jointParameters.getMinimumPosition());
			RealtimeDouble velLimit = RealtimeDouble.createFromConstant(jointParameters.getMaximumVelocity() * 2.0);
			RealtimeDouble accLimit = RealtimeDouble
					.createFromConstant(jointParameters.getMaximumAcceleration() * 0.95);

			// distance and maximum velocity to joint limit
			RealtimeDouble upperDistance = upperLimit.minus(cur);
			RealtimeDouble lowerDistance = cur.minus(lowerLimit);
			RealtimeDouble maxVel = upperDistance.multiply(accLimit).multiply(2).sqrt();
			RealtimeDouble minVel = lowerDistance.multiply(accLimit).multiply(2).sqrt().negate();

			// beyond joint limit -> vel = 0
			maxVel = RealtimeDouble.createConditional(upperDistance.less(0), RealtimeDouble.createFromConstant(0),
					maxVel);
			minVel = RealtimeDouble.createConditional(lowerDistance.less(0), RealtimeDouble.createFromConstant(0),
					minVel);

			// limit to configured maximum velocity
			maxVel = RealtimeDouble.createConditional(maxVel.greater(velLimit), velLimit, maxVel);
			minVel = RealtimeDouble.createConditional(minVel.less(velLimit.negate()), velLimit.negate(), minVel);

			// maximum and minimum allowed position in the next cycle
			RealtimeDouble maxPos = cur.add(maxVel.multiply(dt));
			RealtimeDouble minPos = cur.add(minVel.multiply(dt));
			RealtimeDouble nan = RealtimeDouble.createFromConstant(0).divide(0);

			// check & limit joint position
			RealtimeBoolean tooBig = cur.greater(maxPos.add(JointParameters.PRECISION_EPSILON));
			RealtimeBoolean tooSmall = cur.less(minPos.minus(JointParameters.PRECISION_EPSILON));
			cur = RealtimeDouble.createConditional(tooBig, /* maxPos */nan, cur);
			cur = RealtimeDouble.createConditional(tooSmall, /* minPos */nan, cur);
		}

		final JointPosition jnt = ret
				.add(new JointPosition(actuatorDriver.getRpiDeviceName(), actuatorDriver.getIndex()));
		jnt.getInPosition().setDebug(2);
		ret.addDependency(cur, "inPos", jnt.getInPosition());

		ret.addRealtimeValueFragmentFactory(
				new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(IdentityRealtimeDouble.class) {
					@Override
					protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value)
							throws MappingException, RpiException {
						if (value != dt) {
							return null;
						}
						return new RealtimeDoubleFragment(dt, new CycleTime().getOutValue());
					}
				});

		ret.addException(ConcurrentAccessException.class, jnt.getOutErrorConcurrentAccess(), "outConcurrentAccess");
		ret.addException(ActuatorNotOperationalException.class, jnt.getOutErrorJointFailed(), "outJointFailed");
		ret.addException(IllegalJointValueException.class, jnt.getOutErrorIllegalPosition(), "outIllegalPosition");
		return ret;
	}
}
