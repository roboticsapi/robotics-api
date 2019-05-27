/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import org.roboticsapi.tool.AbstractTool;

/**
 * Abstract implementation for {@link Gripper}s.
 * 
 * @param <GD> The gripper's driver
 */
public abstract class AbstractGripper<GD extends GripperDriver> extends AbstractTool<GD> implements Gripper {

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
	public final int getFingerCount() {
		return this.fingerCount;
	}

	@Override
	public final double getRecommendedWorkpieceWeight() {
		return this.recommendedWorkpieceWeight;
	}

}