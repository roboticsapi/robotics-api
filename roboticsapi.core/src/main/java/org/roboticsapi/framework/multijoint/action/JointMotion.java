/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.action.PlannedAction;

/**
 * Abstract class for motions in joint space
 */
public abstract class JointMotion<P extends JointMotionPlan> extends PlannedAction<P> implements JointSpaceAction {

	public JointMotion(double watchdogTimeout) {
		super(watchdogTimeout, true, true);
	}

}
