/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * A RealtimeBoolean depending on an Actuator.
 */
public abstract class ActuatorRealtimeBoolean extends RealtimeBoolean {
	/** The device triggering the event */
	protected final Actuator actuator;

	public ActuatorRealtimeBoolean(Command scope, Actuator actuator) {
		super(scope);
		this.actuator = actuator;
	}

	public Actuator getActuator() {
		return actuator;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && actuator == ((ActuatorRealtimeBoolean) obj).actuator;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), actuator);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + actuator + ">";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
