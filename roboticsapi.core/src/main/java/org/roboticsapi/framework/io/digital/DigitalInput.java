/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.digital;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.framework.io.Input;
import org.roboticsapi.framework.io.activity.DigitalInputSensorInterface;

public final class DigitalInput extends Input<DigitalInputDriver, Boolean> {

	@Override
	public final RealtimeBoolean getSensor() {
		return use(DigitalInputSensorInterface.class).getSensor();
	}

	public final RealtimeBoolean getSetState() {
		return getSensor();
	}

	@Override
	public RealtimeValue<Boolean> getSensor(RoboticsRuntime runtime) {
		return use(DigitalInputSensorInterface.class, runtime).getSensor();
	}

}