/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import java.util.List;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.exception.CommunicationException;

public interface MultiJointDeviceDriver extends ActuatorDriver {

	int getJointCount();

	Joint getJoint(int joint);

	List<? extends Joint> getJoints();

	double[] getJointAngles() throws CommunicationException;

	double[] getMeasuredJointAngles() throws CommunicationException;

	JointDriver createJointDriver(int jointNumber);

	void setup(List<? extends Joint> joints);

}
