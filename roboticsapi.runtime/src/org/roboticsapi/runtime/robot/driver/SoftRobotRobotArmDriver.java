/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver;

import java.util.List;

import org.roboticsapi.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.sensor.DoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.robot.KinematicsException;
import org.roboticsapi.robot.RobotArmDriver;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * Default implementation of a {@link RobotArmDriver} for the RCC reference
 * implementation.
 */
public class SoftRobotRobotArmDriver extends SoftRobotMultiJointDeviceDriver
		implements RobotArmDriver, CartesianActuatorDriver {

	protected static final String DEVICE_INTERFACE = "armkinematics";
	private Frame flange;
	private Frame base;
	private Frame defaultMotionCenter;

	@Override
	public final Transformation getForwardKinematics(double[] joints) throws CommunicationException {
		try {
			TransformationSensor result = getForwardKinematicsSensor(
					DoubleArraySensor.fromConstant(joints).getSensors());
			return result.getCurrentValue();
		} catch (SensorReadException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	public double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws CommunicationException, KinematicsException {
		try {
			DoubleSensor[] hints = new DoubleSensor[getJointCount()];
			for (int i = 0; i < getJointCount(); i++) {
				hints[i] = DoubleSensor.fromValue(hintJoints[i]);
			}

			DoubleSensor[] result = getInverseKinematicsSensor(TransformationSensor.fromConstant(point), hints,
					parameters);
			Double[] joints = DoubleArraySensor.fromSensors(result).getCurrentValue();
			double[] ret = new double[getJointCount()];
			for (int i = 0; i < joints.length; i++) {
				ret[i] = joints[i];
			}
			return ret;
		} catch (SensorReadException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	public final VelocitySensor getMeasuredVelocitySensor() {
		return getBase().getMeasuredVelocitySensorOf(getFlange());
	}

	@Override
	protected final boolean checkMultiJointDeviceInterfaces(List<String> interfaces) {
		return interfaces.contains(DEVICE_INTERFACE) && checkRobotArmInterfaces(interfaces);
	}

	protected boolean checkRobotArmInterfaces(List<String> interfaces) {
		return true;
	}

	@Override
	public final Frame getBase() {
		return base;
	}

	@Override
	public final Frame getFlange() {
		return flange;
	}

	@Override
	public Frame getDefaultMotionCenter() {
		return defaultMotionCenter;
	}

	@Override
	public TransformationSensor getForwardKinematicsSensor(DoubleSensor[] joints) {
		return new SoftRobotRobotArmForwardKinematicsSensor(this, joints);
	}

	@Override
	public DoubleSensor[] getInverseKinematicsSensor(TransformationSensor point, DoubleSensor[] hintJoints,
			DeviceParameterBag parameters) throws KinematicsException {

		FrameProjectorParameter proj = parameters.get(FrameProjectorParameter.class);
		if (proj != null && proj.getProjector() != null) {
			try {
				point = proj.getProjector().project(new RelationSensor(point, getBase(), getFlange()), parameters)
						.getTransformationSensor();
			} catch (TransformationException e) {
				throw new KinematicsException(e.getMessage());
			}
		}

		return new SoftRobotRobotArmInverseKinematicsSensor(this, point, hintJoints).getSensors();
	}

	@Override
	public Frame getReferenceFrame() {
		return getBase();
	}

	@Override
	public Frame getMovingFrame() {
		return getFlange();
	}

	@Override
	public Relation createRelation() {
		return null;
	}

	@Override
	public void setup(Frame referenceFrame, Frame movingFrame) {
		// empty
	}

	@Override
	public void setup(Frame base, Frame flange, Frame defaultMotionCenter) {
		this.base = base;
		this.flange = flange;
		this.defaultMotionCenter = defaultMotionCenter;
	}

	protected static String toString(Frame frame, Frame reference) {
		try {
			Transformation t = reference.getTransformationTo(frame);
			return toString(t);
		} catch (TransformationException e) {
			return "";
		}
	}

	protected static String toString(Transformation transformation) {
		return convert(transformation).toString();
	}

	protected static String toString(double x, double y, double z) {
		return convert(x, y, z).toString();
	}

	protected static RPIVector convert(double x, double y, double z) {
		return new RPIVector(new RPIdouble(x), new RPIdouble(y), new RPIdouble(z));
	}

	private static RPIFrame convert(Transformation transformation) {
		RPIVector pos = convert(transformation.getTranslation());
		RPIRotation rot = convert(transformation.getRotation());
		return new RPIFrame(pos, rot);
	}

	private static RPIVector convert(Vector vector) {
		RPIdouble x = new RPIdouble(vector.getX());
		RPIdouble y = new RPIdouble(vector.getY());
		RPIdouble z = new RPIdouble(vector.getZ());
		return new RPIVector(x, y, z);
	}

	private static RPIRotation convert(Rotation rotation) {
		RPIdouble a = new RPIdouble(rotation.getA());
		RPIdouble b = new RPIdouble(rotation.getB());
		RPIdouble c = new RPIdouble(rotation.getC());
		return new RPIRotation(a, b, c);
	}

}
