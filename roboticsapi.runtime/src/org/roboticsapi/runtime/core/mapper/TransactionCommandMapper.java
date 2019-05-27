/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.SensorState;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TargetedEventHandler;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.TransactionCommand.ConditionalCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.state.CommandState;
import org.roboticsapi.core.state.DerivedState;
import org.roboticsapi.runtime.MappedCommand;
import org.roboticsapi.runtime.SoftRobotCommandPorts;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughInPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.AndFragment;
import org.roboticsapi.runtime.mapping.parts.CommandMappingPorts;
import org.roboticsapi.runtime.mapping.parts.DelayedStateActivationFragment;
import org.roboticsapi.runtime.mapping.parts.OrFragment;
import org.roboticsapi.runtime.mapping.parts.RisingEdgeDetectionFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class TransactionCommandMapper extends SoftRobotCommandMapper<SoftRobotRuntime, TransactionCommand> {

	public static class Result extends AbstractCommandMapperResult {

		private final Map<Command, CommandMapperResult> commandResults;

		public Result(NetFragment netFragment, ActiveNetFragment activeFragment, TransactionCommand command,
				final Map<Command, CommandMapperResult> commandResults, DataflowOutPort cancelState,
				DataflowOutPort activeState) {
			super(netFragment, activeFragment, command, activeState, cancelState);
			this.commandResults = commandResults;

		}

		@Override
		public TransactionCommand getCommand() {
			return (TransactionCommand) super.getCommand();
		}

		@Override
		protected List<DataflowOutPort> getStatePortInternal(State state) throws MappingException {
			List<DataflowOutPort> superPorts = super.getStatePortInternal(state);

			// also delegate other events to child commands
			List<DataflowOutPort> childPorts = new Vector<DataflowOutPort>();
			if (superPorts != null) {
				childPorts.addAll(superPorts);
			}

			// do not evaluate composed events here
			if (state instanceof DerivedState) {
			}
			// do not evaluate SensorStates here
			else if (state instanceof SensorState) {
			}

			// do not evaluate CommandStates of the TransactionCommand
			// itself
			else if (state instanceof CommandState && getCommand().equals(((CommandState) state).getCommand())) {
			}

			else {
				for (CommandMapperResult result : commandResults.values()) {
					List<DataflowOutPort> cmdStatePort = result.getStatePort(state);
					if (cmdStatePort != null) {
						childPorts.addAll(cmdStatePort);
					}
				}
			}

			if (childPorts.size() == 0) {
				return null;
			}
			return childPorts;

		}
	}

	private static class CommandStruct {
		DataflowInPort startEvent;
		List<State> startStates = new Vector<State>();
		DataflowInPort stopEvent;
		List<State> stopStates = new Vector<State>();
		DataflowInPort cancelEvent;
		List<State> cancelStates = new Vector<State>();

		public CommandStruct(DataflowInPort startEvent, DataflowInPort stopEvent, DataflowInPort cancelEvent) {
			this.startEvent = startEvent;
			this.stopEvent = stopEvent;
			this.cancelEvent = cancelEvent;
		}
	}

	@Override
	protected AbstractCommandMapperResult buildMapperResult(SoftRobotRuntime runtime, TransactionCommand command,
			NetFragment fragment, ActiveNetFragment activeFragment, DataflowOutPort outActive,
			DataflowOutPort outCancel, CommandMappingPorts ports) throws MappingException, RPIException {

		Map<Command, CommandMapperResult> childresults = new HashMap<Command, CommandMapperResult>();
		Map<Command, CommandStruct> results = new HashMap<Command, CommandStruct>();

		// map child commands
		NetFragment childHandlers = activeFragment.add(new NetFragment("Event handlers"));

		for (Command cmd : command.getCommandsInTransaction()) {
			// child command trigger ports

			if (cmd instanceof MappedCommand) {
				CommandMapperResult commandResult = ((MappedCommand) cmd).getMappingResult();
				SoftRobotCommandPorts mappingPorts = ((MappedCommand) cmd).getMappingPorts();
				commandResult.getNetFragment().add(mappingPorts);

				results.put(cmd, new CommandStruct(mappingPorts.inStart, mappingPorts.inStop, mappingPorts.inCancel));
				commandResult.getNetFragment().connect(ports.override, mappingPorts.inOverride);
				childresults.put(cmd, commandResult);

			} else {

				DataflowThroughInPort inStart = childHandlers.addInPort(new DataflowThroughInPort(new EventDataflow()));
				DataflowThroughOutPort childStart = childHandlers.addOutPort(new DataflowThroughOutPort(true, inStart));
				DataflowThroughInPort inStop = childHandlers.addInPort(new DataflowThroughInPort(new EventDataflow()));
				DataflowThroughOutPort childStop = childHandlers.addOutPort(new DataflowThroughOutPort(true, inStop));
				DataflowThroughInPort inCancel = childHandlers
						.addInPort(new DataflowThroughInPort(new StateDataflow()));
				DataflowThroughOutPort childCancel = childHandlers
						.addOutPort(new DataflowThroughOutPort(true, inCancel));

				// map the command
				CommandMapperResult commandResult = runtime.getMapperRegistry().mapCommand(runtime, cmd, childStart,
						childStop, childCancel, ports.override);
				results.put(cmd, new CommandStruct(inStart, inStop, inCancel));
				childresults.put(cmd, commandResult);
			}

		}

		// map start command conditions
		SensorMappingContext context = new SensorMappingContext();
		Map<Command, DataflowOutPort> startConditions = new HashMap<Command, DataflowOutPort>();
		for (ConditionalCommand cmd : command.getStartCommands()) {
			if (cmd.getCondition() != null) {
				SensorMapperResult<Boolean> conditionSensor = runtime.getMapperRegistry().mapSensor(runtime,
						cmd.getCondition(), activeFragment, context);
				DataflowThroughInPort source = activeFragment.addInPort(new DataflowThroughInPort(new StateDataflow()));
				DataflowThroughOutPort state = activeFragment
						.addOutPort(new DataflowThroughOutPort(false, source, new EventDataflow()));
				activeFragment.connect(conditionSensor.getSensorPort(), source);

				startConditions.put(cmd.getCommand(), state);
			} else {
				startConditions.put(cmd.getCommand(), null);
			}
		}

		// handle event handlers for child commands
		for (EventHandler eh : command.getEventHandlers()) {
			if (eh.getEffect() instanceof TargetedEventHandler
					&& ((TargetedEventHandler) eh.getEffect()).getTarget() == command) {
				continue;
			}
			if (eh.getEffect() instanceof CommandStarter) {
				results.get(((CommandStarter) eh.getEffect()).getTarget()).startStates.add(eh.getState());
			} else if (eh.getEffect() instanceof CommandStopper) {
				results.get(((CommandStopper) eh.getEffect()).getTarget()).stopStates.add(eh.getState());
			} else if (eh.getEffect() instanceof CommandCanceller) {
				results.get(((CommandCanceller) eh.getEffect()).getTarget()).cancelStates.add(eh.getState());
			}
		}

		// build result
		Result result = new Result(fragment, activeFragment, command, childresults, outCancel, outActive);

		NetFragment childrenFragment = activeFragment.add(new NetFragment("Child commands"));
		// connect control ports for children
		for (Map.Entry<Command, CommandStruct> cmdentry : results.entrySet()) {
			Command cmd = cmdentry.getKey();
			CommandStruct cmdresult = cmdentry.getValue();
			NetFragment childFragment = childrenFragment.add(new NetFragment(cmd.getName()));
			childFragment.add(childresults.get(cmd).getNetFragment());

			// start
			List<DataflowOutPort> startPorts = new Vector<DataflowOutPort>();

			if (startConditions.containsKey(cmd)) {
				if (startConditions.get(cmd) == null) {
					startPorts.add(ports.start); // first -> not delayed
				} else {
					startPorts.add(childFragment
							.add(new AndFragment(new EventDataflow(), ports.start, startConditions.get(cmd)))
							.getAndOut());
				}
			}
			for (State s : cmdresult.startStates) {
				List<DataflowOutPort> startStatePorts = result.getStatePort(s);

				if (startStatePorts != null) {
					DataflowOutPort startStatePort = childFragment
							.add(new OrFragment(new StateDataflow(), startStatePorts)).getOrOut();

					startPorts.add(
							childFragment.add(new RisingEdgeDetectionFragment(startStatePort, true)).getEventPort()); // delayed
				}
			}
			childFragment.connect(childFragment.add(new OrFragment(new EventDataflow(), startPorts)).getOrOut(),
					cmdresult.startEvent);

			// stop
			List<DataflowOutPort> stopPorts = new Vector<DataflowOutPort>();
			for (State s : cmdresult.stopStates) {
				List<DataflowOutPort> stopStatePorts = result.getStatePort(s);
				if (stopStatePorts != null) {
					DataflowOutPort stopStatePort = childFragment
							.add(new OrFragment(new StateDataflow(), stopStatePorts)).getOrOut();

					stopPorts.add(
							childFragment.add(new RisingEdgeDetectionFragment(stopStatePort, true)).getEventPort());// delayed
				}
			}
			childFragment.connect(childFragment.add(new OrFragment(new EventDataflow(), stopPorts)).getOrOut(),
					cmdresult.stopEvent);

			// cancel
			List<DataflowOutPort> cancelPorts = new Vector<DataflowOutPort>();
			for (State s : cmdresult.cancelStates) {
				List<DataflowOutPort> cancelStatePorts = result.getStatePort(s);
				if (cancelStatePorts != null) {
					DataflowOutPort cancelStatePort = childFragment
							.add(new OrFragment(new StateDataflow(), cancelStatePorts)).getOrOut();
					cancelPorts
							.add(childFragment.add(new DelayedStateActivationFragment(cancelStatePort)).getStatePort());
				}
			}
			childFragment.connect(childFragment.add(new OrFragment(new StateDataflow(), cancelPorts)).getOrOut(),
					cmdresult.cancelEvent);

		}
		return result;
	}

	@Override
	public CommandMapperResult map(SoftRobotRuntime runtime, TransactionCommand command, CommandMappingPorts ports)
			throws MappingException, RPIException {
		return super.map(runtime, command, ports);
	}

}
