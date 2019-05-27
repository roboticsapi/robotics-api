/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.parameter;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.world.Frame;

/**
 * A BlendingParameter specifies at which progress a motion should be blended
 * into a successive motion.
 */
public class CartesianBlendingParameter implements DeviceParameters {

	private Double atProgress = null;
	private Frame blendFrame = null;
	private Double blendingPathDistance = null;

	private CartesianBlendingParameter() {
	}

	public static CartesianBlendingParameter AtProgress(Double progress) {
		CartesianBlendingParameter p = new CartesianBlendingParameter();
		p.atProgress = progress;
		return p;
	}

	public static CartesianBlendingParameter AtDistance(Double distance) {
		CartesianBlendingParameter p = new CartesianBlendingParameter();
		p.blendingPathDistance = distance;
		return p;

	}

	public static CartesianBlendingParameter AtFrame(Frame frame) {
		CartesianBlendingParameter p = new CartesianBlendingParameter();
		p.blendFrame = frame;
		return p;
	}

	/**
	 * Creates a new Blending Parameter that triggers blending at a specified motion
	 * progress.
	 *
	 * @param atProgress the motion progress after which to start blending (range
	 *                   0...1)
	 */
	public CartesianBlendingParameter(Double atProgress) {
		if (atProgress < 0 || atProgress > 1) {
			throw new IllegalArgumentException("Parameter 'atProgress' may only have values between 0 and 1");
		}

		this.atProgress = atProgress;

	}

	/**
	 * Creates a new Blending Parameter that triggers blending when reaching a
	 * specified Frame.
	 *
	 * @param blendFrame the Frame at which to start blending
	 */
	public CartesianBlendingParameter(Frame blendFrame) {
		this.blendFrame = blendFrame;
	}

	/**
	 * Gets the value indicating the motion progress at which blending should start.
	 *
	 * @return the motion progress after which to start blending
	 */
	public Double getAtProgress() {
		return atProgress;
	}

	/**
	 * Gets the value indicating the Frame at which blending should start.
	 *
	 * @return the Frame at which to start blending
	 */
	public Frame getBlendFrame() {
		return blendFrame;
	}

	public Double getBlendingPathDistance() {
		return blendingPathDistance;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof CartesianBlendingParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		CartesianBlendingParameter bound = (CartesianBlendingParameter) boundingObject;

		return atProgress == null || atProgress <= bound.atProgress;
	}

}
