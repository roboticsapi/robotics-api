/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.parameter;

import org.roboticsapi.core.DeviceParameters;

/**
 * Joint parameters for a single joint
 */
public class JointParameters implements DeviceParameters {
	/**
	 * Joint limits are reduced by this amount to avoid exceeding due to rounding
	 * errors
	 */
	public static final double PRECISION_EPSILON = 1e-5;

	private double maximumAcceleration, maximumPosition, maximumVelocity, minimumPosition;
	private double allowedPositionError;

	/**
	 * Joint parameters
	 *
	 * @param minimumPosition      minimum position of the joint (rad)
	 * @param maximumPosition      maximum position of the joint (rad)
	 * @param maximumVelocity      maximum velocity of the joint (rad/s)
	 * @param maximumAcceleration  maximum acceleration of the joint (rad/s^2)
	 * @param allowedPositionError maximum allowed deviation between measured and
	 *                             commanded position of the joint (rad)
	 */
	public JointParameters(final double minimumPosition, final double maximumPosition, final double maximumVelocity,
			final double maximumAcceleration, final double allowedPositionError) {
		setAllowedPositionError(allowedPositionError);
		setMinimumPosition(minimumPosition);
		setMaximumVelocity(maximumVelocity);
		setMaximumPosition(maximumPosition);
		setMaximumAcceleration(maximumAcceleration);
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
	 * Retrieves the minimum position
	 *
	 * @return minimum position of the joint
	 */
	public double getMinimumPosition() {
		return minimumPosition;
	}

	public void setMaximumAcceleration(final double maximumAcceleration) {
		this.maximumAcceleration = maximumAcceleration;
	}

	public void setMaximumPosition(final double maximumPosition) {
		this.maximumPosition = maximumPosition;
	}

	public void setMaximumVelocity(final double maximumVelocity) {
		this.maximumVelocity = maximumVelocity;
	}

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
				&& getMaximumVelocity() <= bounds.getMaximumVelocity()
				&& getMinimumPosition() >= bounds.getMinimumPosition();
	}

	/**
	 * Returns the maximum allowed deviation between measured and commanded position
	 * of the joint (rad)
	 *
	 * @return the maximum allowed deviation between measured and commanded position
	 *         of the joint (rad)
	 */
	public double getAllowedPositionError() {
		return allowedPositionError;
	}

	/**
	 * Sets the maximum allowed deviation between measured and commanded position of
	 * the joint (rad)
	 *
	 * @param allowedPositionError the maximum allowed deviation between measured
	 *                             and commanded position of the joint (rad)
	 */
	public void setAllowedPositionError(double allowedPositionError) {
		this.allowedPositionError = allowedPositionError;
	}

}
