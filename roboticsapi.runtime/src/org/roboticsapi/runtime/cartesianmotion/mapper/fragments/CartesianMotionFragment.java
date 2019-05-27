/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;

public interface CartesianMotionFragment {

	public abstract DataflowOutPort getResultPort();

	public abstract Transformation getTransformationAt(double time);

	public abstract Twist getTwistAt(double time);

}