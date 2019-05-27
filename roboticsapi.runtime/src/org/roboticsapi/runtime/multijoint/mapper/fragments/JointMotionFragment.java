/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public interface JointMotionFragment {

	public abstract DataflowOutPort getResultPort();

	public abstract double[] getJointPositionsAt(double time);

	public abstract double[] getJointVelocitiesAt(double time);

	public abstract double[] getJointAccelerationsAt(double time);

}