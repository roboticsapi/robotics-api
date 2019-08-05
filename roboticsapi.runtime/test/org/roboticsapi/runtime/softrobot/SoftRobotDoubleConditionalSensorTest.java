/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.softrobot;

import org.roboticsapi.runtime.generic.AbstractDoubleConditionalSensorTest;
import org.roboticsapi.runtime.generic.RuntimeSetup;

public class SoftRobotDoubleConditionalSensorTest extends AbstractDoubleConditionalSensorTest {
	@Override
	public RuntimeSetup getRuntimeSetup() {
		return new SoftRobotRuntimeSetup();
	}
}
