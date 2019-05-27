/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.multijoint.MultiJointDevice;

/**
 * An AllJointVelocitiesScaledMotion modifies the commanded values of a given
 * Motion by scaling the velocities of all joint linearly, such that no joint
 * exceeds its maximum velocity.
 */
public class AllJointVelocitiesScaledMotion extends JointVelocityGuardedMotion {

	/**
	 * Instantiates a new AllJointVelocitiesScaledMotion.
	 * 
	 * @param guardedMotion    the Motion to guard
	 * @param multiJointDevice the MultiJointDevice whose maximum joint velocities
	 *                         are respected
	 */
	public AllJointVelocitiesScaledMotion(Action guardedMotion, MultiJointDevice multiJointDevice) {
		super(guardedMotion, multiJointDevice);
	}

}
