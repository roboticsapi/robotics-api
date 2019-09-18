/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Status of a command TODO: find good/sufficient states
 */
public enum CommandStatus {
	READY, // the net is ready to run
	SCHEDULED, // the net is scheduled
	RUNNING, // the net is running
	TERMINATED, // the net has completed
	ERROR // an error as occured
}
