/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Quaternion;
import org.roboticsapi.core.world.Rotation;

public final class QuaternionToRealtimeRotation extends RealtimeRotation {

	private final RealtimeDouble xComponent;
	private final RealtimeDouble yComponent;
	private final RealtimeDouble zComponent;
	private final RealtimeDouble wComponent;

	QuaternionToRealtimeRotation(RealtimeDouble xComponent, RealtimeDouble yComponent, RealtimeDouble zComponent,
			RealtimeDouble wComponent) {
		super(xComponent, yComponent, zComponent, wComponent);

		this.xComponent = xComponent;
		this.yComponent = yComponent;
		this.zComponent = zComponent;
		this.wComponent = wComponent;
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

	public RealtimeDouble getWComponent() {
		return wComponent;
	}

	@Override
	public RealtimeDouble getQuaternionW() {
		return wComponent;
	}

	@Override
	public RealtimeDouble getQuaternionX() {
		return xComponent;
	}

	@Override
	public RealtimeDouble getQuaternionY() {
		return yComponent;
	}

	@Override
	public RealtimeDouble getQuaternionZ() {
		return zComponent;
	}

	@Override
	protected Rotation calculateCheapValue() {
		Double x = getXComponent().getCheapValue();
		Double y = getYComponent().getCheapValue();
		Double z = getZComponent().getCheapValue();
		Double w = getWComponent().getCheapValue();
		return (x == null || y == null || z == null || w == null) ? null : new Rotation(new Quaternion(x, y, z, w));
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && xComponent.equals(((QuaternionToRealtimeRotation) obj).xComponent)
				&& yComponent.equals(((QuaternionToRealtimeRotation) obj).yComponent)
				&& zComponent.equals(((QuaternionToRealtimeRotation) obj).zComponent)
				&& wComponent.equals(((QuaternionToRealtimeRotation) obj).wComponent);
	}

	@Override
	public int hashCode() {
		return classHash(xComponent, yComponent, zComponent, wComponent);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(xComponent, yComponent, zComponent, wComponent);
	}

	@Override
	public String toString() {
		return "rotation(X:" + xComponent + ", Y:" + yComponent + ", Z:" + zComponent + ", W:" + wComponent + ")";
	}
}
