/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.core.State;

public interface CartesianSplineActivity extends PlannedRtActivity {

	/**
	 * Retrieves a State that is active when the spline motion has reached a certain
	 * spline point during execution.
	 * 
	 * @param pointIndex the point index (0 means starting point, 1 denotes 1st
	 *                   point specified in spline definition)
	 * @return the state
	 */
	public State atSplinePoint(int pointIndex);
}
