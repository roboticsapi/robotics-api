/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ExceptionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class TypedActuatorExceptionMapper<T extends ActuatorDriverRealtimeException>
		extends TypedRealtimeValueAliasFactory<Boolean, ExceptionRealtimeBoolean> {

	private Class<T> exception;
	private ActuatorDriver driver;
	private RealtimeBoolean alias;

	protected TypedActuatorExceptionMapper(Class<T> exception, ActuatorDriver driver, RealtimeBoolean alias) {
		super(ExceptionRealtimeBoolean.class);
		this.exception = exception;
		this.driver = driver;
		this.alias = alias;
	}

	@Override
	protected RealtimeValue<Boolean> createAlias(ExceptionRealtimeBoolean value) {
		CommandRealtimeException exception = value.getException();
		if (this.exception.isAssignableFrom(exception.getClass())
				&& ((ActuatorDriverRealtimeException) exception).getActuatorDriver() == driver) {
			return alias;
		} else {
			return null;
		}
	}

}
