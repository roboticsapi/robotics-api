/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.WaitCommand.WaitCompleted;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.CommandMappingPorts;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class WaitCommandMapper extends SoftRobotCommandMapper<SoftRobotRuntime, WaitCommand> {

	public static class Result extends AbstractCommandMapperResult {

		private Result(NetFragment netFragment, ActiveNetFragment activeFragment, WaitCommand command,
				DataflowOutPort activeState, DataflowOutPort cancelState, DataflowOutPort waitCompleteState) {
			super(netFragment, activeFragment, command, activeState, cancelState);

			addStatePort(WaitCompleted.class, waitCompleteState);
		}

	}

	@Override
	protected Result buildMapperResult(SoftRobotRuntime runtime, WaitCommand command, NetFragment fragment,
			ActiveNetFragment activeFragment, DataflowOutPort outActive, DataflowOutPort outCancel,
			CommandMappingPorts ports) throws MappingException, RPIException {

		// create the clock
		final NetFragment waitNet = activeFragment.add(new NetFragment("Wait"));
		final Clock clock = new Clock(1.0);
		waitNet.add(clock);
		DataflowOutPort outTimeCompleted = null;
		if (command.getDuration() >= 0) {
			final DoubleGreater cmp = new DoubleGreater();
			waitNet.add(cmp);
			cmp.setSecond(command.getDuration());
			cmp.getInFirst().connectTo(clock.getOutValue());
			outTimeCompleted = waitNet.addOutPort(new StateDataflow(), false, cmp.getOutValue());
		}
		waitNet.connect(outActive, waitNet.addInPort(new StateDataflow(), true, clock.getInActive()));

		// build result
		return new Result(fragment, activeFragment, command, outActive, outCancel, outTimeCompleted);
	}

	@Override
	public CommandMapperResult map(SoftRobotRuntime runtime, WaitCommand command, CommandMappingPorts ports)
			throws MappingException, RPIException {
		return super.map(runtime, command, ports);
	}
}
