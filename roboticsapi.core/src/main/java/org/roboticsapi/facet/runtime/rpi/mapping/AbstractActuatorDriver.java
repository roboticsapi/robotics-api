/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;

/**
 * Abstract implementation for a {@link ActuatorDriver} targeting the RCC
 * reference implementation.
 */
public abstract class AbstractActuatorDriver<A extends Actuator> extends AbstractDeviceDriver<A>
		implements ActuatorDriver, NamedActuatorDriver<A> {

	/**
	 * Default constructor.
	 */
	protected AbstractActuatorDriver() {
		super();
	}

	/**
	 * Convenience constructor.
	 *
	 * @param runtime    the driver's runtime.
	 * @param deviceName the driver's device name at the RCC.
	 */
	protected AbstractActuatorDriver(RpiRuntime runtime, String deviceName) {
		super(runtime, deviceName);
	}

	@Override
	public final List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		List<Class<? extends ActuatorDriverRealtimeException>> types = new ArrayList<Class<? extends ActuatorDriverRealtimeException>>();

		for (ActuatorDriverRealtimeException e : defineActuatorDriverExceptions()) {
			types.add(e.getClass());
		}

		return types;
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		ArrayList<ActuatorDriverRealtimeException> exceptions = new ArrayList<ActuatorDriverRealtimeException>();
		exceptions.add(new ConcurrentAccessException(this));
		exceptions.add(new ActuatorNotOperationalException(this));

		return exceptions;
	}

}
