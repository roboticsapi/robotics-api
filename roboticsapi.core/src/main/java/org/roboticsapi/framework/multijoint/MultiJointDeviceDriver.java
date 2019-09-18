/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.exception.CommunicationException;

public interface MultiJointDeviceDriver extends ActuatorDriver {

	int getJointCount();

	double[] getJointAngles() throws CommunicationException;

	double[] getMeasuredJointAngles() throws CommunicationException;

	JointDriver getJointDriver(int joint);

	@Override
	MultiJointDevice getDevice();
}
