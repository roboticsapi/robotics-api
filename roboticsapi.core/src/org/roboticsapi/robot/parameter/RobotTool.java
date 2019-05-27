/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.parameter;

import org.roboticsapi.world.Frame;

public class RobotTool {

	protected final Frame centerOfMass;
	protected final double mass;
	protected final double jx;
	protected final double jy;
	protected final double jz;

	public RobotTool(double mass, Frame centerOfMass) {
		this(mass, centerOfMass, 0, 0, 0);
	}

	public RobotTool(double mass, Frame centerOfMass, double jx, double jy, double jz) {
		super();
		this.mass = mass;
		this.centerOfMass = centerOfMass;
		this.jx = jx;
		this.jy = jy;
		this.jz = jz;
	}

	public double getMass() {
		return mass;
	}

	public double getJx() {
		return jx;
	}

	public double getJy() {
		return jy;
	}

	public double getJz() {
		return jz;
	}

	public Frame getCenterOfMass() {
		return centerOfMass;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RobotTool) {
			RobotTool other = (RobotTool) obj;

			return mass == other.mass && centerOfMass.equals(other.centerOfMass) && jx == other.jx && jy == other.jy
					&& jz == other.jz;
		}

		return false;
	}

}