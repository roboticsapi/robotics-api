/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.actuator.GeneralActuatorException;

/**
 * Abstract implementation for a {@link ActuatorDriver} targeting the RCC
 * reference implementation.
 */
public abstract class AbstractSoftRobotActuatorDriver extends AbstractSoftRobotDeviceDriver implements ActuatorDriver {

	/**
	 * Default constructor.
	 */
	public AbstractSoftRobotActuatorDriver() {
		super();
	}

	/**
	 * Convenience constructor.
	 * 
	 * @param runtime    the driver's runtime.
	 * @param deviceName the driver's device name at the RCC.
	 */
	public AbstractSoftRobotActuatorDriver(SoftRobotRuntime runtime, String deviceName) {
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
		exceptions.add(new GeneralActuatorException(this));

		return exceptions;
	}

	@Override
	public final <T extends ActuatorDriverRealtimeException> T defineActuatorDriverException(Class<T> type)
			throws CommandException {
		for (ActuatorDriverRealtimeException ex : defineActuatorDriverExceptions()) {
			if (type.isAssignableFrom(ex.getClass())) {
				return type.cast(ex);
			}
		}
		throw new CommandException("Found no actuator exception of the given type: " + type.getName());
	}

}
