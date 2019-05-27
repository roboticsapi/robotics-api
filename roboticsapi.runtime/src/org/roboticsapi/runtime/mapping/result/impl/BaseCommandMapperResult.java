/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.SensorState;
import org.roboticsapi.core.State;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.StateRaiser;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.AliasState;
import org.roboticsapi.core.state.AndState;
import org.roboticsapi.core.state.CommandState;
import org.roboticsapi.core.state.ExceptionState;
import org.roboticsapi.core.state.ExplicitState;
import org.roboticsapi.core.state.FalseState;
import org.roboticsapi.core.state.LongState;
import org.roboticsapi.core.state.NotState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.core.state.ScopedState;
import org.roboticsapi.core.state.TrueState;
import org.roboticsapi.runtime.core.primitives.BooleanAnd;
import org.roboticsapi.runtime.core.primitives.BooleanNot;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.Trigger;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.OrFragment;
import org.roboticsapi.runtime.mapping.parts.SignalTimerFragment;
import org.roboticsapi.runtime.mapping.parts.TrueFragment;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class BaseCommandMapperResult extends BaseMapperResult implements CommandMapperResult {
	private final Command command;
	protected final NetFragment stateFragment;

	public BaseCommandMapperResult(NetFragment fragment, ActiveNetFragment activeFragment, final Command command,
			DataflowOutPort activeState, DataflowOutPort cancelState) {
		super(fragment);
		stateFragment = activeFragment.add(new NetFragment("States"));
		this.command = command;

		addStatePort(FalseState.class, null);
		addStatePort(Command.ActiveState.class, activeState);
		addStatePort(Command.CancelState.class, cancelState);

		addStatePortFactory(TrueState.class, new TrueStatePortFactory());
		addStatePortFactory(ExceptionState.class, new ExceptionStatePortFactory());
		addStatePortFactory(AliasState.class, new AliasStatePortFactory());
		addStatePortFactory(ScopedState.class, new ScopedStatePortFactory());
		addStatePortFactory(AndState.class, new AndStatePortFactory());
		addStatePortFactory(ExplicitState.class, new ExplicitStatePortFactory());
		addStatePortFactory(LongState.class, new LongStatePortFactory());
		addStatePortFactory(NotState.class, new NotStatePortFactory());
		addStatePortFactory(OrState.class, new OrStatePortFactory());
		addStatePortFactory(SensorState.class, new SensorStatePortFactory());

		addExceptionPortFactory(CommandRealtimeException.class, new ThrownExceptionPortFactory(command));

	}

	@Override
	public Command getCommand() {
		return command;
	}

	@Override
	public NetFragment getNetFragment() {
		return super.getNetFragment();
	}

	@Override
	public List<DataflowOutPort> getExceptionPort(CommandRealtimeException exception) throws MappingException {

		if (command.getExceptions().contains(exception)) {
			return getStatePort(State.fromException(exception));
		} else {
			return null;
		}
	}

	// private final HashMap<State, List<DataflowOutPort>> cachedStateResults =
	// new HashMap<State, List<DataflowOutPort>>();

	@Override
	protected List<DataflowOutPort> getStatePortInternal(State state) throws MappingException {
		// command and composed events with another context are not handled
		// here...
		if (state instanceof CommandState && ((CommandState) state).getCommand() != getCommand()
				&& ((CommandState) state).getCommand() != null) {
			return null;
		}

		List<DataflowOutPort> ports = new ArrayList<DataflowOutPort>();

		ports.addAll(super.getStatePortInternal(state));

		// find other possible causes (event raisers)
		for (EventHandler eh : command.getEventHandlers()) {
			if (eh.getEffect() instanceof StateRaiser && ((StateRaiser) eh.getEffect()).getRaisedState().equals(state)
					&& !eh.getState().equals(state)) {

				List<DataflowOutPort> port = super.getStatePortInternal(eh.getState());
				if (port != null) {
					ports.addAll(port);
				}
			}
		}

		return ports;
	}

	private final class ThrownExceptionPortFactory extends AbstractExceptionPortFactory<CommandRealtimeException> {
		private final Command command;

		private ThrownExceptionPortFactory(Command command) {
			this.command = command;
		}

		@Override
		public List<DataflowOutPort> createTypedExceptionPort(CommandRealtimeException exception)
				throws MappingException {

			OrState or = new OrState();

			for (EventHandler e : command.getEventHandlers()) {
				if (e.getEffect() instanceof ExceptionThrower) {
					if (((ExceptionThrower) e.getEffect()).getThrownException().equals(exception)) {

						or.addState(e.getState());

					}
				}
			}
			return getStatePort(or);

		}
	}

	private final class ExplicitStatePortFactory extends AbstractStateSinglePortFactory<ExplicitState> {
		@Override
		public DataflowOutPort createTypedStateSinglePort(ExplicitState state) throws MappingException {
			List<DataflowOutPort> activationStatePorts = getStatePort(state.getActivatingState());

			if (activationStatePorts == null) {
				// state is never activated, we return static false
				return null;
			}

			// otherwise, we map the ExplicitState by using a Trigger
			final NetFragment ret = new NetFragment(state.toString());
			stateFragment.add(ret);

			DataflowOutPort activationStatePort = ret.add(new OrFragment(new StateDataflow(), activationStatePorts))
					.getOrOut();

			final Trigger trig = ret.add(new Trigger());

			final DataflowInPort inActivation = ret.addInPort(new StateDataflow(), true, trig.getInOn());

			ret.connect(activationStatePort, inActivation);

			final DataflowInPort inDeactivation = ret.addInPort(new StateDataflow(), false, trig.getInOff());
			List<DataflowOutPort> deactivationStatePorts = getStatePort(state.getDeactivatingState());

			if (deactivationStatePorts != null) {
				ret.connect(ret.add(new OrFragment(new StateDataflow(), deactivationStatePorts)).getOrOut(),
						inDeactivation);
			}

			return ret.addOutPort(new StateDataflow(), false, trig.getOutActive());
		}
	}

	private final class TrueStatePortFactory extends AbstractStateSinglePortFactory<TrueState> {

		@Override
		public DataflowOutPort createTypedStateSinglePort(TrueState state) throws MappingException {
			return stateFragment.add(new TrueFragment(state.toString())).getTrueOut();
		}
	}

	private final class AndStatePortFactory extends AbstractStateSinglePortFactory<AndState> {
		@Override
		public DataflowOutPort createTypedStateSinglePort(AndState state) throws MappingException {
			final NetFragment ret = stateFragment.add(new NetFragment(state.toString()));
			final List<State> events = state.getStates();
			List<DataflowOutPort> eventPorts = new ArrayList<DataflowOutPort>();
			for (int i = 0; i < events.size(); i++) {
				List<DataflowOutPort> port = getStatePort(events.get(i));
				if (port == null) {
					return null; // and state will never occur
				}
				eventPorts.add(stateFragment.add(new OrFragment(new StateDataflow(), port)).getOrOut());
			}

			// zero events? -> true
			if (eventPorts.size() == 0) {
				BooleanValue value = ret.add(new BooleanValue(true));
				return ret.addOutPort(new StateDataflow(), false, value.getOutValue());
			}

			// just one event? then map it
			if (eventPorts.size() == 1) {
				return eventPorts.get(0);
			}

			DataflowInPort inFirst, inSecond = null;
			DataflowOutPort out = null;
			DataflowOutPort result = null;

			for (int i = 0; i < eventPorts.size(); i++) {
				// child event
				final DataflowOutPort eventOut = eventPorts.get(i);

				if (i == eventPorts.size() - 1) {
					ret.connect(eventOut, inSecond);
				} else {
					// boolean and
					final BooleanAnd and = ret.add(new BooleanAnd(false, false));
					out = ret.addOutPort(new StateDataflow(), true, and.getOutValue());
					if (inSecond != null) {
						ret.connect(out, inSecond);
					} else {
						result = out;
					}

					inFirst = ret.addInPort(new StateDataflow(), true, and.getInFirst());
					inSecond = ret.addInPort(new StateDataflow(), false, and.getInSecond());

					// connect it all
					ret.connect(eventOut, inFirst);
				}
			}

			return result;
		}
	}

	private final class ScopedStatePortFactory extends AbstractStatePortFactory<ScopedState> {
		@Override
		public List<DataflowOutPort> createTypedStatePort(ScopedState state) throws MappingException {
			return getStatePort(state.getOther());
		}
	}

	private final class AliasStatePortFactory extends AbstractStatePortFactory<AliasState> {
		@Override
		public List<DataflowOutPort> createTypedStatePort(AliasState state) throws MappingException {
			return getStatePort(state.getOther());
		}
	}

	private final class ExceptionStatePortFactory extends AbstractStatePortFactory<ExceptionState> {
		@Override
		public List<DataflowOutPort> createTypedStatePort(ExceptionState state) throws MappingException {
			return BaseCommandMapperResult.super.getExceptionPort(state.getException());
		}
	}

	private final class LongStatePortFactory extends AbstractStateSinglePortFactory<LongState> {
		@Override
		public DataflowOutPort createTypedStateSinglePort(LongState state) throws MappingException {
			List<DataflowOutPort> otherStatePorts = getStatePort(state.getOther());

			if (otherStatePorts == null) {
				// state is never activated, we return static false
				return null;
			}

			final NetFragment ret = new NetFragment(state.toString());
			stateFragment.add(ret);

			DataflowOutPort otherStatePort = ret.add(new OrFragment(new StateDataflow(), otherStatePorts)).getOrOut();

			SignalTimerFragment signalTimerFragment = new SignalTimerFragment(otherStatePort, otherStatePort);
			stateFragment.add(signalTimerFragment);
			DataflowOutPort millisecondsActivePort = signalTimerFragment.getSignalTimePort();

			DoubleGreater greater = ret.add(new DoubleGreater());
			greater.setSecond(state.getSeconds() * 1000);
			ret.connect(millisecondsActivePort, ret.addInPort(new DoubleDataflow(), true, greater.getInFirst()));
			OutPort activator = greater.getOutValue();

			Trigger trigger = ret.add(new Trigger());
			ret.connect(otherStatePort, ret.addInPort(new StateDataflow(), true, trigger.getInActive()));
			trigger.setOff(false);
			try {
				trigger.getInOn().connectTo(activator);
			} catch (RPIException e) {
				throw new MappingException(e);
			}

			return ret.addOutPort(new StateDataflow(), true, trigger.getOutActive());
		}
	}

	private final class NotStatePortFactory extends AbstractStateSinglePortFactory<NotState> {
		@Override
		public DataflowOutPort createTypedStateSinglePort(NotState state) throws MappingException {
			List<DataflowOutPort> statePorts = getStatePort(state.getState());

			if (statePorts == null) {
				// original State never occurs -> NotState is always true (when
				// active)
				return stateFragment.add(new TrueFragment(state.toString())).getTrueOut();
			}

			final NetFragment ret = new NetFragment(state.toString());
			stateFragment.add(ret);

			DataflowOutPort statePort = ret.add(new OrFragment(new StateDataflow(), statePorts)).getOrOut();

			final BooleanNot not = ret.add(new BooleanNot());
			final DataflowInPort inValue = ret.addInPort(new StateDataflow(), true, not.getInValue());

			ret.connect(statePort, inValue);
			return ret.addOutPort(new StateDataflow(), false, not.getOutValue());
		}
	}

	private final class OrStatePortFactory extends AbstractStatePortFactory<OrState> {
		@Override
		public List<DataflowOutPort> createTypedStatePort(OrState state) throws MappingException {
			List<DataflowOutPort> ret = new ArrayList<DataflowOutPort>();
			final List<State> events = state.getStates();

			for (int i = 0; i < events.size(); i++) {
				List<DataflowOutPort> ports = getStatePort(events.get(i));

				if (ports != null) {
					ret.addAll(ports);
				}
			}
			return ret;
		}
	}

	private final class SensorStatePortFactory extends AbstractStatePortFactory<SensorState> {
		@Override
		public List<DataflowOutPort> createTypedStatePort(SensorState state) throws MappingException {
			AbstractMapperRuntime runtime = (AbstractMapperRuntime) command.getRuntime();

			SensorMapperResult<Boolean> sensorResult = runtime.getMapperRegistry().mapSensor(runtime, state.getSensor(),
					stateFragment, null);

			return sensorResult.getStatePort(state);
		}
	}
}
