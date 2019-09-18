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
import org.roboticsapi.framework.multijoint.action.SwitchJointController;
import org.roboticsapi.framework.multijoint.controller.JointImpedanceController;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointImpedanceControllerActionResult;

public class SwitchControllerJointImpedanceMapper implements ActionMapper<SwitchJointController> {

	@Override
	public ActionResult map(SwitchJointController action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {
		if (action.getController() == null) {
			return null;
		}
		if (action.getController().getClass() != JointImpedanceController.class) {
			return null;
		}

		JointImpedanceController ji = (JointImpedanceController) action.getController();

		RealtimeDouble[] stiffnessPorts = new RealtimeDouble[ji.getJointCount()];
		RealtimeDouble[] dampingPorts = new RealtimeDouble[ji.getJointCount()];
		RealtimeDouble[] addTorquePorts = new RealtimeDouble[ji.getJointCount()];
		double[] maxTorques = new double[ji.getJointCount()];

		for (int i = 0; i < ji.getJointCount(); i++) {
			stiffnessPorts[i] = RealtimeDouble.createFromConstant(ji.getImpedanceSettings(i).getStiffness());
			dampingPorts[i] = RealtimeDouble.createFromConstant(ji.getImpedanceSettings(i).getDamping());
			addTorquePorts[i] = RealtimeDouble.createFromConstant(ji.getImpedanceSettings(i).getAddTorque());
			maxTorques[i] = ji.getImpedanceSettings(i).getMaxTorque();
		}

		JointImpedanceControllerActionResult result = new JointImpedanceControllerActionResult(action,
				RealtimeBoolean.TRUE, 5, stiffnessPorts, dampingPorts, addTorquePorts, maxTorques);

		return result;
	}
}
