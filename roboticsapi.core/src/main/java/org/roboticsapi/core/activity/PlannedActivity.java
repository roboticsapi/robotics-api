/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public interface PlannedActivity extends Activity {

	/**
	 * Creates a RealtimeBoolean that becomes true when a certain part of the
	 * execution time of this Activity has been reached.
	 *
	 * @param progress the progress, in the range 0...1
	 * @return RealtimeBoolean representing the specified progress
	 */
	public abstract RealtimeBoolean getMotionTimeProgress(double progress);

}