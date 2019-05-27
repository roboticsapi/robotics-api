/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;

public class DriveDirection extends Action {

	private final Orientation orientation;
	private final DoubleSensor xVel;
	private final DoubleSensor yVel;
	private final DoubleSensor aVel;
	private final Frame reference;

	public DriveDirection(Frame reference, Orientation orientation, DoubleSensor xVel, DoubleSensor yVel,
			DoubleSensor aVel) {
		super(0);
		this.reference = reference;
		this.orientation = orientation;
		this.xVel = xVel;
		this.yVel = yVel;
		this.aVel = aVel;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public DoubleSensor getxVel() {
		return xVel;
	}

	public DoubleSensor getyVel() {
		return yVel;
	}

	public DoubleSensor getaVel() {
		return aVel;
	}

	public Frame getReference() {
		return reference;
	}

}
