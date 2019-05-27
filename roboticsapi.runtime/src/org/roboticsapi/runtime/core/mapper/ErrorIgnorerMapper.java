/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.EventEffectMapper;
import org.roboticsapi.runtime.mapping.parts.EventHandlerMappingPorts;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.EventHandlerMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.SimpleEventHandlerMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class ErrorIgnorerMapper implements EventEffectMapper<SoftRobotRuntime, ExceptionIgnorer> {
	static int cnt = 0;

	@Override
	public EventHandlerMapperResult map(final SoftRobotRuntime runtime, DataflowOutPort event,
			final ExceptionIgnorer eventEffect, CommandMapperResult context, EventHandlerMappingPorts ports)
			throws MappingException, RPIException {
		final NetFragment ret = new NetFragment("ErrorIgnorer");
		return new SimpleEventHandlerMapperResult(ret);
	}
}
