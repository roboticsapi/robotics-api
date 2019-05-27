/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.AbstractMultiJointDevice;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.link.Link;
import org.roboticsapi.multijoint.parameter.BlendingParameter;
import org.roboticsapi.robot.parameter.RobotTool;
import org.roboticsapi.robot.parameter.RobotToolParameter;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;
import org.roboticsapi.world.util.WorldUtils;

public abstract class AbstractRobotArm<J extends Joint, DD extends RobotArmDriver>
		extends AbstractMultiJointDevice<J, DD> implements RobotArm {

	/** The flange frame */
	private Frame flange;

	/** Center of mass of the (default) payload */
	private Frame defaultCenterOfMass;

	/** Mass of the (default) payload */
	private double defaultMass = 0;

	/** Mass moments of inertia of the (default) payload */
	private double jx = 0, jy = 0, jz = 0;

	/**
	 * Constructor.
	 * 
	 * @param jointCount the number of joints the robot arm has.
	 */
	public AbstractRobotArm(int jointCount) {
		super(jointCount, jointCount + 1);
	}

	@Override
	protected abstract Link createLink(int number);

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNotNullAndInitialized("flange", flange);
		checkNoParent("flange", flange);
	}

	@Override
	protected final void fillAutomaticMultiJointDeviceProperties(Map<String, RoboticsObject> createdObjects) {
		flange = fill("flange", flange, new Frame(getName() + " Flange"), createdObjects);

		if (defaultCenterOfMass == null || !defaultCenterOfMass.isInitialized()) {
			defaultCenterOfMass = flange;
			createdObjects.put("defaultCenterOfMass", null);
		}
		fillAutomaticRobotArmProperties(createdObjects);
	}

	protected void fillAutomaticRobotArmProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected final void clearAutomaticMultiJointDeviceProperties(Map<String, RoboticsObject> createdObjects) {
		clearAutomaticRobotArmProperties(createdObjects);

		flange = clear("flange", flange, createdObjects);
		defaultCenterOfMass = clear("defaultCenterOfMass", defaultCenterOfMass, createdObjects);
	}

	protected void clearAutomaticRobotArmProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		// TODO: Change Parameters for automatic calculation of mass and
		// center of mass
		addDefaultParameters(new RobotToolParameter(new RobotTool(getDefaultPayloadMass(), getDefaultCenterOfMass(),
				getDefaultInertiaX(), getDefaultInertiaY(), getDefaultInertiaZ())));

		addDefaultParameters(new MotionCenterParameter(getFlange()));

		// TODO: Fix motion planning to work without BlendingParameter
		addDefaultParameters(new BlendingParameter(1));
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();
		flange.setParent(this);
	}

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		flange.setParent(null);
		super.cleanupEntities();
	}

	@Override
	public final Frame getDefaultMotionCenter() {
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
		return defaultCenterOfMass;
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
		return jx;
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
		return jy;
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
		return jz;
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
		return defaultMass;
	}

	@Override
	public final Frame getFlange() {
		return flange;
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
	public final double[] getInverseKinematics(Frame to, DeviceParameters... parameters) throws RoboticsException {
		final MotionCenterParameter mp = this.getDefaultParameters().withParameters(parameters)
				.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		return getInverseKinematics(to, mp.getMotionCenter());
	}

	@Override
	public final double[] getInverseKinematics(Frame to, double[] hintJoints, DeviceParameters... parameters)
			throws RoboticsException {
		final MotionCenterParameter mp = this.getDefaultParameters().withParameters(parameters)
				.get(MotionCenterParameter.class);

		if (mp == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		return getInverseKinematics(to, mp.getMotionCenter(), hintJoints);
	}

	@Override
	public final double[] getInverseKinematics(Frame to, Frame motionCenter) throws RoboticsException {
		Transformation point = getBase().getTransformationTo(to)
				.multiply(getFlange().getTransformationTo(motionCenter).invert());
		return getInverseKinematics(point);
	}

	@Override
	public final double[] getInverseKinematics(Frame to, Frame motionCenter, double[] hintJoints)
			throws RoboticsException {
		Transformation point = getBase().getTransformationTo(to)
				.multiply(getFlange().getTransformationTo(motionCenter).invert());
		return getInverseKinematics(point, hintJoints, getDefaultParameters());
	}

	@Override
	public final double[] getInverseKinematics(Transformation point) throws RoboticsException {
		return getInverseKinematics(point, getJointAngles(), getDefaultParameters());
	}

	@Override
	public DoubleSensor[] getInverseKinematicsSensor(TransformationSensor point, DeviceParameterBag parameters)
			throws RoboticsException {
		DoubleSensor[] jointSensors = new DoubleSensor[getJointCount()];
		for (int i = 0; i < getJointCount(); i++) {
			jointSensors[i] = getJoint(i).getDriver().getCommandedPositionSensor();
		}
		return getInverseKinematicsSensor(point, jointSensors, parameters);
	}

	@Override
	public TransformationSensor getForwardKinematicsSensor(DoubleSensor[] joints) throws RoboticsException {
		if (getJointCount() != joints.length) {
			throw new IllegalArgumentException(
					joints.length + " values supplied, but robot arm has " + getJointCount() + " joints");
		}

		return getDriver().getForwardKinematicsSensor(joints);
	}

	@Override
	public DoubleSensor[] getInverseKinematicsSensor(TransformationSensor point, DoubleSensor[] hintJoints,
			DeviceParameterBag parameters) throws RoboticsException {
		return getDriver().getInverseKinematicsSensor(point, hintJoints, parameters);
	}

	public double[] getInverseKinematicsInternal(Transformation point, double[] hintJoints,
			DeviceParameterBag parameters) throws KinematicsException, CommunicationException {
		return getDriver().getInverseKinematics(point, hintJoints, parameters);
	}

	@Override
	public final double[] getInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag parameters)
			throws KinematicsException, CommunicationException {
		if (!isPresent()) {
			throw new CommunicationException("Kinematics calculation not possible: Device is in State " + getState());
		}
		return getInverseKinematicsInternal(point, hintJoints, parameters);
	}

	protected final Link getFlangeLink() {
		return getLink(getJointCount());
	}

	@Override
	public final RelationSensor getMeasuredTransformationSensor() {
		return getBase().getMeasuredRelationSensor(getFlange());
	}

	@Override
	public final VelocitySensor getMeasuredVelocitySensor() {
		return getDriver().getMeasuredVelocitySensor();
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
		Connection connection = flangeLink.getConnection();

		return WorldUtils.getConnectedEntities(flange, type, true, false, connection, flangeLink, this);
	}

	@Override
	public List<RedundancyStrategy> getRedundancyStrategies() {
		return new ArrayList<RedundancyStrategy>();
	}

	@Override
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

	@Override
	public final RelationSensor getTransformationSensor() {
		return getBase().getRelationSensor(getFlange());
	}

	@Override
	public final VelocitySensor getVelocitySensor() {
		return getBase().getVelocitySensorOf(getFlange());
	}

	/**
	 * Sets the flange {@link Frame}.
	 * 
	 * @param flange the new flange
	 */
	@ConfigurationProperty(Optional = true)
	public final void setFlange(Frame flange) {
		immutableWhenInitialized();
		this.flange = flange;
	}

	/**
	 * Set the center of mass that is assumed for the robot's payload by default
	 * (optional).
	 * 
	 * @return the default center of mass.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultCenterOfMass(Frame defaultCenterOfMass) {
		immutableWhenInitialized();
		this.defaultCenterOfMass = defaultCenterOfMass;
	}

	/**
	 * Set the mass that is assumed for the robot's payload by default (optional).
	 * 
	 * @return the default mass.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultPayloadMass(double mass) {
		immutableWhenInitialized();
		this.defaultMass = mass;
	}

	/**
	 * Set the inertia about the X axis that is assumed for the robot's payload by
	 * default (optional).
	 * 
	 * @return the default inertia about the X axis.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultInertiaX(double jx) {
		immutableWhenInitialized();
		this.jx = jx;
	}

	/**
	 * Set the inertia about the Y axis that is assumed for the robot's payload by
	 * default (optional).
	 * 
	 * @return the default inertia about the Y axis.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultInertiaY(double jy) {
		immutableWhenInitialized();
		this.jy = jy;
	}

	/**
	 * Set the inertia about the Z axis that is assumed for the robot's payload by
	 * default (optional).
	 * 
	 * @return the default inertia about the Z axis.
	 */
	@ConfigurationProperty(Optional = true)
	public final void setDefaultInertiaZ(double jz) {
		immutableWhenInitialized();
		this.jz = jz;
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

	@Override
	protected void setupDriver(DD driver) {
		super.setupDriver(driver);
		driver.setup(getBase(), getFlange(), getDefaultMotionCenter());
	}

}
