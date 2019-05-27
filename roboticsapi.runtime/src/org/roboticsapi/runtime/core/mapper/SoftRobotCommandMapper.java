/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.State;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.StateRaiser;
import org.roboticsapi.core.sensor.Assignment;
import org.roboticsapi.core.sensor.Observer;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.Trigger;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughInPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.AbstractCommandMapper;
import org.roboticsapi.runtime.mapping.parts.CommandMappingPorts;
import org.roboticsapi.runtime.mapping.parts.DelayedStateActivationFragment;
import org.roboticsapi.runtime.mapping.parts.OrFragment;
import org.roboticsapi.runtime.mapping.parts.RisingEdgeDetectionFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.MapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class SoftRobotCommandMapper<R extends SoftRobotRuntime, C extends Command>
		extends AbstractCommandMapper<R, C> {

	@SuppressWarnings({ "unchecked" })
	@Override
	public CommandMapperResult map(R runtime, C command, CommandMappingPorts ports)
			throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("Command: " + command.getName());

		// Activation part
		final NetFragment activation = fragment.add(new NetFragment("Activation"));

		// Active trigger
		DataflowThroughInPort inStop = activation.addInPort(new DataflowThroughInPort(new EventDataflow()));
		DataflowThroughOutPort outStop = activation.addOutPort(new DataflowThroughOutPort(false, inStop));
		final Trigger triggerActive = activation.add(new Trigger());
		activation.connect(ports.start, activation.addInPort(new EventDataflow(), true, triggerActive.getInOn()));
		activation.connect(outStop, activation.addInPort(new EventDataflow(), true, triggerActive.getInOff()));
		DataflowOutPort activePort = activation.addOutPort(new StateDataflow(), false, triggerActive.getOutActive());

		triggerActive.getInOn().setDebug(10);
		triggerActive.getInOff().setDebug(10);

		// Active part & inner stuff
		final ActiveNetFragment activeFragment = fragment.add(new ActiveNetFragment("Active part", activePort));

		// Cancel trigger
		DataflowThroughInPort inCancel = activeFragment.addInPort(new DataflowThroughInPort(new StateDataflow()));
		DataflowThroughOutPort outCancel = activeFragment.addOutPort(new DataflowThroughOutPort(false, inCancel));
		DataflowOutPort cancel = outCancel;

		// prepare local stop and cancel
		List<State> stopStates = new Vector<State>();
		stopStates.add(command.getDoneState());
		for (EventHandler eh : command.getEventHandlers()) {
			if (eh.getEffect() instanceof CommandStopper && ((CommandStopper) eh.getEffect()).getTarget() == command) {
				stopStates.add(eh.getState());
			}
		}

		List<State> cancelStates = new Vector<State>();
		for (EventHandler eh : command.getEventHandlers()) {
			if (eh.getEffect() instanceof CommandCanceller
					&& ((CommandCanceller) eh.getEffect()).getTarget() == command) {
				cancelStates.add(eh.getState());
			}
		}

		// build command specific mapper result
		AbstractCommandMapperResult result = buildMapperResult(runtime, command, fragment, activeFragment, activePort,
				cancel, ports);

		// map the watchdog mechanism of the Command
		mapWatchdog(result);

		// map monitors
		SensorMappingContext context = new SensorMappingContext();
		final NetFragment monitorFragment = activeFragment.add(new NetFragment("Monitors"));
		for (final Observer<?> observer : command.getObservers()) {
			@SuppressWarnings("rawtypes")
			SensorMapperResult sensor = runtime.getMapperRegistry().mapSensor(runtime, observer.getSensor(),
					monitorFragment, context);
			sensor.addListener(command, observer.getListener());
		}

		for (final Assignment<?> assign : command.getAssignments()) {
			@SuppressWarnings("rawtypes")
			SensorMapperResult sensor = runtime.getMapperRegistry().mapSensor(runtime, assign.getSource(),
					monitorFragment, context);
			sensor.assign(command, assign.getTarget());
		}

		// event handlers
		final NetFragment eventHandlers = activeFragment.add(new NetFragment("Event handlers"));

		// handle stop conditions
		final NetFragment stopFragment = eventHandlers.add(new NetFragment("Command stoppers"));
		List<DataflowOutPort> stopPorts = new Vector<DataflowOutPort>();
		stopPorts.add(ports.stop); // input -> not delayed

		for (State s : stopStates) {
			List<DataflowOutPort> statePorts = result.getStatePort(s);
			if (statePorts != null) {
				DataflowOutPort statePort = stopFragment.add(new OrFragment(new StateDataflow(), statePorts))
						.getOrOut();

				stopPorts.add(stopFragment.add(new RisingEdgeDetectionFragment(statePort, true)).getEventPort()); // delayed
			}
		}
		activation.connect(activation.add(new OrFragment(new EventDataflow(), stopPorts)).getOrOut(), inStop);

		// handle cancel conditions
		NetFragment cancelFragment = eventHandlers.add(new NetFragment("Cancel conditions"));
		List<DataflowOutPort> cancelPorts = new ArrayList<DataflowOutPort>();
		cancelPorts.add(ports.cancel);
		for (State s : cancelStates) {
			List<DataflowOutPort> innerCancelPorts = result.getStatePort(s);
			if (innerCancelPorts != null) {
				DataflowOutPort cancelPort = cancelFragment.add(new OrFragment(new StateDataflow(), innerCancelPorts))
						.getOrOut();

				cancelPorts.add(cancelFragment.add(new DelayedStateActivationFragment(cancelPort)).getStatePort());
			}
		}
		activeFragment.connect(activeFragment.add(new OrFragment(new StateDataflow(), cancelPorts)).getOrOut(),
				inCancel);

		// java event handlers
		for (EventHandler eh : command.getEventHandlers()) {
			if (eh.getEffect() instanceof CommandStarter || eh.getEffect() instanceof CommandStopper
					|| eh.getEffect() instanceof CommandCanceller || eh.getEffect() instanceof StateRaiser
					|| eh.getEffect() instanceof ExceptionThrower) {
				continue;
			}

			NetFragment eventHandlerFragment = eventHandlers
					.add(new NetFragment(eh.getEffect().getClass().getSimpleName()));
			List<DataflowOutPort> statePorts = result.getStatePort(eh.getState());

			// if no port is provided for this State, we need not map handlers
			if (statePorts == null) {
				continue;
			}

			DataflowOutPort statePort = eventHandlerFragment.add(new OrFragment(new StateDataflow(), statePorts))
					.getOrOut();

			DataflowOutPort eventPort = eventHandlerFragment.add(new RisingEdgeDetectionFragment(statePort))
					.getEventPort();
			MapperResult mappedHandler = runtime.getMapperRegistry().mapEventHandler(runtime, eventPort, eh.getEffect(),
					result);
			if (mappedHandler != null) {
				eventHandlerFragment.add(mappedHandler.getNetFragment());
			}
		}

		// resolve smart links
		fragment.buildLinks();

		// connect all required in ports
		for (DataflowInPort in : fragment.getInPorts()) {
			if (in.isRequired() && !in.isConnected()) {
				fragment.connect(null, in);
				// throw new RuntimeException("auto-connect");
			}
		}

		// resolve smart links
		fragment.buildLinks();

		return result;
	}

	protected abstract AbstractCommandMapperResult buildMapperResult(R runtime, C command, NetFragment fragment,
			ActiveNetFragment activeFragment, DataflowOutPort outActive, DataflowOutPort outCancel,
			CommandMappingPorts ports) throws MappingException, RPIException;
}
