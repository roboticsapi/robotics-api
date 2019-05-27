/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.parameter;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;

/**
 * Joint parameters for a single joint
 */
public class JointParameters implements DeviceParameters {
	private double maximumAcceleration, maximumPosition, maximumVelocity, minimumPosition, maximumJerk;

	/**
	 * Joint parameters
	 *
	 * @param minimumPosition     minimum position of the joint (rad)
	 * @param maximumPosition     maximum position of the joint (rad)
	 * @param maximumVelocity     maximum velocity of the joint (rad/s)
	 * @param maximumAcceleration maximum acceleration of the joint (rad/s^2)
	 */
	public JointParameters(final double minimumPosition, final double maximumPosition, final double maximumVelocity,
			final double maximumAcceleration) {
		this(minimumPosition, maximumPosition, maximumVelocity, maximumAcceleration, Double.MAX_VALUE);
	}

	/**
	 * Joint parameters
	 *
	 * @param minimumPosition     minimum position of the joint (rad)
	 * @param maximumPosition     maximum position of the joint (rad)
	 * @param maximumVelocity     maximum velocity of the joint (rad/s)
	 * @param maximumAcceleration maximum acceleration of the joint (rad/s^2)
	 * @param maximumJerk         maximum jerk of the joint (rad/s^3)
	 */
	public JointParameters(final double minimumPosition, final double maximumPosition, final double maximumVelocity,
			final double maximumAcceleration, final double maximumJerk) {
		setMinimumPosition(minimumPosition);
		setMaximumVelocity(maximumVelocity);
		setMaximumPosition(maximumPosition);
		setMaximumAcceleration(maximumAcceleration);
		setMaximumJerk(maximumJerk);
	}

	/**
	 * Retrieves the maximum acceleration
	 *
	 * @return maximum acceleration of the joint
	 */
	public double getMaximumAcceleration() {
		return maximumAcceleration;
	}

	/**
	 * Retrieves the maximum position
	 *
	 * @return maximum position of the joint
	 */
	public double getMaximumPosition() {
		return maximumPosition;
	}

	/**
	 * Retrieves the maximum velocity
	 *
	 * @return maximum velocity of the joint
	 */
	public double getMaximumVelocity() {
		return maximumVelocity;
	}

	/**
	 * Retrieves the maximum jerk
	 *
	 * @return maximum jerk of the joint
	 */
	public double getMaximumJerk() {
		return maximumJerk;
	}

	/**
	 * Retrieves the minimum position
	 *
	 * @return minimum position of the joint
	 */
	public double getMinimumPosition() {
		return minimumPosition;
	}

	@ConfigurationProperty
	public void setMaximumAcceleration(final double maximumAcceleration) {
		this.maximumAcceleration = maximumAcceleration;
	}

	@ConfigurationProperty
	public void setMaximumPosition(final double maximumPosition) {
		this.maximumPosition = maximumPosition;
	}

	@ConfigurationProperty
	public void setMaximumVelocity(final double maximumVelocity) {
		this.maximumVelocity = maximumVelocity;
	}

	@ConfigurationProperty
	public void setMaximumJerk(final double maximumJerk) {
		this.maximumJerk = maximumJerk;
	}

	@ConfigurationProperty
	public void setMinimumPosition(final double minimumPosition) {
		this.minimumPosition = minimumPosition;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof JointParameters)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		JointParameters bounds = (JointParameters) boundingObject;

		return getMaximumPosition() <= bounds.getMaximumPosition()
				&& getMaximumAcceleration() <= bounds.getMaximumAcceleration()
				&& getMaximumVelocity() <= bounds.getMaximumVelocity() && getMaximumJerk() <= bounds.getMaximumJerk()
				&& getMinimumPosition() >= bounds.getMinimumPosition();
	}

}
