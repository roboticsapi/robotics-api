/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.WrappedAction;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class WrappedActionMapper<A extends WrappedAction> implements ActionMapper<SoftRobotRuntime, A> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, A action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		ActionMapperResult mappedInnerAction = runtime.getMapperRegistry().mapAction(runtime, action.getWrappedAction(),
				parameters, ports.cancelPort, ports.overridePort, ports.actionPlans);

		return getWrapperMapperResult(runtime, action, mappedInnerAction, parameters, ports);
	}

	protected abstract ActionMapperResult getWrapperMapperResult(SoftRobotRuntime runtime, A action,
			ActionMapperResult mappedInnerAction, DeviceParameterBag parameters, ActionMappingContext ports)
			throws MappingException;

}
