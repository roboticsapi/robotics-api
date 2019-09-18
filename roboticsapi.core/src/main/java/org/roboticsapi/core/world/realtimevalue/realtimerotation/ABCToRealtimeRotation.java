/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;

public final class ABCToRealtimeRotation extends RealtimeRotation {

	private final RealtimeDouble aComponent;
	private final RealtimeDouble bComponent;
	private final RealtimeDouble cComponent;

	ABCToRealtimeRotation(RealtimeDouble aComponent, RealtimeDouble bComponent, RealtimeDouble cComponent) {
		super(aComponent, bComponent, cComponent);

		this.aComponent = aComponent;
		this.bComponent = bComponent;
		this.cComponent = cComponent;
	}

	public RealtimeDouble getAComponent() {
		return aComponent;
	}

	public RealtimeDouble getBComponent() {
		return bComponent;
	}

	public RealtimeDouble getCComponent() {
		return cComponent;
	}

	@Override
	public RealtimeDouble getA() {
		return getAComponent();
	}

	@Override
	public RealtimeDouble getB() {
		return getBComponent();
	}

	@Override
	public RealtimeDouble getC() {
		return getCComponent();
	}

	@Override
	protected Rotation calculateCheapValue() {
		Double a = getAComponent().getCheapValue();
		Double b = getBComponent().getCheapValue();
		Double c = getCComponent().getCheapValue();

		return (a == null || b == null || c == null) ? null : new Rotation(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && aComponent.equals(((ABCToRealtimeRotation) obj).aComponent)
				&& bComponent.equals(((ABCToRealtimeRotation) obj).bComponent)
				&& cComponent.equals(((ABCToRealtimeRotation) obj).cComponent);
	}

	@Override
	public int hashCode() {
		return classHash(aComponent, bComponent, cComponent);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(aComponent, bComponent, cComponent);
	}

	@Override
	public String toString() {
		return "rotation(A:" + aComponent + ", B:" + bComponent + ", C:" + cComponent + ")";
	}
}
