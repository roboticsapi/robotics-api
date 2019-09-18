/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public abstract class AbstractComposedActuatorDriver<D extends Actuator, R extends RoboticsRuntime>
		extends AbstractComposedDeviceDriver<D, R> implements ActuatorDriver {

	@Override
	public List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		List<Class<? extends ActuatorDriverRealtimeException>> ret = new ArrayList<>();
		for (ActuatorDriverRealtimeException e : defineActuatorDriverExceptions()) {
			ret.add(e.getClass());
		}
		return ret;
	}

}
