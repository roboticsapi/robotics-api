/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.FilteredAction;
import org.roboticsapi.framework.multijoint.MultiJointDevice;

/**
 * A JointVelocityGuardedMotion modifies the commanded values of a given Motion
 * so that they don't exceed the maximum joint velocities of a Robot.
 */
public abstract class JointVelocityGuardedMotion extends FilteredAction implements JointSpaceAction {

	/** The robot. */
	private final MultiJointDevice multiJointDevice;

	/**
	 * Instantiates a new JointVelocityGuardedMotion.
	 * 
	 * @param guardedMotion    the Motion to guard
	 * @param multiJointDevice the Robot whose maximum joint velocities are
	 *                         respected
	 */
	public JointVelocityGuardedMotion(Action guardedMotion, MultiJointDevice multiJointDevice) {
		super(guardedMotion);
		this.multiJointDevice = multiJointDevice;

	}

	/**
	 * Gets the Robot whose maximum joint velocities are respected.
	 * 
	 * @return the MultiJointDevice
	 */
	public MultiJointDevice getMultiJointDevice() {
		return multiJointDevice;
	}

}
