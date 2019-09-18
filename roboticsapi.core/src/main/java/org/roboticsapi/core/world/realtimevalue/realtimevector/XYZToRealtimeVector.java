/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Vector;

public final class XYZToRealtimeVector extends RealtimeVector {

	@Override
	protected RealtimeVector performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new XYZToRealtimeVector(xComponent.substitute(substitutionMap), yComponent.substitute(substitutionMap),
				zComponent.substitute(substitutionMap));
	}

	private final RealtimeDouble xComponent;
	private final RealtimeDouble yComponent;
	private final RealtimeDouble zComponent;

	XYZToRealtimeVector(RealtimeDouble xComponent, RealtimeDouble yComponent, RealtimeDouble zComponent) {
		super(xComponent, yComponent, zComponent);

		this.xComponent = xComponent;
		this.yComponent = yComponent;
		this.zComponent = zComponent;
	}

	public RealtimeDouble getXComponent() {
		return xComponent;
	}

	public RealtimeDouble getYComponent() {
		return yComponent;
	}

	public RealtimeDouble getZComponent() {
		return zComponent;
	}

	@Override
	public RealtimeDouble getX() {
		return getXComponent();
	}

	@Override
	public RealtimeDouble getY() {
		return getYComponent();
	}

	@Override
	public RealtimeDouble getZ() {
		return getZComponent();
	}

	@Override
	protected Vector calculateCheapValue() {
		Double x = getX().getCheapValue();
		Double y = getY().getCheapValue();
		Double z = getZ().getCheapValue();

		if (x == null || y == null || z == null) {
			return null;
		}
		return new Vector(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && xComponent.equals(((XYZToRealtimeVector) obj).xComponent)
				&& yComponent.equals(((XYZToRealtimeVector) obj).yComponent)
				&& zComponent.equals(((XYZToRealtimeVector) obj).zComponent);
	}

	@Override
	public int hashCode() {
		return classHash(xComponent, yComponent, zComponent);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(xComponent, yComponent, zComponent);
	}

	@Override
	public String toString() {
		return "vector(" + xComponent + ", " + yComponent + ", " + zComponent + ")";
	}
}
