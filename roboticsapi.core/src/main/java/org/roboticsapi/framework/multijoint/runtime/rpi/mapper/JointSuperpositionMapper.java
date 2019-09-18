/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import java.util.Map;

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
import org.roboticsapi.framework.multijoint.action.JointSuperposition;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;

@SuppressWarnings("rawtypes")
public class JointSuperpositionMapper implements ActionMapper<JointSuperposition> {

	@Override
	public ActionResult map(JointSuperposition action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {

		ActionResult innerResult = registry.mapAction(action.getInnerAction(), parameters, cancel, override, time,
				plans);
		if (innerResult instanceof MultiJointActionResult) {
			ActionResult[] innerResults = ((MultiJointActionResult) innerResult).getJointResults();
			for (int i = 0; i < innerResults.length; i++) {
				if (innerResults[i] instanceof JointPositionActionResult) {
					JointPositionActionResult pos = (JointPositionActionResult) innerResults[i];
					innerResults[i] = new JointPositionActionResult(null, null,
							pos.getPosition().add(action.getSuperposition(i)));
				} else {
					throw new MappingException("Mapping of inner action did not return JointPositionActionResult");
				}
			}
			MultiJointActionResult ret = new MultiJointActionResult(action, RealtimeBoolean.TRUE, innerResults);
			ret.addRealtimeValueSource(innerResult);
			return ret;

		} else {
			throw new MappingException("Mapping of inner action did not return JointPositionResult");
		}
	}
}
