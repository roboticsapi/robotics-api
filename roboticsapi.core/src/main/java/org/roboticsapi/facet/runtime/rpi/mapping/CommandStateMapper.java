/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.CommandRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class CommandStateMapper<V extends CommandRealtimeBoolean> extends TypedRealtimeValueAliasFactory<Boolean, V> {
	private Command command;
	private RealtimeBoolean alias;

	public CommandStateMapper(Class<V> type, Command command, RealtimeBoolean alias) {
		super(type);
		this.command = command;
		this.alias = alias;
	}

	@Override
	protected RealtimeValue<Boolean> createAlias(V value) {
		if (value.getCommand() == command)
			return alias;
		return null;
	}

}
