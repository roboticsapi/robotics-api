/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.simulation;

import org.roboticsapi.core.world.mutable.MutableTransformation;
import org.roboticsapi.facet.simulation.SEntity;

public class SVelocityControlledAxis extends SEntity {
	private double pos = 0;
	private double vel = 0;
	private double cmdvel = 0;
	private final double weight;
	private MutableTransformation baseTrans;
	private MutableTransformation relativePosition = new MutableTransformation();

	public SVelocityControlledAxis(SEntity parent, MutableTransformation baseTrans, double startPos, double weight) {
		if (baseTrans == null)
			throw new IllegalArgumentException("baseTrans");
		setParent(parent);
		this.baseTrans = baseTrans;
		this.pos = startPos;
		this.weight = weight;
	}

	public double getMeasuredJointPosition() {
		return pos;
	}

	public double getMeasuredJointVelocity() {
		return vel;
	}

	public void setJointVelocity(double velocity) {
		cmdvel = velocity;
	}

	@Override
	public MutableTransformation getRelativePosition() {
		return relativePosition;
	}

	@Override
	public double getSimulationHz() {
		return 50;
	}

	@Override
	public void simulateStep(Long time) {
		double dt = 1 / getSimulationHz();
		vel = (1 - weight) * vel + weight * cmdvel;
		pos += vel * dt;

		relativePosition.setVectorEuler(0, 0, 0, pos, 0, 0);
		baseTrans.multiplyTo(relativePosition, relativePosition);
	}
}
