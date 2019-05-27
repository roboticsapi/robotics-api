/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public interface RobotArmDriver extends MultiJointDeviceDriver {

	void setup(Frame base, Frame flange, Frame defaultMotionCenter);

	Transformation getForwardKinematics(double[] joints) throws CommunicationException;

	double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws CommunicationException, KinematicsException;

	TransformationSensor getForwardKinematicsSensor(DoubleSensor[] joints);

	DoubleSensor[] getInverseKinematicsSensor(TransformationSensor point, DoubleSensor[] hintJoints,
			DeviceParameterBag parameters) throws KinematicsException;

	VelocitySensor getMeasuredVelocitySensor();

	Frame getBase();

	Frame getFlange();

	Frame getDefaultMotionCenter();

}
