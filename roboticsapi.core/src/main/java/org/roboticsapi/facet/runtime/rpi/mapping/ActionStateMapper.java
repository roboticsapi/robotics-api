/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class ActionStateMapper<V extends ActionRealtimeBoolean> extends TypedRealtimeValueAliasFactory<Boolean, V> {
	private RealtimeBoolean alias;
	private Action action;

	protected ActionStateMapper(Class<V> type, Action action, RealtimeBoolean alias) {
		super(type);
		this.action = action;
		this.alias = alias;
	}

	@Override
	protected RealtimeValue<Boolean> createAlias(V value) {
		if (value.getAction() == action)
			return alias;
		return null;
	}

}
