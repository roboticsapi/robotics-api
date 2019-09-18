/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Frame;

public interface PlannedCartesianActivity extends PlannedActivity {

	/**
	 * Creates a State that gets active when the Actuator's motion center Frame
	 * reaches the specified Frame during motion.
	 *
	 * @param f the Frame to reach
	 * @return State that gets active when the Frame has been reached, or null if
	 *         this will not happen
	 */
	public RealtimeBoolean getMotionTimeProgress(Frame f);

}
