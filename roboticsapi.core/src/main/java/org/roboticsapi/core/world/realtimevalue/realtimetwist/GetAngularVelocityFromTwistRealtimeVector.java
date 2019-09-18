/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class GetAngularVelocityFromTwistRealtimeVector extends RealtimeVector {

	private final RealtimeTwist twist;

	GetAngularVelocityFromTwistRealtimeVector(RealtimeTwist twist) {
		super(twist);
		this.twist = twist;
	}

	public RealtimeTwist getTwist() {
		return twist;
	}

	@Override
	protected Vector calculateCheapValue() {
		Twist cheapValue = getTwist().getCheapValue();
		return cheapValue == null ? null : cheapValue.getRotVel();
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && twist.equals(((GetAngularVelocityFromTwistRealtimeVector) obj).twist);
	}

	@Override
	public int hashCode() {
		return classHash(twist);
	}

	@Override
	public boolean isAvailable() {
		return twist.isAvailable();
	}

	@Override
	public String toString() {
		return "rotVel(" + twist + ")";
	}
}
