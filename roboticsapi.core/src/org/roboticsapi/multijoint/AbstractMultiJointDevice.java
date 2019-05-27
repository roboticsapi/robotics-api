/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.roboticsapi.multijoint.link.Link;
import org.roboticsapi.multijoint.parameter.BlendingParameter;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.sensor.RelationSensor;

/**
 * An abstract multi joint device.
 */
public abstract class AbstractMultiJointDevice<J extends Joint, DD extends MultiJointDeviceDriver>
		extends AbstractActuator<DD> implements MultiJointDevice {

	private Frame base;

	/** The joints. */
	private final List<J> joints;

	private final Link[] links;

	/**
	 * Creates a new multi joint device with zero links.
	 */
	public AbstractMultiJointDevice(int jointCount) {
		this(jointCount, 0);
	}

	/**
	 * Creates a new multi joint device with a given number of links.
	 */
	public AbstractMultiJointDevice(int jointCount, int linkCount) {
		this.joints = new ArrayList<J>();

		for (int i = 0; i < jointCount; i++) {
			joints.add(null);
		}
		links = new Link[linkCount];
	}

	/**
	 * Sets the link for the given index.
	 *
	 * @param index the link index
	 * @param link  the new link
	 */
	protected final void setLink(int index, Link link) {
		this.links[index] = link;
	}

	protected abstract Link createLink(int index);

	@Override
	public final Frame getBase() {
		return base;
	}

	@Override
	public final Link getLink(int nr) {
		if (links == null || nr < 0 || nr >= links.length) {
			return null;
		}
		return links[nr];
	}

	@Override
	public final Link[] getLinks() {
		return this.links;
	}

	@Override
	@ConfigurationProperty(Optional = true)
	public final void setBase(Frame base) {
		immutableWhenInitialized();
		this.base = base;
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNotNullAndInitialized("base", base);
		checkNoParent("base", base);

		checkNotNullAndInitialized("joint", joints);
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		base = fill("base", base, new Frame(name("Base")), createdObjects);

		for (int i = 0; i < joints.size(); i++) {
			J joint = getJoint(i);

			if (joint == null) {
				joint = createJoint(i, name("Joint", i));
			}

			if (!joint.isInitialized()) {
				createdObjects.put("jointDriver[" + i + "]", joint.getDriver());
				fill("joint", joints, i, joint, createdObjects);
			}
		}
		fillAutomaticMultiJointDeviceProperties(createdObjects);

		// Needs to be here because super function invoces this class'
		// createLink method
		super.fillAutomaticConfigurationProperties(createdObjects);

		for (int i = 0; i < links.length; i++) {
			Link link = links[i];

			if (link == null) {
				link = createLink(i);
			}

			if (!link.isInitialized()) {
				link.setName(getName() + " Link " + i);
				links[i] = link;
				createdObjects.put("link[" + i + "]", link);
			}
		}
	}

	/**
	 * Fills automatic configuration properties of derived
	 * {@link MultiJointDevice}s. This method will be invoked after the base frame
	 * and all joints have been filled. However, it is invoked before the links have
	 * been filled.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #fillAutomaticConfigurationProperties(Map)
	 */
	protected void fillAutomaticMultiJointDeviceProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected final void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		for (int i = 0; i < links.length; i++) {
			if (createdObjects.containsKey("link[" + i + "]")) {
				links[i] = null;
			}
		}
		// clear("link", links, createdObjects);
		clearAutomaticMultiJointDeviceProperties(createdObjects);

		base = clear("base", base, createdObjects);
		clear("joint", joints, createdObjects);
	}

	/**
	 * Clears automatic configuration properties of derived
	 * {@link MultiJointDevice}s. This method will be invoked before the base frame
	 * and all joints have been cleared. However, it is invoked after the links have
	 * been cleared.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #clearAutomaticConfigurationProperties(Map)
	 */
	protected void clearAutomaticMultiJointDeviceProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		// TODO: Fix motion planning to work without BlendingParameter
		addDefaultParameters(new BlendingParameter(1));
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();
		base.setParent(this); // check such things in validate...()
		for (J joint : joints) {
			joint.setParent(this);
		}
		for (int i = 0; i < links.length; i++) {
			links[i].setParent(this);
		}
	}

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		for (int i = 0; i < links.length; i++) {
			links[i].setParent(null);
		}
		for (J joint : joints) {
			joint.setParent(null);
		}
		base.setParent(null);
		super.cleanupEntities();
	}

	@Override
	public final double[] getHomePosition() {
		if (!isInitialized()) {
			throw new IllegalStateException("Device " + getName() + " is not initialized");
		}

		double[] ret = new double[getJointCount()];
		for (int i = 0; i < getJointCount(); i++) {
			ret[i] = getJoint(i).getHomePosition();
		}
		return ret;
	}

	@Override
	public final J getJoint(int nr) {
		if (joints == null || nr < 0 || nr >= joints.size()) {
			return null;
		}

		return joints.get(nr);
	}

	@Override
	public final int getJointCount() {
		return joints.size();
	}

	@Override
	public final List<J> getJoints() {
		return Collections.unmodifiableList(joints);
	}

	@Override
	public final double[] getMeasuredJointAngles() throws RoboticsException {
		double[] result = new double[getJointCount()];

		for (int j = 0; j < getJointCount(); j++) {
			result[j] = getJoint(j).getMeasuredPosition();
		}

		return result;
	}

	/**
	 * Sets the joint for the given index.
	 *
	 * @param index the joint index
	 * @param joint the new joint
	 */
	@ConfigurationProperty(Optional = true)
	public void setJoint(int index, J joint) {
		this.joints.set(index, joint);
	}

	@Override
	public abstract void validateParameters(DeviceParameters parameters) throws InvalidParametersException;

	protected abstract J createJoint(int number, String name);

	@Override
	public final double[] getJointAngles() throws RoboticsException {
		double[] angles = getDriver().getJointAngles();
		if (angles != null) {
			return angles;
		}
		double[] result = new double[getJointCount()];

		for (int j = 0; j < getJointCount(); j++) {
			result[j] = getJoint(j).getCommandedPosition();
		}

		return result;
	}

	@Override
	public DoubleSensor[] getJointSensors() {
		DoubleSensor[] sensors = new DoubleSensor[getJointCount()];

		for (int i = 0; i < sensors.length; i++) {
			sensors[i] = getJoint(i).getCommandedPositionSensor();
		}

		return sensors;
	}

	protected static <T extends JointDriver> RevoluteJoint<T> createRevoluteJoint(T driver, String name,
			double minimumPosition, double maximumPosition, double maximumVelocity, double maximumAcceleration) {
		return createRevoluteJoint(driver, name, minimumPosition, maximumPosition, maximumVelocity, maximumAcceleration,
				null, 0d);
	}

	protected static <T extends JointDriver> RevoluteJoint<T> createRevoluteJoint(T driver, String name,
			double minimumPosition, double maximumPosition, double maximumVelocity, double maximumAcceleration,
			Double maximumJerk) {
		return createRevoluteJoint(driver, name, minimumPosition, maximumPosition, maximumVelocity, maximumAcceleration,
				maximumJerk, 0d);
	}

	protected static <T extends JointDriver> RevoluteJoint<T> createRevoluteJoint(T driver, String name,
			double minimumPosition, double maximumPosition, double maximumVelocity, double maximumAcceleration,
			Double maximumJerk, double homePosition) {
		RevoluteJoint<T> joint = new RevoluteJoint<T>();
		joint.setDriver(driver);
		joint.setName(name);

		joint.setMinimumPosition(minimumPosition);
		joint.setMaximumPosition(maximumPosition);
		joint.setHomePosition(homePosition);

		joint.setMaximumVelocity(maximumVelocity);
		joint.setMaximumAcceleration(maximumAcceleration);

		if (maximumJerk != null) {
			joint.setMaximumJerk(maximumJerk);
		}

		return joint;
	}

	protected static <T extends JointDriver> PrismaticJoint<T> createPrismaticJoint(T driver, String name,
			double minimumPosition, double maximumPosition, double maximumVelocity, double maximumAcceleration) {
		return createPrismaticJoint(driver, name, minimumPosition, maximumPosition, maximumVelocity,
				maximumAcceleration, null, 0d);
	}

	protected static <T extends JointDriver> PrismaticJoint<T> createPrismaticJoint(T driver, String name,
			double minimumPosition, double maximumPosition, double maximumVelocity, double maximumAcceleration,
			Double maximumJerk) {
		return createPrismaticJoint(driver, name, minimumPosition, maximumPosition, maximumVelocity,
				maximumAcceleration, maximumJerk, 0d);
	}

	protected static <T extends JointDriver> PrismaticJoint<T> createPrismaticJoint(T driver, String name,
			double minimumPosition, double maximumPosition, double maximumVelocity, double maximumAcceleration,
			Double maximumJerk, double homePosition) {
		PrismaticJoint<T> joint = new PrismaticJoint<T>();
		joint.setDriver(driver);
		joint.setName(name);

		joint.setMinimumPosition(minimumPosition);
		joint.setMaximumPosition(maximumPosition);
		joint.setHomePosition(homePosition);

		joint.setMaximumVelocity(maximumVelocity);
		joint.setMaximumAcceleration(maximumAcceleration);

		if (maximumJerk != null) {
			joint.setMaximumJerk(maximumJerk);
		}

		return joint;
	}

	protected final JointDeviceParameters getJointDeviceParameters(final double positionMargin,
			final double velocityMargin) {
		JointParameters[] jointParameters = new JointParameters[getJointCount()];

		for (int i = 0; i < jointParameters.length; i++) {
			J joint = getJoint(i);
			jointParameters[i] = new JointParameters(joint.getMinimumPosition() + positionMargin,
					joint.getMaximumPosition() - positionMargin, joint.getMaximumVelocity() - velocityMargin,
					joint.getMaximumAcceleration(), joint.getMaximumJerk());
		}
		return new JointDeviceParameters(jointParameters);
	}

	@Override
	protected void setupDriver(DD driver) {
		driver.setup(joints);
	}

	@Override
	public Map<Relation, RelationSensor> getForwardKinematicsRelationMap(double[] joints) {
		Map<Relation, RelationSensor> ret = new HashMap<Relation, RelationSensor>();
		for (int i = 0; i < joints.length; i++) {
			J j = getJoint(i);
			Frame from = j.getFixedFrame();
			Frame to = j.getMovingFrame();
			ret.put(j.getJointConnection(),
					new RelationSensor(j.getForwardKinematicsSensor(DoubleSensor.fromValue(joints[i])), from, to));
		}
		return ret;
	}

	@Override
	public Map<Relation, RelationSensor> getForwardKinematicsRelationMap(DoubleSensor[] joints) {
		Map<Relation, RelationSensor> ret = new HashMap<Relation, RelationSensor>();
		for (int i = 0; i < joints.length; i++) {
			J j = getJoint(i);
			Frame from = j.getFixedFrame();
			Frame to = j.getMovingFrame();
			ret.put(j.getJointConnection(), new RelationSensor(j.getForwardKinematicsSensor(joints[i]), from, to));
		}
		return ret;
	}

}
