/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result;

import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.State;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.result.impl.ExceptionPortFactory;
import org.roboticsapi.runtime.mapping.result.impl.StatePortFactory;

public interface CommandMapperResult extends MapperResult {

	/**
	 * Retrieves one Boolean dataflow out port - if it is true, the given event has
	 * occured
	 * 
	 * @param state event to get ports for
	 * @return Boolean data flow telling that the event has occured
	 * @throws MappingException
	 */
	List<DataflowOutPort> getStatePort(State state) throws MappingException;

	List<DataflowOutPort> getExceptionPort(CommandRealtimeException exception) throws MappingException;

	/**
	 * Retrieves the command this command mapper result belongs to
	 * 
	 * @return the command
	 */
	Command getCommand();

	public <T extends State> void addStatePortFactory(Class<T> stateType, StatePortFactory factory);

	public <T extends State> void addStatePort(Class<T> stateType, DataflowOutPort port);

	public void addStatesPort(List<Class<? extends State>> stateTypes, DataflowOutPort port);

	public void addStatesPortFactory(List<Class<? extends State>> stateTypes, StatePortFactory factory);

	public <T extends CommandRealtimeException> void addExceptionPortFactory(Class<T> exceptionType,
			ExceptionPortFactory factory);

	public void addExceptionsPortFactory(List<Class<? extends CommandRealtimeException>> exceptionTypes,
			ExceptionPortFactory factory);

	public <T extends CommandRealtimeException> void addExceptionPort(Class<T> exception, DataflowOutPort port);

	public void addExceptionsPort(List<Class<? extends CommandRealtimeException>> exceptions, DataflowOutPort port);
}
