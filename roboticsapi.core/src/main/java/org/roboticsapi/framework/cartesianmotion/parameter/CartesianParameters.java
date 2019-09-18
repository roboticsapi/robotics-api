/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.parameter;

import org.roboticsapi.core.DeviceParameters;

/**
 * Cartesian parameters for a robot.
 */
public class CartesianParameters implements DeviceParameters {

	private double maximumPositionAcceleration, maximumPositionVelocity, maximumPositionJerk,
			maximumRotationAcceleration, maximumRotationVelocity, maximumRotationJerk, allowedPositionError,
			allowedRotationError;

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
		this(maximumPositionVelocity, maximumPositionAcceleration, maximumPositionJerk, maximumRotationVelocity,
				maximumRotationAcceleration, maximumRotationJerk, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	/**
	 * Cartesian motion parameters
	 *
	 * @param maximumPositionVelocity     maximum position velocity (m/s)
	 * @param maximumPositionAcceleration maximum position acceleration (m/s^2)
	 * @param maximumPositionJerk         maximum position jerk (m/s^3)
	 * @param maximumRotationVelocity     maximum rotation velocity (rad/s)
	 * @param maximumRotationAcceleration maximum rotation acceleration (rad/s^2)
	 * @param maximumRotationJerk         maximum rotation jerk (rad/s^3)
	 * @param allowedPositionError        maximum allowed deviation between measured
	 *                                    and commanded position (m)
	 * @param allowedRotationError        maximum allowed deviation between measured
	 *                                    and commanded rotation (rad)
	 */
	public CartesianParameters(final double maximumPositionVelocity, final double maximumPositionAcceleration,
			final double maximumPositionJerk, final double maximumRotationVelocity,
			final double maximumRotationAcceleration, final double maximumRotationJerk,
			final double allowedPositionError, final double allowedRotationError) {
		setMaximumPositionVelocity(maximumPositionVelocity);
		setMaximumPositionAcceleration(maximumPositionAcceleration);
		setMaximumPositionJerk(maximumPositionJerk);
		setMaximumRotationVelocity(maximumRotationVelocity);
		setMaximumRotationAcceleration(maximumRotationAcceleration);
		setMaximumRotationJerk(maximumRotationJerk);
		setAllowedPositionError(allowedPositionError);
		setAllowedRotationError(allowedRotationError);
	}

	// /**
	// * Cartesian motion parameters
	// *
	// * @param maximumPositionVelocity
	// * maximum position velocity (m/s)
	// * @param maximumPositionAcceleration
	// * maximum position acceleration (m/s^2)
	// * @param maximumRotationVelocity
	// * maximum rotation velocity (rad/s)
	// * @param maximumRotationAcceleration
	// * maximum rotation acceleration (rad/s^2)
	// */
	// public CartesianParameters(final double maximumPositionVelocity, final
	// double maximumPositionAcceleration,
	// final double maximumRotationVelocity, final double
	// maximumRotationAcceleration) {
	// this(maximumPositionVelocity, maximumPositionAcceleration,
	// Double.POSITIVE_INFINITY, maximumRotationVelocity,
	// maximumRotationAcceleration, Double.POSITIVE_INFINITY,
	// Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	// }

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

	public void setMaximumPositionAcceleration(final double maximumPositionAcceleration) {
		this.maximumPositionAcceleration = maximumPositionAcceleration;
	}

	public void setMaximumPositionVelocity(final double maximumPositionVelocity) {
		this.maximumPositionVelocity = maximumPositionVelocity;
	}

	public void setMaximumRotationAcceleration(final double maximumRotationAcceleration) {
		this.maximumRotationAcceleration = maximumRotationAcceleration;
	}

	public void setMaximumRotationVelocity(final double maximumRotationVelocity) {
		this.maximumRotationVelocity = maximumRotationVelocity;
	}

	public double getMaximumPositionJerk() {
		return maximumPositionJerk;
	}

	public void setMaximumPositionJerk(double maximumPositionJerk) {
		this.maximumPositionJerk = maximumPositionJerk;
	}

	public double getMaximumRotationJerk() {
		return maximumRotationJerk;
	}

	public void setMaximumRotationJerk(double maximumRotationJerk) {
		this.maximumRotationJerk = maximumRotationJerk;
	}

	public double getAllowedPositionError() {
		return allowedPositionError;
	}

	public void setAllowedPositionError(double allowedPositionError) {
		this.allowedPositionError = allowedPositionError;
	}

	public double getAllowedRotationError() {
		return allowedRotationError;
	}

	public void setAllowedRotationError(double allowedRotationError) {
		this.allowedRotationError = allowedRotationError;
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
