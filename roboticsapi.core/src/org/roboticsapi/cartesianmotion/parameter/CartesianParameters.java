/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.parameter;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;

/**
 * Cartesian parameters for a robot.
 */
public class CartesianParameters implements DeviceParameters {

	private double maximumPositionAcceleration, maximumPositionVelocity, maximumPositionJerk,
			maximumRotationAcceleration, maximumRotationVelocity, maximumRotationJerk;

	/**
	 * Cartesian motion parameters
	 *
	 * @param maximumPositionVelocity     maximum position velocity (m/s)
	 * @param maximumPositionAcceleration maximum position acceleration (m/s^2)
	 * @param maximumPositionJerk         maximum position jerk (m/s^3)
	 * @param maximumRotationVelocity     maximum rotation velocity (rad/s)
	 * @param maximumRotationAcceleration maximum rotation acceleration (rad/s^2)
	 * @param maximumRotationJerk         maximum rotation jerk (rad/s^3)
	 */
	public CartesianParameters(final double maximumPositionVelocity, final double maximumPositionAcceleration,
			final double maximumPositionJerk, final double maximumRotationVelocity,
			final double maximumRotationAcceleration, final double maximumRotationJerk) {
		setMaximumPositionVelocity(maximumPositionVelocity);
		setMaximumPositionAcceleration(maximumPositionAcceleration);
		setMaximumPositionJerk(maximumPositionJerk);
		setMaximumRotationVelocity(maximumRotationVelocity);
		setMaximumRotationAcceleration(maximumRotationAcceleration);
		setMaximumRotationJerk(maximumRotationJerk);
	}

	/**
	 * Cartesian motion parameters
	 *
	 * @param maximumPositionVelocity     maximum position velocity (m/s)
	 * @param maximumPositionAcceleration maximum position acceleration (m/s^2)
	 * @param maximumRotationVelocity     maximum rotation velocity (rad/s)
	 * @param maximumRotationAcceleration maximum rotation acceleration (rad/s^2)
	 */
	public CartesianParameters(final double maximumPositionVelocity, final double maximumPositionAcceleration,
			final double maximumRotationVelocity, final double maximumRotationAcceleration) {
		this(maximumPositionVelocity, maximumPositionAcceleration, Double.POSITIVE_INFINITY, maximumRotationVelocity,
				maximumRotationAcceleration, Double.POSITIVE_INFINITY);
	}

	/**
	 * Retrieves the maximum velocity
	 *
	 * @return maximum velocity
	 */
	public double getMaximumPositionVelocity() {
		return maximumPositionVelocity;
	}

	/**
	 * Retrieves the maximum acceleration
	 *
	 * @return maximum acceleration
	 */
	public double getMaximumPositionAcceleration() {
		return maximumPositionAcceleration;
	}

	/**
	 * Retrieves the maximum rotation velocity
	 *
	 * @return maximum velocity
	 */
	public double getMaximumRotationVelocity() {
		return maximumRotationVelocity;
	}

	/**
	 * Retrieves the maximum rotation acceleration
	 *
	 * @return maximum acceleration
	 */
	public double getMaximumRotationAcceleration() {
		return maximumRotationAcceleration;
	}

	@ConfigurationProperty
	public void setMaximumPositionAcceleration(final double maximumPositionAcceleration) {
		this.maximumPositionAcceleration = maximumPositionAcceleration;
	}

	@ConfigurationProperty
	public void setMaximumPositionVelocity(final double maximumPositionVelocity) {
		this.maximumPositionVelocity = maximumPositionVelocity;
	}

	@ConfigurationProperty
	public void setMaximumRotationAcceleration(final double maximumRotationAcceleration) {
		this.maximumRotationAcceleration = maximumRotationAcceleration;
	}

	@ConfigurationProperty
	public void setMaximumRotationVelocity(final double maximumRotationVelocity) {
		this.maximumRotationVelocity = maximumRotationVelocity;
	}

	public double getMaximumPositionJerk() {
		return maximumPositionJerk;
	}

	@ConfigurationProperty
	public void setMaximumPositionJerk(double maximumPositionJerk) {
		this.maximumPositionJerk = maximumPositionJerk;
	}

	public double getMaximumRotationJerk() {
		return maximumRotationJerk;
	}

	@ConfigurationProperty
	public void setMaximumRotationJerk(double maximumRotationJerk) {
		this.maximumRotationJerk = maximumRotationJerk;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof CartesianParameters)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		CartesianParameters bounds = (CartesianParameters) boundingObject;

		return getMaximumPositionVelocity() <= bounds.getMaximumPositionVelocity()
				&& getMaximumPositionAcceleration() <= bounds.getMaximumPositionAcceleration()
				&& getMaximumRotationVelocity() <= bounds.getMaximumRotationVelocity()
				&& getMaximumRotationAcceleration() <= bounds.getMaximumRotationAcceleration();
	}

}
