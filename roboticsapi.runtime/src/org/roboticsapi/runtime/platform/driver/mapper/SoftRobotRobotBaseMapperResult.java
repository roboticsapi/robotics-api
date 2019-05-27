/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.driver.mapper;

import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.actuator.GeneralActuatorException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.impl.BaseActuatorDriverMapperResult;
import org.roboticsapi.runtime.platform.driver.SoftRobotRobotBaseDriver;

public class SoftRobotRobotBaseMapperResult extends BaseActuatorDriverMapperResult {

	public SoftRobotRobotBaseMapperResult(SoftRobotRobotBaseDriver platform, NetFragment fragment,
			DataflowOutPort completed, DataflowOutPort concurrentAccessError, DataflowOutPort drivesNotEnabledError,
			DataflowOutPort unknownError) {
		super(platform, fragment, completed);

		addExceptionPort(ConcurrentAccessException.class, concurrentAccessError);
		addExceptionPort(ActuatorNotOperationalException.class, drivesNotEnabledError);
		addExceptionPort(GeneralActuatorException.class, unknownError);

	}

}
