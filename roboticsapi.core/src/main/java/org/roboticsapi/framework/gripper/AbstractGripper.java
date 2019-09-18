/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.world.actuator.AbstractPhysicalActuator;

/**
 * Abstract implementation for {@link Gripper}s.
 *
 * @param <GD> The gripper's driver
 */
public abstract class AbstractGripper<GD extends GripperDriver> extends AbstractPhysicalActuator<GD> implements Gripper {

	private final int fingerCount;
	private final double recommendedWorkpieceWeight;

	/**
	 * Constructor.
	 *
	 * @param fingerCount                the gripper's finger count.
	 * @param recommendedWorkpieceWeight the gripper's recommended workpiece weight.
	 */
	public AbstractGripper(final int fingerCount, final double recommendedWorkpieceWeight) {
		this.fingerCount = fingerCount;
		this.recommendedWorkpieceWeight = recommendedWorkpieceWeight;
	}

	@Override
	public abstract GrippingFinger getFinger(int index);

	@Override
	public final Finger[] getFingers() {
		GrippingFinger[] fingers = new GrippingFinger[fingerCount];
		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = getFinger(i);
		}
		return fingers;
	}

	@Override
	public final int getFingerCount() {
		return fingerCount;
	}

	@Override
	public final double getRecommendedWorkpieceWeight() {
		return this.recommendedWorkpieceWeight;
	}

}