/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.driver;

import java.util.Map;

import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.core.DeviceInterfaceModifierCollector;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity.CartesianMotionInterfaces;
import org.roboticsapi.framework.multijoint.activity.JointPtpInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;
import org.roboticsapi.framework.robot.KinematicsException;
import org.roboticsapi.framework.robot.RobotArm;
import org.roboticsapi.framework.robot.RobotArmDriver;
import org.roboticsapi.framework.robot.activity.CartesianGoalMotionInterfaceWithJoints;
import org.roboticsapi.framework.robot.activity.GoalMotionInterface;
import org.roboticsapi.framework.robot.activity.JointPtpInterfaceWithCartesian;
import org.roboticsapi.framework.robot.activity.LinearMotionInterfaceWithJoints;
import org.roboticsapi.framework.robot.activity.MotionInterface;
import org.roboticsapi.framework.robot.activity.RobotPtpInterface;
import org.roboticsapi.framework.robot.activity.TeachingInterface;
import org.roboticsapi.framework.robot.runtime.rpi.activity.GoalMotionInterfaceImpl;
import org.roboticsapi.framework.robot.runtime.rpi.activity.MotionInterfaceImpl;
import org.roboticsapi.framework.robot.runtime.rpi.activity.RobotPtpInterfaceImpl;
import org.roboticsapi.framework.robot.runtime.rpi.activity.TeachingInterfaceImpl;

/**
 * Default implementation of a {@link RobotArmDriver} for the RCC reference
 * implementation.
 */
public class RobotArmGenericDriver<T extends RobotArm> extends MultiJointDeviceGenericDriver<T>
		implements RobotArmDriver, CartesianActuatorDriver {

	protected static final String DEVICE_INTERFACE = "armkinematics";

	public RobotArmGenericDriver() {
	}

	public RobotArmGenericDriver(T device, RpiRuntime runtime) {
		this();
		setDevice(device);
		setRuntime(runtime);
	}

	@Override
	public final Transformation getForwardKinematics(double[] joints) throws CommunicationException {
		checkDeviceAndRuntimeOperational();
		try {
			RealtimeDouble[] jointRV = new RealtimeDouble[joints.length];
			for (int i = 0; i < jointRV.length; i++) {
				jointRV[i] = RealtimeDouble.createFromConstant(joints[i]);
			}
			RealtimeTransformation ret = getForwardKinematicsSensor(jointRV);
			return ret.getCurrentValue();
		} catch (RealtimeValueReadException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	public double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws CommunicationException, KinematicsException {
		try {
			RealtimeDouble[] hints = new RealtimeDouble[getJointCount()];
			for (int i = 0; i < getJointCount(); i++) {
				hints[i] = RealtimeDouble.createFromConstant(hintJoints[i]);
			}

			RealtimeDouble[] result = getInverseKinematicsSensor(RealtimeTransformation.createfromConstant(point),
					hints, parameters);
			Double[] joints = RealtimeDoubleArray.createFromComponents(result).getCurrentValue();
			double[] ret = new double[getJointCount()];
			for (int i = 0; i < joints.length; i++) {
				ret[i] = joints[i];
			}
			return ret;
		} catch (RealtimeValueReadException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	protected final boolean checkMultiJointDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		return interfaces.containsKey(DEVICE_INTERFACE) && checkRobotArmInterfaces(interfaces);
	}

	protected boolean checkRobotArmInterfaces(Map<String, RpiParameters> interfaces) {
		return true;
	}

	@Override
	public RealtimeTransformation getForwardKinematicsSensor(RealtimeDouble[] joints) {
		return new RobotArmForwardKinematicsRealtimeTransformation(this, joints);
	}

	@Override
	public RealtimeDouble[] getInverseKinematicsSensor(RealtimeTransformation point, RealtimeDouble[] hintJoints,
			DeviceParameterBag parameters) throws KinematicsException {
		FrameTopology topology = World.getCommandedTopology().forRuntime(getRuntime());
		RealtimePose motionCenter = parameters.get(MotionCenterParameter.class).getMotionCenter().asRealtimeValue();
		RealtimePose pose = RealtimePose.createFromTransformation(getDevice().getBase(), point);

		FrameProjectorParameter projector = parameters.get(FrameProjectorParameter.class);
		if (projector != null && projector.getProjector() != null) {
			try {
				pose = projector.getProjector().project(pose, motionCenter, parameters);
			} catch (TransformationException e) {
				throw new KinematicsException("Frame projector failed.");
			}
		}

		try {
			point = new RealtimePoseCoincidence(pose, motionCenter).getTransformation(getDevice().getBase(),
					getDevice().getMovingFrame(), topology);
		} catch (TransformationException e) {
			throw new KinematicsException("Position cannot be controlled by the robot.");
		}
		return new RobotArmInverseKinematicsRealtimeDoubleArray(this, point, hintJoints).getDoubles();
	}

	@Override
	public Relation createRelation(Frame from, Frame to) {
		return null;
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

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		super.collectDeviceInterfaceFactories(collector);

		CartesianMotionInterfaces.collectDeviceInterfaceFactories(this, collector);

		collector.add(RobotPtpInterface.class, () -> new RobotPtpInterfaceImpl(this));
		collector.add(MotionInterface.class, () -> new MotionInterfaceImpl(this));
		collector.add(TeachingInterface.class, () -> new TeachingInterfaceImpl(this));
		collector.add(GoalMotionInterface.class, () -> new GoalMotionInterfaceImpl(this));

	}

	@Override
	protected void collectDeviceInterfaceModifiers(DeviceInterfaceModifierCollector collector) {
		super.collectDeviceInterfaceModifiers(collector);

		collector.add(LinearMotionInterface.class,
				instance -> new LinearMotionInterfaceWithJoints(instance, getDevice()));
		collector.add(CartesianGoalMotionInterface.class,
				instance -> new CartesianGoalMotionInterfaceWithJoints(instance, getDevice()));
		collector.add(JointPtpInterface.class, instance -> new JointPtpInterfaceWithCartesian(instance, getDevice()));
	}

}
