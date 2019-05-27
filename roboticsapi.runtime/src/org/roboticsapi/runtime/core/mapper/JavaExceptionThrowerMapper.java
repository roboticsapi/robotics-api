/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.eventhandler.JavaExceptionThrower;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.ReadBoolFromNet;
import org.roboticsapi.runtime.core.primitives.Trigger;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.EventEffectMapper;
import org.roboticsapi.runtime.mapping.parts.EventHandlerMappingPorts;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.EventHandlerMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.SimpleEventHandlerMapperResult;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public class JavaExceptionThrowerMapper implements EventEffectMapper<SoftRobotRuntime, JavaExceptionThrower> {
	static int cnt = 0;

	@Override
	public EventHandlerMapperResult map(SoftRobotRuntime runtime, DataflowOutPort event,
			final JavaExceptionThrower eventEffect, CommandMapperResult context, EventHandlerMappingPorts ports)
			throws MappingException, RPIException {
		// add a netcomm net
		final NetFragment ret = new NetFragment("ExceptionThrower");

		final Trigger trig = ret.add(new Trigger(false, false));
		ret.connect(event, ret.addInPort(new EventDataflow(), true, trig.getInOn()));

		final ReadBoolFromNet out = ret.add(new ReadBoolFromNet("ExceptionThrower" + (cnt++)));
		out.getInValue().connectTo(trig.getOutActive());
		out.getNetcomm().addNetcommListener(new NetcommListenerAdapter() {
			@Override
			public void valueChanged(final NetcommValue value) {
				if (value.getString().equals("true")) {
					eventEffect.getContext().handleException(eventEffect.getException());
				}
			}
		});
		return new SimpleEventHandlerMapperResult(ret);
	}
}
