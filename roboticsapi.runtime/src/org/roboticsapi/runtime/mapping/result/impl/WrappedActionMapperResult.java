/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;

public class WrappedActionMapperResult extends BaseActionMapperResult {

	private final ActionMapperResult innerActionMapperResult;

	public WrappedActionMapperResult(Action action, final Action innerAction, NetFragment fragment, ActionResult result,
			final ActionMapperResult innerActionMapperResult) {
		super(action, fragment, result, new AbstractStatePortFactory<Action.CompletedState>() {

			@Override
			public List<DataflowOutPort> createTypedStatePort(org.roboticsapi.core.Action.CompletedState state)
					throws MappingException {
				return innerActionMapperResult.getStatePort(innerAction.getCompletedState());
			}
		});
		this.innerActionMapperResult = innerActionMapperResult;
	}

	@Override
	public List<DataflowOutPort> getStatePort(ActionState state) throws MappingException {
		List<DataflowOutPort> states = new ArrayList<DataflowOutPort>();
		List<DataflowOutPort> superStates = super.getStatePort(state);
		if (superStates != null) {
			states.addAll(superStates);
		}
		List<DataflowOutPort> innerStates = innerActionMapperResult.getStatePort(state);
		if (innerStates != null) {
			states.addAll(innerStates);
		}
		return states;
	}

	@Override
	public List<DataflowOutPort> getExceptionPort(ActionRealtimeException exception) throws MappingException {

		List<DataflowOutPort> ports = new ArrayList<DataflowOutPort>();

		// collect ports returned by super implementation
		List<DataflowOutPort> superPorts = super.getExceptionPort(exception);
		if (superPorts != null) {
			ports.addAll(superPorts);
		}

		// collect ports of inner (wrapped) ActionResult
		List<DataflowOutPort> innerPorts = innerActionMapperResult.getExceptionPort(exception);
		if (innerPorts != null) {
			ports.addAll(innerPorts);
		}

		return ports;
	}

}
