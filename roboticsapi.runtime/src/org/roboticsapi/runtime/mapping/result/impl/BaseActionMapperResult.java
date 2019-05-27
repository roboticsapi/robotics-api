/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.TrueFragment;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;

public class BaseActionMapperResult extends BaseMapperResult implements ActionMapperResult {
	private final ActionResult result;
	private final Action action;

	private BaseActionMapperResult(Action action, NetFragment fragment, ActionResult result) {
		super(fragment);
		this.result = result;
		this.action = action;

		addStatePort(Action.ActiveState.class, fragment.add(new TrueFragment()).getTrueOut());
	}

	public BaseActionMapperResult(Action action, NetFragment fragment, ActionResult result, DataflowOutPort completed) {
		this(action, fragment, result);

		addStatePort(Action.CompletedState.class, completed);
	}

	public BaseActionMapperResult(Action action, NetFragment fragment, ActionResult result,
			final List<DataflowOutPort> completed) {
		this(action, fragment, result);

		addStatePortFactory(Action.CompletedState.class, new AbstractStatePortFactory<Action.CompletedState>() {

			@Override
			public List<DataflowOutPort> createTypedStatePort(Action.CompletedState state) throws MappingException {
				return completed;
			}
		});
	}

	public BaseActionMapperResult(Action action, NetFragment fragment, ActionResult result,
			StatePortFactory completedFactory) {
		this(action, fragment, result);

		addStatePortFactory(Action.CompletedState.class, completedFactory);
	}

	@Override
	public ActionResult getActionResult() {
		return result;
	}

	@Override
	public <T extends ActionRealtimeException> void addActionExceptionPort(Class<T> exception, DataflowOutPort port) {
		super.addExceptionPort(exception, port);
	}

	@Override
	public void addActionExceptionsPort(List<Class<? extends ActionRealtimeException>> exceptions,
			DataflowOutPort port) {
		for (Class<? extends ActionRealtimeException> e : exceptions) {
			addActionExceptionPort(e, port);
		}
	}

	@Override
	public <T extends ActionRealtimeException> void addActionExceptionPortFactory(Class<T> exceptionType,
			ExceptionPortFactory factory) {
		super.addExceptionPortFactory(exceptionType, factory);
	}

	@Override
	public void addActionExceptionsPortFactory(List<Class<? extends ActionRealtimeException>> exceptionTypes,
			ExceptionPortFactory factory) {
		for (Class<? extends ActionRealtimeException> type : exceptionTypes) {
			addActionExceptionPortFactory(type, factory);
		}
	}

	@Override
	public <T extends ActionState> void addActionStatePort(Class<T> stateType, DataflowOutPort port) {
		super.addStatePort(stateType, port);
	}

	@Override
	public void addActionStatesPort(List<Class<? extends ActionState>> stateTypes, DataflowOutPort port) {
		for (Class<? extends ActionState> stateType : stateTypes) {
			addActionStatePort(stateType, port);
		}
	}

	@Override
	public <T extends ActionState> void addActionStatePortFactory(Class<T> stateType, StatePortFactory factory) {
		super.addStatePortFactory(stateType, factory);
	}

	@Override
	public void addActionStatesPortFactory(List<Class<? extends ActionState>> stateTypes, StatePortFactory factory) {
		for (Class<? extends ActionState> type : stateTypes) {
			addActionStatePortFactory(type, factory);
		}

	}

	@Override
	public List<DataflowOutPort> getExceptionPort(ActionRealtimeException exception) throws MappingException {
		if (exception.getAction() == null || exception.getAction().equals(action)) {
			return super.getExceptionPort(exception);
		} else {
			return null;
		}

	}

	@Override
	public List<DataflowOutPort> getStatePort(ActionState state) throws MappingException {
		if (state.getAction() == null || state.getAction().equals(action)) {
			return super.getStatePort(state);
		} else {
			return null;
		}

	}
}
