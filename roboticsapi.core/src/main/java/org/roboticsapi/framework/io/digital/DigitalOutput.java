/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.digital;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.framework.io.Output;
import org.roboticsapi.framework.io.activity.DigitalOutputSensorInterface;

public final class DigitalOutput extends Output<DigitalOutputDriver, Boolean> {

	@Override
	public final RealtimeBoolean getSensor() {
		return use(DigitalOutputSensorInterface.class).getSensor();
	}

}
