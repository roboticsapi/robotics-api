/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;

/**
 * Abstract implementation for a digital/analog output.
 *
 * @param <ID> type of the driver
 * @param <T>  type of the input
 */
public abstract class Output<OD extends OutputDriver<T>, T> extends AbstractActuator<OD> {

	@Override
	public final void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	protected final void defineMaximumParameters() throws InvalidParametersException {
	}

	public final T getValue() throws RealtimeValueReadException {
		return getSensor().getCurrentValue();
	}

	public abstract RealtimeValue<T> getSensor();

	public RealtimeValue<T> getSensor(RoboticsRuntime runtime) {
		return getDriver(runtime).getSensor();
	}

	public static class OutputException extends ActuatorDriverRealtimeException {
		private static final long serialVersionUID = 1L;

		public OutputException(OutputDriver<?> actuatorDriver) {
			super(actuatorDriver);
		}
	}

	public static class CommunicationException extends OutputException {
		private static final long serialVersionUID = 1L;

		public CommunicationException(OutputDriver<?> actuatorDriver) {
			super(actuatorDriver);
		}

	}

	public static class ValueOutOfRangeException extends OutputException {
		private static final long serialVersionUID = 1L;

		public ValueOutOfRangeException(OutputDriver<?> actuatorDriver) {
			super(actuatorDriver);
		}
	}

}
