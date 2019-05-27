/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.core.State;
import org.roboticsapi.world.Frame;

public interface PlannedCartesianActivity extends PlannedRtActivity {

	/**
	 * Creates a State that gets active when the Actuator's motion center Frame
	 * reaches the specified Frame during motion.
	 *
	 * @param f the Frame to reach
	 * @return State that gets active when the Frame has been reached, or null if
	 *         this will not happen
	 */
	public State getMotionTimeProgress(Frame f);

}
