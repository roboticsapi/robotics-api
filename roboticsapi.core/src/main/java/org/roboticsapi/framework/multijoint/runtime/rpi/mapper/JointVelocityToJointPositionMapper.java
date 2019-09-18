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
import org.roboticsapi.facet.runtime.rpi.core.primitives.CycleTime;
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
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointVelocityActionResult;

public class JointVelocityToJointPositionMapper
		implements ActuatorDriverMapper<NamedActuatorDriver<Joint>, JointVelocityActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(NamedActuatorDriver<Joint> actuatorDriver,
			JointVelocityActionResult actionResult, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException, RpiException {

		final IdentityRealtimeDouble dt = new IdentityRealtimeDouble("dt", time.getRuntime());
		RealtimeDouble vel = actionResult.getVelocity();
		RealtimeDouble curpos = actuatorDriver.getDevice().getCommandedRealtimePosition();
		RealtimeDouble pos = curpos.add(vel.multiply(dt));
		ActionResult result = new JointPositionActionResult(null, null, pos);

		RealtimeValueConsumerFragment inner = registry.mapDriver(actuatorDriver, result, parameters, cancel, override,
				time);

		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, RealtimeBoolean.TRUE);
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

		ret.addWithDependencies(inner);
		return ret;
	}
}
