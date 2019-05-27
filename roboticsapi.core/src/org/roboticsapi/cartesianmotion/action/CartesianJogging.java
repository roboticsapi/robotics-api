/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;

/**
 * Manual jogging of robot in Cartesian space.
 */
public class CartesianJogging extends Action implements ProcessAction, CartesianPositionAction {

	private final Point pivotPoint;
	private final Orientation orientation;
	private final Frame referenceFrame;
	private final Frame joggedFrame;

	private final DoubleFromJavaSensor xSpeed;
	private final DoubleFromJavaSensor ySpeed;
	private final DoubleFromJavaSensor zSpeed;
	private final DoubleFromJavaSensor aSpeed;
	private final DoubleFromJavaSensor bSpeed;
	private final DoubleFromJavaSensor cSpeed;

	public CartesianJogging(RoboticsRuntime runtime, Frame joggedFrame, Frame referenceFrame, final Point pivotPoint,
			Orientation orientation) {
		super(0);

		this.joggedFrame = joggedFrame;
		this.referenceFrame = referenceFrame;
		this.pivotPoint = pivotPoint;
		this.orientation = orientation;

		if (joggedFrame.getRelationsTo(referenceFrame) == null) {
			throw new IllegalArgumentException("Jogged Frame and Reference Frame must be connected somehow");
		}

		if (joggedFrame.getRelationsTo(pivotPoint.getReferenceFrame()) == null) {
			throw new IllegalArgumentException("Jogged Frame and Pivot Point must be connected somehow");
		}

		if (joggedFrame.getRelationsTo(orientation.getReferenceFrame()) == null) {
			throw new IllegalArgumentException("Jogged Frame and Orientation must be connected somehow");
		}

		xSpeed = new DoubleFromJavaSensor(0);
		ySpeed = new DoubleFromJavaSensor(0);
		zSpeed = new DoubleFromJavaSensor(0);
		aSpeed = new DoubleFromJavaSensor(0);
		bSpeed = new DoubleFromJavaSensor(0);
		cSpeed = new DoubleFromJavaSensor(0);

	}

	public Frame getReferenceFrame() {
		return referenceFrame;
	}

	public Frame getJoggedFrame() {
		return joggedFrame;
	}

	public DoubleFromJavaSensor getxSpeed() {
		return xSpeed;
	}

	public DoubleFromJavaSensor getySpeed() {
		return ySpeed;
	}

	public DoubleFromJavaSensor getzSpeed() {
		return zSpeed;
	}

	public DoubleFromJavaSensor getaSpeed() {
		return aSpeed;
	}

	public DoubleFromJavaSensor getbSpeed() {
		return bSpeed;
	}

	public DoubleFromJavaSensor getcSpeed() {
		return cSpeed;
	}

	public Point getPivotPoint() {
		return pivotPoint;
	}

	public Orientation getOrientation() {
		return orientation;
	}

}
