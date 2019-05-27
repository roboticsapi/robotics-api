/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

/**
 * Defines the possible execution states of an {@link Activity}.
 */
public enum ActivityStatus {
	NEW, // newly created, i.e. not yet started
	SCHEDULED, // started and scheduled for execution
	RUNNING, // started and running
	MAINTAINING, // completed, but still performing maintaining duties
	COMPLETED, // fully completed
	FAILED // failed during starting
	;
}
