/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.runtime.CommandJavaExecutorException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.ReadDoubleFromNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.EventEffectMapper;
import org.roboticsapi.runtime.mapping.parts.EventHandlerMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SignalCounterFragment;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.EventHandlerMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.SimpleEventHandlerMapperResult;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public class JavaExecutorMapper implements EventEffectMapper<SoftRobotRuntime, JavaExecutor> {
	static int cnt = 0;

	@Override
	public EventHandlerMapperResult map(final SoftRobotRuntime runtime, DataflowOutPort event,
			final JavaExecutor eventEffect, final CommandMapperResult context, EventHandlerMappingPorts ports)
			throws MappingException, RPIException {
		final NetFragment ret = new NetFragment("ThreadStarter");

		// add a netcomm net
		final NetFragment netcomm = new NetFragment("Link to Java");

		// final Trigger trig = netcomm.addPrimitive(new Trigger(false, false));
		// netcomm.addInPort(new TriggerDataflow(), true, trig.getInOn())
		// .connectTo(event);
		// netcomm.addInPort(new TriggerDataflow(), true, trig.getInActive())
		// .connectTo(ports.active);

		SignalCounterFragment counter = ret.add(new SignalCounterFragment(event));

		DataflowOutPort counterPort = counter.getSignalCountPort();

		final int nr = cnt++;

		final ReadDoubleFromNet out = netcomm.add(new ReadDoubleFromNet("JavaThreadStarter" + nr));

		netcomm.connect(counterPort, netcomm.addInPort(new DoubleDataflow(), true, out.getInValue()));

		out.getNetcomm().addNetcommListener(new NetcommListenerAdapter() {

			int lastValue;

			@Override
			public void valueChanged(final NetcommValue value) {

				int currentValue = (int) Double.parseDouble(value.getString());

				int eventCount = currentValue - lastValue;
				lastValue = currentValue;

				Runnable safeRunner = new Runnable() {

					@Override
					public void run() {
						try {
							eventEffect.getRunnable().run();
						} catch (Throwable exc) {
							context.getCommand().handleException(
									new CommandJavaExecutorException("Exception in JavaExecutor thread", exc));
						}

					}
				};

				for (int i = 0; i < eventCount; i++) {
					if (eventEffect.isAsync()) {
						eventEffect.getContext().getCommandHandle().startThread(safeRunner);
					} else {
						safeRunner.run();
					}
				}
			}
		});
		ret.add(netcomm);
		return new SimpleEventHandlerMapperResult(ret);
	}
}
