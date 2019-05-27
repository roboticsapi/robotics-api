/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import org.roboticsapi.core.State;

public interface PlannedRtActivity extends RtActivity {

	/**
	 * Creates a State that gets active when a certain part of the execution time of
	 * this Activity has been reached.
	 * 
	 * @param progress the progress, in the range 0...1
	 * @return State representing the specified progress
	 */
	public abstract State getMotionTimeProgress(double progress);

}