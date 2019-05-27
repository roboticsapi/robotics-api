/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result;

import java.util.List;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public interface ActuatorDriverMapperResult extends MapperResult {

	ActuatorDriver getActuatorDriver();

	List<DataflowOutPort> getStatePort(ActuatorState state) throws MappingException;

	List<DataflowOutPort> getExceptionPort(ActuatorDriverRealtimeException exception) throws MappingException;

}
