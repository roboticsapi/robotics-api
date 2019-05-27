/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot;

import java.util.List;
import java.util.Set;

import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.AbstractJoint;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * Base interface for robot arms consisting of multiple {@link AbstractJoint}s.
 */
public interface RobotArm extends MultiJointDevice, CartesianMotionDevice {

	/**
	 * Gets the default motion center.
	 * 
	 * @return the default motion center
	 */
	@Override
	Frame getDefaultMotionCenter();

	/**
	 * Gets the flange {@link Frame} of this robot.
	 * 
	 * @return the flange {@link Frame}
	 */
	Frame getFlange();

	/**
	 * Returns a set of {@link Device}s of the specified type which are mounted at
	 * the robot's flange (or exactly attached to its flange frame).
	 * 
	 * @param type the type of {@link Device} to look for.
	 * @return a set of mounted {@link Device}s
	 */
	<T extends Device> Set<T> getMountedDevices(Class<T> type);

	<T extends Device> T getMountedDevice(Class<T> type);

	/**
	 * Gets the forward kinematics.
	 * 
	 * @param joints the joints
	 * @return the forward kinematics
	 * @throws CommunicationException if the communication fails
	 */
	Transformation getForwardKinematics(double[] joints) throws CommunicationException;

	/**
	 * Gets a forward kinematics interpreted as the geometric transformation between
	 * the robot base and the flange.
	 * 
	 * @param joints sensors for the joint values
	 * @return sensor for the forward kinematics
	 */
	TransformationSensor getForwardKinematicsSensor(DoubleSensor[] joints) throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given end-effector configuration.
	 * 
	 * @param to         the {@link Frame} to calculate inverse kinematics for
	 * @param parameters optional {@link DeviceParameters}s
	 * @return the inverse kinematics solution
	 * @throws RoboticsException if kinematics could not be calculated
	 */
	double[] getInverseKinematics(final Frame to, final DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given end-effector configuration.
	 * 
	 * @param to         the {@link Frame} to calculate inverse kinematics for
	 * @param hintJoints a 'hint' to the preferred joint configuration to select; if
	 *                   multiple joint configurations exist, the one with the
	 *                   smallest distance to these hint joints will be chosen
	 * @param parameters optional {@link DeviceParameters}s
	 * @return the inverse kinematics solution
	 * @throws RoboticsException if kinematics could not be calculated
	 */
	double[] getInverseKinematics(final Frame to, final double[] hintJoints, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given end-effector configuration.
	 * 
	 * @param to           the {@link Frame} to calculate inverse kinematics for
	 * @param motionCenter the motion center {@link Frame} for which kinematics
	 *                     should be calculated
	 * @return the inverse kinematics solution
	 * @throws RoboticsException if kinematics could not be calculated
	 */
	double[] getInverseKinematics(Frame to, Frame motionCenter) throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given end-effector configuration.
	 * 
	 * @param to           the {@link Frame} to calculate inverse kinematics for
	 * @param motionCenter the motion center {@link Frame} for which kinematics
	 *                     should be calculated
	 * @param hintJoints   a 'hint' to the preferred joint configuration to select;
	 *                     if multiple joint configurations exist, the one with the
	 *                     smallest distance to these hint joints will be chosen
	 * @return the inverse kinematics solution
	 * @throws RoboticsException if kinematics could not be calculated
	 */
	double[] getInverseKinematics(Frame to, Frame motionCenter, double[] hintJoints) throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given {@link Transformation}, interpreted
	 * as the geometric transformation between {@link RobotArm} base and flange.
	 * 
	 * @param point the {@link Transformation} stating the input to the kinematics
	 *              calculation
	 * @return the inverse kinematics solution
	 * @throws RoboticsException if kinematics could not be calculated
	 */
	double[] getInverseKinematics(Transformation point) throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given {@link Transformation}, interpreted
	 * as the geometric transformation between {@link RobotArm} base and flange.
	 * 
	 * @param point      the {@link Transformation} stating the input to the
	 *                   kinematics calculation
	 * @param hintJoints a 'hint' to the preferred joint configuration to select; if
	 *                   multiple joint configurations exist, the one with the
	 *                   smallest distance to these hint joints will be chosen
	 * @return the inverse kinematics solution
	 * @throws KinematicsException    if no valid kinematics solution was found
	 * @throws CommunicationException if communication with the actual robot device
	 *                                failed
	 */
	double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws KinematicsException, CommunicationException;

	/**
	 * Gets the inverse kinematics for a given {@link TransformationSensor},
	 * interpreted as the geometric transformation between {@link RobotArm} base and
	 * flange.
	 * 
	 * @param point the {@link TransformationSensor} stating the input to the
	 *              kinematics calculation
	 * @return the inverse kinematics solution
	 * @throws RoboticsException if kinematics could not be calculated
	 */
	DoubleSensor[] getInverseKinematicsSensor(TransformationSensor point, DeviceParameterBag parameters)
			throws RoboticsException;

	/**
	 * Gets the inverse kinematics for a given {@link TransformationSensor},
	 * interpreted as the geometric transformation between {@link RobotArm} base and
	 * flange.
	 * 
	 * @param point      the {@link TransformationSensor} stating the input to the
	 *                   kinematics calculation
	 * @param hintJoints a 'hint' to the preferred joint configuration to select; if
	 *                   multiple joint configurations exist, the one with the
	 *                   smallest distance to these hint joints will be chosen
	 * @return the inverse kinematics solution
	 */
	DoubleSensor[] getInverseKinematicsSensor(TransformationSensor point, DoubleSensor[] hintJoints,
			DeviceParameterBag parameters) throws RoboticsException;

	/**
	 * Retrieves a {@link Sensor} measuring the actual {@link Transformation}
	 * between the robot's base {@link Frame} and its default motion center
	 * {@link Frame}.
	 * 
	 * @return a {@link Sensor} measuring the {@link Transformation}
	 */
	RelationSensor getMeasuredTransformationSensor();

	/**
	 * Retrieves a {@link Sensor} measuring the actual {@link Twist} between the
	 * robot's base {@link Frame} and its default motion center {@link Frame}.
	 * 
	 * @return a {@link Sensor} measuring the {@link Twist}
	 */
	VelocitySensor getMeasuredVelocitySensor();

	/**
	 * Retrieves strategies for resolving kinematic redundancy which are available
	 * for this device.
	 * 
	 * @return list of available redundancy strategies
	 */
	List<RedundancyStrategy> getRedundancyStrategies();

	/**
	 * Retrieves a redundancy strategy with given properties.
	 * 
	 * @param classes Requested redundancy strategy properties
	 * @return a redundancy strategy with the requested properties, or null
	 */
	RedundancyStrategy getRedundancyStrategy(final Class<? extends RedundancyStrategy>... classes);

	/**
	 * Retrieves a {@link Sensor} measuring the commanded {@link Transformation}
	 * between the robot's base {@link Frame} and its default motion center
	 * {@link Frame}.
	 * 
	 * @return a {@link Sensor} measuring the {@link Transformation}
	 */
	RelationSensor getTransformationSensor();

	/**
	 * Retrieves a {@link Sensor} measuring the commanded {@link Twist} between the
	 * robot's base {@link Frame} and its default motion center {@link Frame}.
	 * 
	 * @return a {@link Sensor} measuring the {@link Twist}
	 */
	VelocitySensor getVelocitySensor();

	@Override
	public RobotArmDriver getDriver();

}