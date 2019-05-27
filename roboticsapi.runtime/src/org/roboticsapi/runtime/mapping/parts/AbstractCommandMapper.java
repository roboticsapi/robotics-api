/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Command.WatchdogTimeoutException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.core.netcomm.ReadDoubleFromNet;
import org.roboticsapi.runtime.core.netcomm.WriteDoubleToNet;
import org.roboticsapi.runtime.core.primitives.BooleanNot;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleEquals;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.result.impl.BaseCommandMapperResult;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class AbstractCommandMapper<R extends RoboticsRuntime, C extends Command>
		implements CommandMapper<R, C> {

	private static int netcommId = 0;

	protected void mapWatchdog(final AbstractCommandMapperResult result) throws RPIException, MappingException {

		if (result.getCommand().needsWatchdog()) {
			// if a watchdog is really needed, we create one

			NetFragment fragment = new NetFragment("Watchdog");
			BooleanNot not = fragment.add(new BooleanNot());

			final WriteDoubleToNet reflectedValueIn = fragment.add(new WriteDoubleToNet("WatchdogIn" + netcommId, 0d));
			ReadDoubleFromNet valueOut = fragment.add(new ReadDoubleFromNet("WatchdogOut" + netcommId++));
			Clock clock = fragment.add(new Clock(1.0));
			DoubleEquals compare = fragment.add(new DoubleEquals(result.getCommand().getWatchdogTimeout()));

			List<DataflowOutPort> activationPorts = result.getStatePort(result.getCommand().getActiveState());

			DataflowOutPort activationPort = fragment.add(new OrFragment(new StateDataflow(), activationPorts))
					.getOrOut();

			// fragment.connect(
			// activationPort,
			// fragment.addInPort(new StateDataflow(), true,
			// valueOut.getInActive()));
			fragment.connect(activationPort, fragment.addInPort(new StateDataflow(), true, clock.getInActive()));

			compare.getInFirst().connectTo(clock.getOutValue());
			compare.getInSecond().connectTo(reflectedValueIn.getOutValue());
			not.getInValue().connectTo(compare.getOutValue());
			valueOut.getInValue().connectTo(clock.getOutValue());

			valueOut.getNetcomm().addNetcommListener(new NetcommListenerAdapter() {
				@Override
				public void valueChanged(NetcommValue value) {
					reflectedValueIn.getNetcomm().setString(value.getString());
				}
			});
			result.getNetFragment().add(fragment);
			result.setWatchdogErrorStatePort(fragment.addOutPort(new StateDataflow(), false, not.getOutValue()));
		} else {
			// otherwise, the error shall never occur
			result.setWatchdogErrorStatePort(null);
		}
	}

	public static abstract class AbstractCommandMapperResult extends BaseCommandMapperResult {

		public AbstractCommandMapperResult(NetFragment fragment, ActiveNetFragment activeFragment, Command command,
				DataflowOutPort activeState, DataflowOutPort cancelState) {
			super(fragment, activeFragment, command, activeState, cancelState);
		}

		public void setWatchdogErrorStatePort(DataflowOutPort watchdogErrorState) {
			addExceptionPort(WatchdogTimeoutException.class, watchdogErrorState);
		}
	}
}