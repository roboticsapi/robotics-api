/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public interface CartesianSplineActivity extends PlannedActivity {

	/**
	 * Retrieves a State that is active when the spline motion has reached a certain
	 * spline point during execution.
	 *
	 * @param pointIndex the point index (0 means starting point, 1 denotes 1st
	 *                   point specified in spline definition)
	 * @return the state
	 */
	public RealtimeBoolean atSplinePoint(int pointIndex);
}
