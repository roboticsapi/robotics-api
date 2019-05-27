/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result;

import java.util.List;

import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.result.impl.ExceptionPortFactory;
import org.roboticsapi.runtime.mapping.result.impl.StatePortFactory;

public interface ActionMapperResult extends MapperResult {
	ActionResult getActionResult();

	List<DataflowOutPort> getStatePort(ActionState state) throws MappingException;

	List<DataflowOutPort> getExceptionPort(ActionRealtimeException exception) throws MappingException;

	public <T extends ActionState> void addActionStatePortFactory(Class<T> stateType, StatePortFactory factory);

	public <T extends ActionState> void addActionStatePort(Class<T> stateType, DataflowOutPort port);

	public void addActionStatesPort(List<Class<? extends ActionState>> stateTypes, DataflowOutPort port);

	public void addActionStatesPortFactory(List<Class<? extends ActionState>> stateTypes, StatePortFactory factory);

	public <T extends ActionRealtimeException> void addActionExceptionPortFactory(Class<T> exceptionType,
			ExceptionPortFactory factory);

	public void addActionExceptionsPortFactory(List<Class<? extends ActionRealtimeException>> exceptionTypes,
			ExceptionPortFactory factory);

	public <T extends ActionRealtimeException> void addActionExceptionPort(Class<T> exception, DataflowOutPort port);

	public void addActionExceptionsPort(List<Class<? extends ActionRealtimeException>> exceptions,
			DataflowOutPort port);
}
