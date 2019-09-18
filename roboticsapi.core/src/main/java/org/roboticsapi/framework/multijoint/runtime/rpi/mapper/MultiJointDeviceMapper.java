/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.multijoint.parameter.JointParameters;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;

public class MultiJointDeviceMapper<DD extends NamedActuatorDriver<? extends MultiJointDevice>>
		implements ActuatorDriverMapper<DD, MultiJointActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(DD actuatorDriver, MultiJointActionResult actionResult,
			DeviceParameterBag parameters, MapperRegistry registry, RealtimeBoolean cancel, RealtimeDouble override,
			RealtimeDouble time) throws MappingException, RpiException {

		JointDeviceParameters jointParameters = parameters.get(JointDeviceParameters.class);

		List<RealtimeValueConsumerFragment> children = new ArrayList<>();
		RealtimeBoolean completed = RealtimeBoolean.TRUE;

		for (int i = 0; i < actuatorDriver.getDevice().getJointCount(); i++) {
			DeviceParameterBag jParameters = parameters;
			if (jointParameters != null) {
				JointParameters jointParameter = jointParameters.getJointParameters(i);
				if (jointParameter != null) {
					jParameters = parameters.withParameters(jointParameter);
				}
			}
			ActionResult jResult = actionResult.getJointResults()[i];

			children.add(
					registry.mapDriver(actuatorDriver.getDevice().getJoint(i).getDriver(actuatorDriver.getRuntime()),
							jResult, jParameters, cancel, override, time));

			completed = completed.and(actuatorDriver.getDevice().getJoint(i).getCompletedState(null));

		}

		ActuatorFragment ret = new ActuatorFragment(actuatorDriver, completed);

		for (RealtimeValueConsumerFragment child : children) {
			ret.addWithDependencies(child);
		}

		ret.addException(ActuatorNotOperationalException.class, RealtimeBoolean.FALSE);
		ret.addException(ConcurrentAccessException.class, RealtimeBoolean.FALSE);

		return ret;

	}
}
