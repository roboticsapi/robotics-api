/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActuatorRealtimeBoolean;

public abstract class AbstractActuator<AD extends ActuatorDriver> extends AbstractDevice<AD> implements Actuator {

	/** Default device parameters for this device */
	private DeviceParameterBag maximumParameters = new DeviceParameterBag();
	private DeviceParameterBag defaultParameters = new DeviceParameterBag();

	public AbstractActuator() {
	}

	@Override
	public void checkParameterBounds(DeviceParameterBag parameters) throws InvalidParametersException {
		if (parameters == null) {
			throw new IllegalArgumentException("parameters may not be null");
		}
		for (DeviceParameters p : parameters.getAll()) {
			checkParameterBounds(p);
		}
	}

	@Override
	public void checkParameterBounds(DeviceParameters... parameters) throws InvalidParametersException {
		for (DeviceParameters p : parameters) {
			checkParameterBounds(p);
		}
	}

	@Override
	public void checkParameterBounds(DeviceParameters parameters) throws InvalidParametersException {
		if (parameters == null) {
			throw new IllegalArgumentException("parameters may not be null");
		}

		DeviceParameters max = maximumParameters.get(parameters.getClass());

		if (max != null && !parameters.respectsBounds(max)) {
			throw new InvalidParametersException("Given DeviceParameters of type "
					+ parameters.getClass().getSimpleName() + " exceed maximum values of this Actuator");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#validateParameters(org.roboticsapi.core
	 * .actuator.DeviceParameters)
	 */
	@Override
	public abstract void validateParameters(DeviceParameters parameters) throws InvalidParametersException;

	@Override
	protected void afterInitialization() {
		super.afterInitialization();
		defineMaximumParameters();
		defineDefaultParameters();
	}

	protected abstract void defineMaximumParameters() throws InvalidParametersException;

	protected void defineDefaultParameters() throws InvalidParametersException {
		// empty default implementation
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#addDefaultParameters(org.roboticsapi.
	 * core.actuator.DeviceParameters)
	 */
	@Override
	public void addDefaultParameters(final DeviceParameters newParameters) throws InvalidParametersException {
		checkParameterBounds(newParameters);
		validateParameters(newParameters);
		this.defaultParameters = this.defaultParameters.withParameters(newParameters);
	}

	protected void addMaximumParameters(final DeviceParameters newParameters) {
		this.maximumParameters = this.maximumParameters.withParameters(newParameters);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#getDefaultParameters()
	 */
	@Override
	public DeviceParameterBag getDefaultParameters() {
		// TODO maybe inefficient, perhaps determine defaultParameters
		// incrementally ?
		return maximumParameters.withParameters(defaultParameters);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#getCompletedState()
	 */
	@Override
	public final ActuatorRealtimeBoolean getCompletedState(Command scope) {
		return new CompletedRealtimeBoolean(scope, this);
	}
}
