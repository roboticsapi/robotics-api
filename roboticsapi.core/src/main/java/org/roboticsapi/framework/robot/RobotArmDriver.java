/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.framework.multijoint.MultiJointDeviceDriver;

public interface RobotArmDriver extends MultiJointDeviceDriver {

	Transformation getForwardKinematics(double[] joints) throws CommunicationException;

	double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws CommunicationException, KinematicsException;

	RealtimeTransformation getForwardKinematicsSensor(RealtimeDouble[] joints);

	RealtimeDouble[] getInverseKinematicsSensor(RealtimeTransformation point, RealtimeDouble[] hintJoints,
			DeviceParameterBag parameters) throws KinematicsException;

}
