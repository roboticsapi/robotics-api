/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multicopter;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class QuadcopterHoverThrustParameters implements DeviceParameters {

	RealtimeDouble maxPitch;
	RealtimeDouble maxRoll;
	RealtimeDouble thrustIntegratorGain;
	RealtimeDouble pitchIntegratorGain;
	RealtimeDouble rollIntegratorGain;

	public QuadcopterHoverThrustParameters(RealtimeDouble maxPitch, RealtimeDouble maxRoll,
			RealtimeDouble thrustIntegratorGain, RealtimeDouble pitchIntegratorGain,
			RealtimeDouble rollIntegratorGain) {
		this.maxPitch = maxPitch;
		this.maxRoll = maxRoll;
		this.pitchIntegratorGain = pitchIntegratorGain;
		this.rollIntegratorGain = rollIntegratorGain;
		this.thrustIntegratorGain = thrustIntegratorGain;
	}

	public RealtimeDouble getMaxPitch() {
		return maxPitch;
	}

	public RealtimeDouble getMaxRoll() {
		return maxRoll;
	}

	public RealtimeDouble getPitchIntegratorGain() {
		return pitchIntegratorGain;
	}

	public RealtimeDouble getRollIntegratorGain() {
		return rollIntegratorGain;
	}

	public RealtimeDouble getThrustIntegratorGain() {
		return thrustIntegratorGain;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		return true;
	}

}
