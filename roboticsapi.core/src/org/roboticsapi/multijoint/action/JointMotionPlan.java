/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import org.roboticsapi.core.action.Plan;

public interface JointMotionPlan extends Plan {

	public double[] getJointPositionsAt(double time);

	public double[] getJointVelocitiesAt(double time);

	public double[] getJointAccelerationsAt(double time);

}
