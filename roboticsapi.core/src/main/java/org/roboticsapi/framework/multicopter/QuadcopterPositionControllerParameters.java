/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multicopter;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class QuadcopterPositionControllerParameters implements DeviceParameters {

	RealtimeDouble kPos = RealtimeDouble.createFromConstant(3);
	RealtimeDouble kVel = RealtimeDouble.createFromConstant(2);
	RealtimeDouble kYaw = RealtimeDouble.createFromConstant(1);

	public QuadcopterPositionControllerParameters(RealtimeDouble k_pos, RealtimeDouble k_vel, RealtimeDouble k_yaw) {
		this.kPos = k_pos;
		this.kVel = k_vel;
		this.kYaw = k_yaw;
	}

	public QuadcopterPositionControllerParameters(double k_pos, double k_vel, double k_yaw) {
		this.kPos = RealtimeDouble.createFromConstant(k_pos);
		this.kVel = RealtimeDouble.createFromConstant(k_vel);
		this.kYaw = RealtimeDouble.createFromConstant(k_yaw);
	}

	public RealtimeDouble getPosGain() {
		return kPos;
	}

	public RealtimeDouble getVelGain() {
		return kVel;
	}

	public RealtimeDouble getYawGain() {
		return kYaw;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		return true;
	}

}
