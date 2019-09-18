/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.util.WorldUtils;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.multijoint.AbstractMultiJointDevice;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.link.Link;
import org.roboticsapi.framework.multijoint.parameter.BlendingParameter;
import org.roboticsapi.framework.robot.parameter.RobotTool;
import org.roboticsapi.framework.robot.parameter.RobotToolParameter;

public abstract class AbstractRobotArm<J extends Joint, DD extends RobotArmDriver>
		extends AbstractMultiJointDevice<J, DD> implements RobotArm {

	/** The flange frame */
	private final Dependency<Frame> flange;

	/** Center of mass of the (default) payload */
	private final Dependency<Frame> defaultCenterOfMass;

	/** Mass of the (default) payload */
	private final Dependency<Double> defaultMass;

	/** Mass moments of inertia of the (default) payload */
	private final Dependency<Double> jx, jy, jz;

	/**
	 * Constructor.
	 *
	 * @param jointCount the number of joints the robot arm has.
	 */
	public AbstractRobotArm(int jointCount) {
		super(jointCount, jointCount + 1);
		flange = createDependency("flange", new Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Flange");
			}
		});
		defaultCenterOfMass = createDependency("defaultCenterOfMass", (Frame) null);
		defaultMass = createDependency("defaultMass", 0.);
		jx = createDependency("jx", 0.);
		jy = createDependency("jy", 0.);
		jz = createDependency("jz", 0.);
	}

	@Override
	protected abstract Link createLink(int number);

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		// TODO: Change Parameters for automatic calculation of mass and
		// center of mass
		addDefaultParameters(new RobotToolParameter(new RobotTool(getDefaultPayloadMass(),
				new Pose(getDefaultCenterOfMass()), getDefaultInertiaX(), getDefaultInertiaY(), getDefaultInertiaZ())));

		addDefaultParameters(new MotionCenterParameter(getFlange()));

		// TODO: Fix motion planning to work without BlendingParameter
		addDefaultParameters(new BlendingParameter(1));
	}

	@Override
	public final Pose getDefaultMotionCenter() {
		MotionCenterParameter motionCenterParameter = getDefaultParameters().get(MotionCenterParameter.class);
		return motionCenterParameter == null ? null : motionCenterParameter.getMotionCenter();
	}

	/**
	 * Returns the center of mass that is assumed for the robot's payload by
	 * default. It is used if no further {@link RobotTool} is specified as
	 * parameter.
	 *
	 * @return the default center of mass.
	 *
	 * @see RobotTool
	 * @see #addDefaultParameters(DeviceParameters)
	 */
	public final Frame getDefaultCenterOfMass() {
		if (!isInitialized()) {
			return defaultCenterOfMass.get();
		}
		return defaultCenterOfMass.get() != null ? defaultCenterOfMass.get() : flange.get();
	}

	/**
	 * Gets the inertia about the X axis that is assumed for the robot's payload by
	 * default. It is used if no further {@link RobotTool} is specified as
	 * parameter.
	 *
	 * @return the default inertia about the X axis [kg m^2].
	 *
	 * @see RobotTool
	 * @see #addDefaultParameters(DeviceParameters)
	 */
	public final double getDefaultInertiaX() {
		return jx.get();
	}

	/**
	 * Gets the inertia about the Y axis that is assumed for the robot's payload by
	 * default. It is used if no further {@link RobotTool} is specified as
	 * parameter.
	 *
	 * @return the default inertia about the Y axis [kg m^2].
	 *
	 * @see RobotTool
	 * @see #addDefaultParameters(DeviceParameters)
	 */
	public final double getDefaultInertiaY() {
		return jy.get();
	}

	/**
	 * Gets the inertia about the Z axis that is assumed for the robot's payload by
	 * default. It is used if no further {@link RobotTool} is specified as
	 * parameter.
	 *
	 * @return the default inertia about the Z axis [kg m^2].
	 *
	 * @see RobotTool
	 * @see #addDefaultParameters(DeviceParameters)
	 */
	public final double getDefaultInertiaZ() {
		return jz.get();
	}

	/**
	 * Returns the mass that is assumed for the robot's payload by default. It is
	 * used if no further {@link RobotTool} is specified as parameter.
	 *
	 * @return the default payload mass [kg].
	 *
	 * @see RobotTool
	 * @see #addDefaultParameters(DeviceParameters)
	 */
	public final double getDefaultPayloadMass() {
		return defaultMass.get();
	}

	@Override
	public final Frame getFlange() {
		return flange.get();
	}

	@Override
	public <T extends Device> T getMountedDevice(Class<T> type) {
		Set<T> devices = getMountedDevices(type);
		Iterator<T> iterator = devices.iterator();

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	@Override
	public final Transformation getForwardKinematics(double[] joints) throws CommunicationException {
		return getDriver().getForwardKinematics(joints);
	}

	@Override
	public final double[] getInverseKinematics(Pose to, DeviceParameters... parameters) throws RoboticsException {
		final MotionCenterParameter mp = this.getDefaultParameters().withParameters(parameters)
				.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		return getInverseKinematics(to, mp.getMotionCenter());
	}

	@Override
	public final double[] getInverseKinematics(Pose to, double[] hintJoints, DeviceParameters... parameters)
			throws RoboticsException {
		return getInverseKinematics(getBase().asPose().getCommandedTransformationTo(to), hintJoints,
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public final double[] getInverseKinematics(Pose to, Pose motionCenter) throws RoboticsException {
		return getInverseKinematics(to, getJointAngles(), new MotionCenterParameter(motionCenter));
	}

	@Override
	public final double[] getInverseKinematics(Pose to, Pose motionCenter, double[] hintJoints)
			throws RoboticsException {
		return getInverseKinematics(to, hintJoints, new MotionCenterParameter(motionCenter));
	}

	@Override
	public final double[] getInverseKinematics(Transformation point) throws RoboticsException {
		return getInverseKinematics(point, getJointAngles(), getDefaultParameters());
	}

	@Override
	public RealtimeDouble[] getInverseKinematicsSensor(RealtimeTransformation point, DeviceParameterBag parameters)
			throws RoboticsException {
		RealtimeDouble[] jointSensors = new RealtimeDouble[getJointCount()];
		for (int i = 0; i < getJointCount(); i++) {
			jointSensors[i] = getJoint(i).getDriver().getCommandedPositionSensor();
		}
		return getInverseKinematicsSensor(point, jointSensors, parameters);
	}

	@Override
	public RealtimeTransformation getForwardKinematicsSensor(RealtimeDouble[] joints) throws RoboticsException {
		if (getJointCount() != joints.length) {
			throw new IllegalArgumentException(
					joints.length + " values supplied, but robot arm has " + getJointCount() + " joints");
		}

		return getDriver().getForwardKinematicsSensor(joints);
	}

	@Override
	public RealtimeDouble[] getInverseKinematicsSensor(RealtimeTransformation point, RealtimeDouble[] hintJoints,
			DeviceParameterBag parameters) throws RoboticsException {
		return getDriver().getInverseKinematicsSensor(point, hintJoints, parameters);
	}

	@Override
	public final double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws KinematicsException, CommunicationException {
		if (!isPresent()) {
			throw new CommunicationException("Kinematics calculation not possible: Device is in State " + getState());
		}
		return getDriver().getInverseKinematics(point, hintJoints, parameters);
	}

	protected final Link getFlangeLink() {
		return getLink(getJointCount());
	}

	/**
	 * Returns a set of {@link Device}s of the specified type which are mounted at
	 * the robot's flange (or exactly attached to its flange frame).
	 *
	 * @param type the type of {@link Device} to look for.
	 * @return a set of mounted {@link Device}s
	 */
	@Override
	public final <T extends Device> Set<T> getMountedDevices(Class<T> type) {
		Link flangeLink = getFlangeLink();

		if (flangeLink == null) {
			return null;
		}
		List<Relation> connections = flangeLink.getConnections();

		Set<T> result = new HashSet<T>();
		for (Relation c : connections) {
			result.addAll(
					WorldUtils.getConnectedEntities(flange.get(), type, false, c.getTo(), flangeLink.getBase(), this));
		}
		return result;
	}

	@Override
	public List<RedundancyStrategy> getRedundancyStrategies() {
		return new ArrayList<RedundancyStrategy>();
	}

	@Override
	@SafeVarargs
	public final RedundancyStrategy getRedundancyStrategy(Class<? extends RedundancyStrategy>... classes) {
		final List<RedundancyStrategy> strategies = getRedundancyStrategies();
		for (final RedundancyStrategy strategy : strategies) {
			try {
				for (final Class<? extends RedundancyStrategy> c : classes) {
					c.cast(strategy);
				}
				return strategy;
			} catch (final ClassCastException cc) {
				// strategy does not have required type, try next one
			}
		}
		return null;
	}

	/**
	 * Set the center of mass that is assumed for the robot's payload by default
	 * (optional).
	 *
	 * @return the default center of mass.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultCenterOfMass(Frame defaultCenterOfMass) {
		this.defaultCenterOfMass.set(defaultCenterOfMass);
	}

	/**
	 * Set the mass that is assumed for the robot's payload by default (optional).
	 *
	 * @return the default mass.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultPayloadMass(double mass) {
		this.defaultMass.set(mass);
	}

	/**
	 * Set the inertia about the X axis that is assumed for the robot's payload by
	 * default (optional).
	 *
	 * @return the default inertia about the X axis.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultInertiaX(double jx) {
		this.jx.set(jx);
	}

	/**
	 * Set the inertia about the Y axis that is assumed for the robot's payload by
	 * default (optional).
	 *
	 * @return the default inertia about the Y axis.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultInertiaY(double jy) {
		this.jy.set(jy);
	}

	/**
	 * Set the inertia about the Z axis that is assumed for the robot's payload by
	 * default (optional).
	 *
	 * @return the default inertia about the Z axis.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultInertiaZ(double jz) {
		this.jz.set(jz);
	}

	@Override
	public abstract void validateParameters(DeviceParameters parameters) throws InvalidParametersException;

	@Override
	protected abstract void defineMaximumParameters() throws InvalidParametersException;

	@Override
	public Frame getReferenceFrame() {
		return getBase();
	}

	@Override
	public Frame getMovingFrame() {
		return getFlange();
	}

}
