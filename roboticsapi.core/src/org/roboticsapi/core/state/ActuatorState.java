/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.State;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * An event triggered by a device
 */
public abstract class ActuatorState extends State {
	/** The device triggering the event */
	protected Actuator actuator;

	public ActuatorState setDevice(final Actuator device) {
		this.actuator = device;
		return this;
	}

	public Actuator getDevice() {
		return actuator;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && (actuator == null || ((ActuatorState) obj).actuator == null
				|| actuator == ((ActuatorState) obj).actuator);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), actuator);
	}

	@Override
	public boolean subclassEquals(State other) {
		return super.subclassEquals(other) && ((ActuatorState) other).actuator == actuator;
	}

	@Override
	public String toString() {
		return super.toString() + "<" + actuator + ">";
	}
}
