/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActuatorRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class ActuatorStateAliasFactory<V extends ActuatorRealtimeBoolean>
		extends TypedRealtimeValueAliasFactory<Boolean, V> {
	private RealtimeBoolean alias;
	private Actuator actuator;

	protected ActuatorStateAliasFactory(Class<V> type, Actuator actuator, RealtimeBoolean alias) {
		super(type);
		this.actuator = actuator;
		this.alias = alias;
	}

	@Override
	protected RealtimeValue<Boolean> createAlias(V value) {
		if (value.getActuator() == actuator)
			return alias;
		return null;
	}

}
