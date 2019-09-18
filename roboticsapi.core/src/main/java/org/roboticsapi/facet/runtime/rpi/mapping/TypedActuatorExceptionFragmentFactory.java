/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ExceptionRealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class TypedActuatorExceptionFragmentFactory<T extends ActuatorDriverRealtimeException>
		extends TypedRealtimeValueFragmentFactory<Boolean, ExceptionRealtimeBoolean> {

	private Class<T> exception;
	private ActuatorDriver driver;
	private OutPort errorPort;

	protected TypedActuatorExceptionFragmentFactory(Class<T> exception, ActuatorDriver driver, OutPort errorPort) {
		super(ExceptionRealtimeBoolean.class);
		this.exception = exception;
		this.driver = driver;
		this.errorPort = errorPort;
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(ExceptionRealtimeBoolean value)
			throws MappingException, RpiException {
		CommandRealtimeException exception = value.getException();
		if (this.exception.isAssignableFrom(exception.getClass())
				&& ((ActuatorDriverRealtimeException) exception).getActuatorDriver() == driver) {
			RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value);
			FragmentInPort inPort = ret.addInPort("inValue");
			ret.addDependency(inPort, errorPort);
			ret.defineResult(inPort.getInternalOutPort());
			return ret;
		} else {
			return null;
		}
	}

}
