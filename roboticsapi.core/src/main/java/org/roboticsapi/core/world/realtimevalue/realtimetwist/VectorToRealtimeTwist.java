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

/**
 * Combines a velocity sensor from translational/rotational motion directions
 */
public final class VectorToRealtimeTwist extends RealtimeTwist {

	private final RealtimeVector translationVelocity;
	private final RealtimeVector rotationVelocity;

	VectorToRealtimeTwist(RealtimeVector translationVelocity, RealtimeVector rotationVelocity) {
		super(translationVelocity, rotationVelocity);
		this.translationVelocity = translationVelocity;
		this.rotationVelocity = rotationVelocity;
	}

	@Override
	public RealtimeVector getTranslationVelocity() {
		return translationVelocity;
	}

	@Override
	public RealtimeVector getRotationVelocity() {
		return rotationVelocity;
	}

	@Override
	protected Twist calculateCheapValue() {
		Vector currentTVel = getTranslationVelocity().getCheapValue();
		Vector currRVel = getRotationVelocity().getCheapValue();

		return (currentTVel == null || currRVel == null) ? null : new Twist(currentTVel, currRVel);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && rotationVelocity.equals(((VectorToRealtimeTwist) obj).rotationVelocity)
				&& translationVelocity.equals(((VectorToRealtimeTwist) obj).translationVelocity);
	}

	@Override
	public int hashCode() {
		return classHash(rotationVelocity, translationVelocity);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(rotationVelocity, translationVelocity);
	}

	@Override
	public String toString() {
		return "velocity(" + translationVelocity + ", " + rotationVelocity + ")";
	}
}
