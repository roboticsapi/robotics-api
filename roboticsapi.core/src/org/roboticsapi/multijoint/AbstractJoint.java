/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.sensor.TransformationSensor;

/**
 * A joint (controllable axis, e.g. revolute or prismatic)
 *
 * @param <JD> Type of Joint driver
 */
public abstract class AbstractJoint<JD extends JointDriver> extends AbstractActuator<JD> implements Joint {

	private double homePosition = 0;
	private Double minimumPosition = null;
	private Double maximumPosition = null;
	private Double maximumVelocity = null;
	private Double maximumAcceleration = null;
	private Double maximumJerk = Double.MAX_VALUE;

	private Frame fixedFrame;
	private Frame movingFrame;
	private JointConnection connection;

	@Override
	public final Frame getFixedFrame() {
		return fixedFrame;
	}

	/**
	 * Sets the fixed frame of the joint (optional, auto-generated if not given).
	 *
	 * @param frame the fixed frame
	 */
	@ConfigurationProperty(Optional = true)
	public final void setFixedFrame(Frame frame) {
		immutableWhenInitialized();
		this.fixedFrame = frame;
	}

	@Override
	public final Frame getMovingFrame() {
		return movingFrame;
	}

	/**
	 * Sets the moving frame of the joint (optional, auto-generated if not given).
	 *
	 * @param frame the moving frame
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMovingFrame(Frame frame) {
		immutableWhenInitialized();
		this.movingFrame = frame;
	}

	/**
	 * Retrieves the maximum acceleration.
	 *
	 * @return maximum acceleration of the joint
	 */
	@Override
	public final double getMaximumAcceleration() {
		return maximumAcceleration;
	}

	/**
	 * Sets the maximum acceleration (obligatory).
	 *
	 * @param maximumAcceleration maximum acceleration of the joint
	 */
	@ConfigurationProperty
	public final void setMaximumAcceleration(final double maximumAcceleration) {
		immutableWhenInitialized();
		this.maximumAcceleration = maximumAcceleration;
	}

	/**
	 * Retrieves the maximum jerk.
	 *
	 * @return maximum jerk of the joint
	 */
	@Override
	public final double getMaximumJerk() {
		return maximumJerk;
	}

	/**
	 * Sets the maximum jerk.
	 *
	 * @param maximumJerk maximum jerk of the joint
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMaximumJerk(final double maximumJerk) {
		immutableWhenInitialized();
		this.maximumJerk = maximumJerk;
	}

	/**
	 * Retrieves the maximum velocity.
	 *
	 * @return maximum velocity of the joint
	 */
	@Override
	public final double getMaximumVelocity() {
		return maximumVelocity;
	}

	/**
	 * Sets the maximum velocity (obligatory).
	 *
	 * @param maximumVelocity maximum velocity of the joint
	 */
	@ConfigurationProperty
	public final void setMaximumVelocity(final double maximumVelocity) {
		immutableWhenInitialized();
		this.maximumVelocity = maximumVelocity;
	}

	/**
	 * Retrieves the minimum position
	 *
	 * @return minimum position of the joint
	 */
	@Override
	public final double getMinimumPosition() {
		return minimumPosition;
	}

	/**
	 * Sets the minimum position (obligatory).
	 *
	 * @param minimumPosition minimum position of the joint
	 */
	@ConfigurationProperty
	public final void setMinimumPosition(final double minimumPosition) {
		immutableWhenInitialized();
		this.minimumPosition = minimumPosition;
	}

	/**
	 * Retrieves the maximum position
	 *
	 * @return maximum position of the joint
	 */
	@Override
	public final double getMaximumPosition() {
		return maximumPosition;
	}

	/**
	 * Sets the maximum position (obligatory).
	 *
	 * @param maximumPosition maximum position of the joint
	 */
	@ConfigurationProperty
	public final void setMaximumPosition(final double maximumPosition) {
		immutableWhenInitialized();
		this.maximumPosition = maximumPosition;
	}

	@Override
	public final double getHomePosition() {
		return homePosition;
	}

	/**
	 * Sets the home position (optional, auto-generated if not given).
	 *
	 * @param homePosition home position of the joint
	 */
	@ConfigurationProperty(Optional = true)
	public final void setHomePosition(double homePosition) {
		immutableWhenInitialized();
		this.homePosition = homePosition;
	}

	@Override
	public final TransformationSensor getForwardKinematicsSensor(DoubleSensor position) {
		return connection.getTransformationSensor(position);
	}

	@Override
	public final double getCommandedPosition() throws RoboticsException {
		return getCommandedPositionSensor().getCurrentValue();
	}

	@Override
	public final double getMeasuredPosition() throws RoboticsException {
		return getMeasuredPositionSensor().getCurrentValue();
	}

	@Override
	public final DoubleSensor getCommandedPositionSensor() {
		return getDriver().getCommandedPositionSensor();
	}

	@Override
	public final DoubleSensor getMeasuredPositionSensor() {
		return getDriver().getMeasuredPositionSensor();
	}

	@Override
	public final DoubleSensor getCommandedVelocitySensor() {
		return getDriver().getCommandedVelocitySensor();
	}

	@Override
	public final DoubleSensor getMeasuredVelocitySensor() {
		return getDriver().getMeasuredVelocitySensor();
	}

	/**
	 * Returns the joint connection between fixed and moving frame.
	 *
	 * @return the joint connection
	 */
	@Override
	public final JointConnection getJointConnection() {
		return connection;
	}

	@Override
	protected final void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (fixedFrame == null || !fixedFrame.isInitialized()) {
			fixedFrame = new Frame(getName() + " Fixed");
			createdObjects.put("fixedFrame", fixedFrame);
		}

		if (movingFrame == null || !movingFrame.isInitialized()) {
			movingFrame = new Frame(getName() + " Moving");
			createdObjects.put("movingFrame", movingFrame);
		}
	}

	@Override
	protected final void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey("fixedFrame")) {
			fixedFrame = null;
		}
		if (createdObjects.containsKey("movingFrame")) {
			movingFrame = null;
		}
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNoParent("fixedFrame", fixedFrame);
		checkNoParent("movingFrame", movingFrame);

		checkNotNull("maximumAcceleration", maximumAcceleration);
		checkNotNull("maximumVelocity", maximumVelocity);
		checkNotNull("minimumPosition", minimumPosition);
		checkNotNull("maximumPosition", maximumPosition);
	}

	@Override
	protected void setupDriver(JD driver) {
		// do nothing
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		fixedFrame.setParent(this);
		movingFrame.setParent(this);

		connection = createConnection();
		connection.setParent(this);
		fixedFrame.addRelation(connection, movingFrame);
	}

	protected abstract JointConnection createConnection();

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		connection.setParent(null);
		fixedFrame.removeRelation(connection);
		connection = null;

		fixedFrame.setParent(null);
		movingFrame.setParent(null);
	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
		// no maximum parameters known here
		// TODO: Really?
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

}
