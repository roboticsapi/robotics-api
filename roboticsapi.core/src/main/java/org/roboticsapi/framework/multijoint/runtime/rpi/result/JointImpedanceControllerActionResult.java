/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.result;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

public class JointImpedanceControllerActionResult extends ActionResult {

	private final RealtimeDouble[] stiffness, damping, addTorque;
	private final double[] maxTorques;

	public JointImpedanceControllerActionResult(Action action, RealtimeBoolean completion, int jointCount,
			RealtimeDouble[] stiffness, RealtimeDouble[] damping, RealtimeDouble[] addTorque, double[] maxTorques) {
		super(action, completion);
		this.maxTorques = maxTorques;
		if (stiffness.length != jointCount) {
			throw new IllegalArgumentException();
		}
		if (damping.length != jointCount) {
			throw new IllegalArgumentException();
		}
		if (addTorque.length != jointCount) {
			throw new IllegalArgumentException();
		}

		this.stiffness = stiffness;
		this.damping = damping;
		this.addTorque = addTorque;
	}

	public int getJointCount() {
		return stiffness.length;
	}

	public RealtimeDouble getStiffness(int joint) {
		return stiffness[joint];
	}

	public RealtimeDouble getDamping(int joint) {
		return damping[joint];
	}

	public RealtimeDouble getAddTorque(int joint) {
		return addTorque[joint];
	}

	public double[] getMaxTorques() {
		return maxTorques;
	}
}
