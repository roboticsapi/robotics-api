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

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.framework.multijoint.action.JointJogging;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointVelocityActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;

public class JointJoggingMapper implements ActionMapper<JointJogging> {

	@Override
	public ActionResult map(JointJogging action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {

		MultiJointActionResult ret = new MultiJointActionResult(action, cancel,
				Stream.of(action.getVelocity()).map(
						vel -> new JointVelocityActionResult(null, null, vel.multiply(override).slidingAverage(0.2)))
						.collect(Collectors.toList()));
		return ret;
	}
}
