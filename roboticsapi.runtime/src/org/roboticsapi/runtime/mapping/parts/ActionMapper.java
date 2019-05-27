/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.MapperInterface;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public interface ActionMapper<R extends RoboticsRuntime, A extends Action> extends MapperInterface {
	ActionMapperResult map(R runtime, A action, DeviceParameterBag parameters, ActionMappingContext ports)
			throws MappingException, RPIException;
}
