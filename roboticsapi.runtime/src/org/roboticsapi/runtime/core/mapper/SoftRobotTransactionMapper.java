/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.runtime.MappedCommand;
import org.roboticsapi.runtime.SoftRobotCommandPorts;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.ReadIntFromNet;
import org.roboticsapi.runtime.core.netcomm.WriteDoubleToNet;
import org.roboticsapi.runtime.core.primitives.BooleanAnd;
import org.roboticsapi.runtime.core.primitives.BooleanNot;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.core.primitives.Cancel;
import org.roboticsapi.runtime.core.primitives.EdgeDetection;
import org.roboticsapi.runtime.core.primitives.IntConditional;
import org.roboticsapi.runtime.core.primitives.IntValue;
import org.roboticsapi.runtime.core.primitives.Takeover;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.OrFragment;
import org.roboticsapi.runtime.mapping.parts.TransactionMapper;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.TransactionMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.SimpleTransactionMapperResult;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotTransactionMapper implements TransactionMapper<SoftRobotRuntime, Command> {

	@Override
	public TransactionMapperResult map(SoftRobotRuntime runtime, final Command command)
			throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("RPI net");
		Cancel cancel = fragment.add(new Cancel());
		Takeover takeover = fragment.add(new Takeover());
		BooleanValue active = fragment.add(new BooleanValue(true));
		BooleanValue stop = fragment.add(new BooleanValue(false));
		EdgeDetection start = fragment.add(new EdgeDetection());
		start.getInValue().connectTo(active.getOutValue());

		WriteDoubleToNet overrideBlock = fragment.add(new WriteDoubleToNet("Override", runtime.getOverride()));
		DataflowOutPort override = fragment.addOutPort(new DoubleDataflow(), true, overrideBlock.getOutValue());

		fragment.addOutPort(new StateDataflow(), false, active.getOutValue());
		DataflowOutPort stopPort = fragment.addOutPort(new EventDataflow(), false, stop.getOutValue());
		DataflowOutPort cancelPort = fragment.addOutPort(new StateDataflow(), false, cancel.getOutCancel());
		DataflowOutPort startPort = fragment.addOutPort(new EventDataflow(), false, start.getOutValue());

		CommandMapperResult mappedCommand;
		if (command instanceof MappedCommand) {
			mappedCommand = ((MappedCommand) command).getMappingResult();

			SoftRobotCommandPorts mappingPorts = ((MappedCommand) command).getMappingPorts();
			mappedCommand.getNetFragment().add(mappingPorts);
			fragment.connect(startPort, mappingPorts.inStart);
			fragment.connect(stopPort, mappingPorts.inStop);
			fragment.connect(cancelPort, mappingPorts.inCancel);
			fragment.connect(override, mappingPorts.inOverride);

		} else {
			mappedCommand = runtime.getMapperRegistry().mapCommand(runtime, command, startPort, stopPort, cancelPort,
					override);
		}
		fragment.add(mappedCommand.getNetFragment());

		// all ports that lead to command completion;
		// part 1: Command's CompletedState
		List<DataflowOutPort> commandCompletedPorts = mappedCommand.getStatePort(command.getCompletedState());

		// part 2: Takeover & TakeoverAccepted
		BooleanAnd takeoverAnd = fragment.add(new BooleanAnd(false, false));
		takeoverAnd.getInFirst().connectTo(takeover.getOutTakeover());
		List<DataflowOutPort> takeoverAcceptedPorts = mappedCommand.getStatePort(command.getTakeoverAllowedState());
		if (takeoverAcceptedPorts != null) {
			DataflowOutPort takeoverAccepted = fragment.add(new OrFragment(new StateDataflow(), takeoverAcceptedPorts))
					.getOrOut();

			fragment.connect(takeoverAccepted,
					fragment.addInPort(new StateDataflow(), true, takeoverAnd.getInSecond()));
		}
		commandCompletedPorts.add(fragment.addOutPort(new StateDataflow(), true, takeoverAnd.getOutValue()));

		// part 3: Command is not active
		DataflowOutPort commandActive = fragment
				.add(new OrFragment(new StateDataflow(), mappedCommand.getStatePort(command.getActiveState())))
				.getOrOut();
		BooleanNot not = fragment.add(new BooleanNot());
		fragment.connect(commandActive, fragment.addInPort(new StateDataflow(), true, not.getInValue()));
		commandCompletedPorts.add(fragment.addOutPort(new StateDataflow(), true, not.getOutValue()));

		// final part: termination means any other part is true
		DataflowOutPort terminationPort = fragment.add(new OrFragment(new StateDataflow(), commandCompletedPorts))
				.getOrOut();

		NetFragment errorHandling = fragment.add(new NetFragment("Unhandled errors"));

		List<DataflowOutPort> allErrorPorts = new ArrayList<DataflowOutPort>();
		// OrState anyError = new OrState();
		final List<CommandRealtimeException> unhandledExceptions = new ArrayList<CommandRealtimeException>(
				command.getExceptions());
		unhandledExceptions.add(0, null);
		OutPort errorPort = errorHandling.add(new IntValue(0)).getOutValue();
		for (int i = 1; i < unhandledExceptions.size(); i++) {
			List<DataflowOutPort> errorPorts = mappedCommand.getExceptionPort(unhandledExceptions.get(i));

			if (errorPorts == null) {
				continue;
			}

			allErrorPorts.addAll(errorPorts);

			IntConditional errorCond = errorHandling.add(new IntConditional());
			errorCond.setTrue(i);
			errorCond.getInFalse().connectTo(errorPort);
			errorHandling.connect(errorHandling.add(new OrFragment(new StateDataflow(), errorPorts)).getOrOut(),
					errorHandling.addInPort(new StateDataflow(), true, errorCond.getInCondition()));
			errorPort = errorCond.getOutValue();
		}

		ReadIntFromNet errorNetcomm = errorHandling.add(new ReadIntFromNet("Error"));
		errorNetcomm.getInValue().connectTo(errorPort);
		errorNetcomm.getNetcomm().addNetcommListener(new NetcommListenerAdapter() {
			@Override
			public void valueChanged(NetcommValue value) {
				int errorNr = Integer.parseInt(value.getString());
				if (errorNr != 0) {
					command.getCommandHandle().throwException(unhandledExceptions.get(errorNr));
				}
			}

		});

		DataflowOutPort failurePort = errorHandling.addOutPort(new StateDataflow(), false, errorPort);

		return new SimpleTransactionMapperResult(fragment, terminationPort, failurePort);
	}
}
