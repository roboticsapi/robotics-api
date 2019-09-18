/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.relation.DynamicConnection;
import org.roboticsapi.core.world.relation.SingleDofConnection;

/**
 * A joint (controllable axis, e.g. revolute or prismatic)
 */
public abstract class AbstractJoint<JD extends JointDriver> extends AbstractActuator<JD> implements Joint {

	private final Dependency<Double> homePosition, minimumPosition, maximumPosition, maximumVelocity,
			maximumAcceleration;

	private final Dependency<Frame> fixedFrame;
	private final Dependency<Frame> movingFrame;
	private final Dependency<DynamicConnection> logicalRelation;
	private final Dependency<Double> maximumJerk;
	private final Dependency<Double> allowedPositionError;

	public AbstractJoint() {
		fixedFrame = createDependency("fixedFrame", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Fixed");
			}
		});
		movingFrame = createDependency("movingFrame", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Moving");
			}
		});

		homePosition = createDependency("homePosition", 0.);
		minimumPosition = createDependency("minimumPosition");
		maximumPosition = createDependency("maximumPosition");
		maximumVelocity = createDependency("maximumVelocity");
		maximumAcceleration = createDependency("maximumAcceleration");
		maximumJerk = createDependency("maximumJerk", Double.MAX_VALUE);
		allowedPositionError = createDependency("allowedPositionError", Double.MAX_VALUE);

		logicalRelation = createDependency("relation", new Dependency.Builder<DynamicConnection>() {
			@Override
			public DynamicConnection create() {
				return createConnection(fixedFrame.get(), movingFrame.get());
			}
		});

	}

	@Override
	public final Frame getFixedFrame() {
		return fixedFrame.get();
	}

	@Override
	public final Frame getMovingFrame() {
		return movingFrame.get();
	}

	@Override
	public DynamicConnection getLogicalRelation() {
		return logicalRelation.get();
	}

	/**
	 * Retrieves the maximum acceleration
	 *
	 * @return maximum acceleration of the joint
	 */
	@Override
	public final double getMaximumAcceleration() {
		return maximumAcceleration.get();
	}

	/**
	 * Sets the maximum acceleration (obligatory).
	 *
	 * @param maximumAcceleration maximum acceleration of the joint
	 */
	@ConfigurationProperty
	public final void setMaximumAcceleration(final double maximumAcceleration) {
		this.maximumAcceleration.set(maximumAcceleration);
	}

	/**
	 * Retrieves the maximum velocity
	 *
	 * @return maximum velocity of the joint
	 */
	@Override
	public final double getMaximumVelocity() {
		return maximumVelocity.get();
	}

	/**
	 * Sets the maximum velocity (obligatory).
	 *
	 * @param maximumVelocity maximum velocity of the joint
	 */
	@ConfigurationProperty
	public final void setMaximumVelocity(final double maximumVelocity) {
		this.maximumVelocity.set(maximumVelocity);
	}

	/**
	 * Retrieves the minimum position
	 *
	 * @return minimum position of the joint
	 */
	@Override
	public final double getMinimumPosition() {
		return minimumPosition.get();
	}

	/**
	 * Sets the minimum position (obligatory).
	 *
	 * @param minimumPosition minimum position of the joint
	 */
	@ConfigurationProperty
	public final void setMinimumPosition(final double minimumPosition) {
		this.minimumPosition.set(minimumPosition);
	}

	/**
	 * Retrieves the maximum position
	 *
	 * @return maximum position of the joint
	 */
	@Override
	public final double getMaximumPosition() {
		return maximumPosition.get();
	}

	/**
	 * Sets the maximum position (obligatory).
	 *
	 * @param maximumPosition maximum position of the joint
	 */
	@ConfigurationProperty
	public final void setMaximumPosition(final double maximumPosition) {
		this.maximumPosition.set(maximumPosition);
	}

	/**
	 * Retrieves the maximum allowed deviation between measured and commanded
	 * position of the joint (rad).
	 *
	 * @return maximum allowed deviation between measured and commanded position of
	 *         the joint (rad)
	 */
	@Override
	public final double getAllowedPositionError() {
		return allowedPositionError.get();
	}

	/**
	 * Sets the maximum allowed deviation between measured and commanded position of
	 * the joint (rad).
	 *
	 * @param allowedPositionError maximum allowed deviation between measured and
	 *                             commanded position of the joint (rad)
	 */
	@ConfigurationProperty
	public final void setAllowedPositionError(final double allowedPositionError) {
		this.allowedPositionError.set(allowedPositionError);
	}

	@Override
	public final double getHomePosition() {
		return homePosition.get();
	}

	/**
	 * Sets the home position (optional, auto-generated if not given).
	 *
	 * @param homePosition home position of the joint
	 */
	@ConfigurationProperty(Optional = true)
	public final void setHomePosition(double homePosition) {
		this.homePosition.set(homePosition);
	}

	/**
	 * Retrieves the maximum jerk.
	 *
	 * @return maximum jerk of the joint
	 */
	@Override
	public final double getMaximumJerk() {
		return maximumJerk.get();
	}

	/**
	 * Sets the maximum jerk.
	 *
	 * @param maximumJerk maximum jerk of the joint
	 */
	@ConfigurationProperty(Optional = true)
	public final void setMaximumJerk(final double maximumJerk) {
		this.maximumJerk.set(maximumJerk);
	}

	@Override
	public final RealtimeTransformation getRealtimeForwardKinematics(RealtimeDouble position) {
		return getTransformationSensor(position);
	}

	@Override
	public final double getCommandedPosition() throws RoboticsException {
		return getCommandedRealtimePosition().getCurrentValue();
	}

	@Override
	public final double getMeasuredPosition() throws RoboticsException {
		return getMeasuredRealtimePosition().getCurrentValue();
	}

	@Override
	public final RealtimeDouble getCommandedRealtimePosition() {
		return use(JointSensorInterface.class).getCommandedPositionSensor();
	}

	@Override
	public final RealtimeDouble getMeasuredRealtimePosition() {
		return use(JointSensorInterface.class).getMeasuredPositionSensor();
	}

	@Override
	public final RealtimeDouble getCommandedRealtimeVelocity() {
		return use(JointSensorInterface.class).getCommandedVelocitySensor();
	}

	@Override
	public final RealtimeDouble getMeasuredRealtimeVelocity() {
		return use(JointSensorInterface.class).getMeasuredVelocitySensor();
	}

	public abstract SingleDofConnection createConnection(Frame from, Frame to);

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
		// no maximum parameters known here
		// TODO: Really?
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	/**
	 * Sensor computing the Cartesian transformation for a given joint position
	 *
	 * @param position input position
	 * @return sensor for the transformation
	 */
	public abstract RealtimeTransformation getTransformationSensor(RealtimeDouble position);

}
