/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.List;

import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.actuator.OverrideParameter.Scaling;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.CommandMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.AbstractExceptionPortFactory;
import org.roboticsapi.runtime.mapping.result.impl.AbstractStatePortFactory;
import org.roboticsapi.runtime.rpi.RPIException;

public class RuntimeCommandMapper extends SoftRobotCommandMapper<SoftRobotRuntime, RuntimeCommand> {

	public static class Result extends AbstractCommandMapperResult {

		private Result(final RuntimeCommand command, NetFragment netFragment, ActiveNetFragment activeFragment,
				final ActionMapperResult actionResult, final ActuatorDriverMapperResult deviceResult,
				DataflowOutPort activeState, DataflowOutPort cancelState) {
			super(netFragment, activeFragment, command, activeState, cancelState);

			addStatePortFactory(ActionState.class, new AbstractStatePortFactory<ActionState>() {

				@Override
				public List<DataflowOutPort> createTypedStatePort(ActionState state) throws MappingException {
					return actionResult.getStatePort(state);
				}
			});

			addStatePortFactory(ActuatorState.class, new AbstractStatePortFactory<ActuatorState>() {

				@Override
				public List<DataflowOutPort> createTypedStatePort(ActuatorState state) throws MappingException {
					if (command.getDevice().equals(state.getDevice())) {
						return deviceResult.getStatePort(state);
					} else {
						return null;
					}

				}
			});

			addExceptionPortFactory(ActionRealtimeException.class,
					new AbstractExceptionPortFactory<ActionRealtimeException>() {

						@Override
						public List<DataflowOutPort> createTypedExceptionPort(ActionRealtimeException exception)
								throws MappingException {
							return actionResult.getExceptionPort(exception);
						}
					});

			addExceptionPortFactory(ActuatorDriverRealtimeException.class,
					new AbstractExceptionPortFactory<ActuatorDriverRealtimeException>() {

						@Override
						public List<DataflowOutPort> createTypedExceptionPort(ActuatorDriverRealtimeException exception)
								throws MappingException {
							return deviceResult.getExceptionPort(exception);
						}
					});
		}
	}

	@Override
	protected Result buildMapperResult(SoftRobotRuntime runtime, RuntimeCommand command, NetFragment fragment,
			ActiveNetFragment activeFragment, DataflowOutPort outActive, DataflowOutPort outCancel,
			CommandMappingPorts ports) throws MappingException, RPIException {

		// override scaling
		OverrideParameter op = command.getDeviceParameters().get(OverrideParameter.class);
		DataflowOutPort override = ports.override;
		if (op != null) {
			SensorMapperResult<Double> ovResult = runtime.getMapperRegistry().mapSensor(runtime, op.getOverride(),
					fragment, null);
			if (op.getScaling() == Scaling.ABSOLUTE) {
				override = ovResult.getSensorPort();
			} else {
				DoubleMultiply mult = fragment.add(new DoubleMultiply());
				fragment.connect(ports.override, mult.getInFirst(), new DoubleDataflow());
				fragment.connect(ovResult.getSensorPort(), mult.getInSecond(), new DoubleDataflow());
				override = fragment.addOutPort(new DoubleDataflow(), false, mult.getOutValue());
			}
		}

		// map action and device
		ActionMapperResult actionResult = runtime.getMapperRegistry().mapAction(runtime, command.getAction(),
				command.getDeviceParameters(), outCancel, override, command.getPlans());
		ActuatorDriverMapperResult deviceResult = runtime.getMapperRegistry().mapActuatorDriver(runtime,
				command.getDevice().getDriver(), actionResult.getActionResult(), command.getDeviceParameters(),
				outCancel, override);

		activeFragment.add(actionResult.getNetFragment());
		activeFragment.add(deviceResult.getNetFragment());

		// build result
		return new Result(command, fragment, activeFragment, actionResult, deviceResult, outActive, outCancel);
	}

	@Override
	public CommandMapperResult map(SoftRobotRuntime runtime, RuntimeCommand command, CommandMappingPorts ports)
			throws MappingException, RPIException {
		return super.map(runtime, command, ports);
	}
}
