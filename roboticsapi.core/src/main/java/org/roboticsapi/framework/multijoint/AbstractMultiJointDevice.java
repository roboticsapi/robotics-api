/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Dependency.ChangeListener;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.actuator.AbstractPhysicalActuator;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.framework.multijoint.link.Link;
import org.roboticsapi.framework.multijoint.parameter.BlendingParameter;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.multijoint.parameter.JointParameters;

/**
 * An abstract multi joint device.
 */
public abstract class AbstractMultiJointDevice<J extends Joint, DD extends MultiJointDeviceDriver>
		extends AbstractPhysicalActuator<DD> implements MultiJointDevice {

	/** The joints. */
	private final MultiDependency<J> joints;

	private final MultiDependency<Link> links;

	/**
	 * Creates a new multi joint device.
	 */
	public AbstractMultiJointDevice() {
		joints = createMultiDependency("joints", new MultiDependency.Builder<J>() {
			@Override
			public J create(int index) {
				return createJoint(index, getName() + " Joint[" + index + "]");
			}
		});

		links = createMultiDependency("links", 0, new MultiDependency.Builder<Link>() {
			@Override
			public Link create(int index) {
				return createLink(index);
			}
		});
	}

	/**
	 * Creates a new multi joint device with a given number of links.
	 */
	public AbstractMultiJointDevice(int jointCount, int linkCount) {
		this();
		setJointCount(jointCount);
		setLinkCount(linkCount);
	}

	protected final void onJointCountChange(ChangeListener<Integer> changeListener) {
		joints.onChange(changeListener);
	}

	public final void setLinkCount(int linkCount) {
		links.set(linkCount);
	}

	public final void setJointCount(int jointCount) {
		joints.set(jointCount);
	}

	public final int getLinkCount() {
		return links.get();
	}

	protected abstract Link createLink(int index);

	protected abstract J createJoint(int number, String name);

	@Override
	public abstract void validateParameters(DeviceParameters parameters) throws InvalidParametersException;

	@Override
	public final Link getLink(int nr) {
		return links.get(nr);
	}

	@Override
	public final Link[] getLinks() {
		return links.getAll().toArray(new Link[0]);
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		// TODO: Fix motion planning to work without BlendingParameter
		addDefaultParameters(new BlendingParameter(1));
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
		return joints.get();
	}

	@Override
	public final List<J> getJoints() {
		return Collections.unmodifiableList(joints.getAll());
	}

	@Override
	public final double[] getMeasuredJointAngles() throws RoboticsException {
		double[] result = new double[getJointCount()];

		for (int j = 0; j < getJointCount(); j++) {
			result[j] = getJoint(j).getMeasuredPosition();
		}

		return result;
	}

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
	public RealtimeDouble[] getJointSensors() {
		RealtimeDouble[] sensors = new RealtimeDouble[getJointCount()];

		for (int i = 0; i < sensors.length; i++) {
			sensors[i] = getJoint(i).getCommandedRealtimePosition();
		}

		return sensors;
	}

	protected static <T extends JointDriver> RevoluteJoint<T> createRevoluteJoint(String name, double minimumPosition,
			double maximumPosition, double maximumVelocity, double maximumAcceleration) {
		return createRevoluteJoint(name, minimumPosition, maximumPosition, maximumVelocity, maximumAcceleration, null,
				0d);
	}

	protected static <T extends JointDriver> RevoluteJoint<T> createRevoluteJoint(String name, double minimumPosition,
			double maximumPosition, double maximumVelocity, double maximumAcceleration, Double maximumJerk) {
		return createRevoluteJoint(name, minimumPosition, maximumPosition, maximumVelocity, maximumAcceleration,
				maximumJerk, 0d);
	}

	protected static <T extends JointDriver> RevoluteJoint<T> createRevoluteJoint(String name, double minimumPosition,
			double maximumPosition, double maximumVelocity, double maximumAcceleration, Double maximumJerk,
			double homePosition) {
		RevoluteJoint<T> joint = new RevoluteJoint<T>();
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

	protected static <T extends JointDriver> PrismaticJoint<T> createPrismaticJoint(String name, double minimumPosition,
			double maximumPosition, double maximumVelocity, double maximumAcceleration) {
		return createPrismaticJoint(name, minimumPosition, maximumPosition, maximumVelocity, maximumAcceleration, 0d);
	}

	protected static <T extends JointDriver> PrismaticJoint<T> createPrismaticJoint(String name, double minimumPosition,
			double maximumPosition, double maximumVelocity, double maximumAcceleration, double homePosition) {
		PrismaticJoint<T> joint = new PrismaticJoint<T>();
		joint.setName(name);

		joint.setMinimumPosition(minimumPosition);
		joint.setMaximumPosition(maximumPosition);
		joint.setHomePosition(homePosition);

		joint.setMaximumVelocity(maximumVelocity);
		joint.setMaximumAcceleration(maximumAcceleration);
		return joint;
	}

	protected final JointDeviceParameters getJointDeviceParameters(final double positionMargin,
			final double velocityMargin) {
		JointParameters[] jointParameters = new JointParameters[getJointCount()];

		for (int i = 0; i < jointParameters.length; i++) {
			J joint = getJoint(i);
			jointParameters[i] = new JointParameters(
					joint.getMinimumPosition() + JointParameters.PRECISION_EPSILON + positionMargin,
					joint.getMaximumPosition() - JointParameters.PRECISION_EPSILON - positionMargin,
					joint.getMaximumVelocity() - velocityMargin, joint.getMaximumAcceleration(),
					joint.getAllowedPositionError());
		}
		return new JointDeviceParameters(jointParameters);
	}

	@Override
	public Map<Relation, RealtimeTransformation> getForwardKinematicsRelationMap(double[] joints) {
		Map<Relation, RealtimeTransformation> ret = new HashMap<Relation, RealtimeTransformation>();
		for (int i = 0; i < joints.length; i++) {
			J j = getJoint(i);
			for (Relation r : World.getCommandedTopology().getRelations(j.getFixedFrame())) {
				if (r.getTo() == j.getMovingFrame()) {
					ret.put(r, j.getRealtimeForwardKinematics(RealtimeDouble.createFromConstant(joints[i])));
				}
			}
		}
		return ret;
	}

	@Override
	public Map<Relation, RealtimeTransformation> getForwardKinematicsRelationMap(RealtimeDouble[] joints) {
		Map<Relation, RealtimeTransformation> ret = new HashMap<Relation, RealtimeTransformation>();
		for (int i = 0; i < joints.length; i++) {
			J j = getJoint(i);
			for (Relation r : World.getCommandedTopology().getRelations(j.getFixedFrame())) {
				if (r.getTo() == j.getMovingFrame()) {
					ret.put(r, j.getRealtimeForwardKinematics(joints[i]));
				}
			}
		}
		return ret;
	}
}
