/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

/**
 * Status of a net downloaded to an RCC
 */
public enum NetStatus {
	LOADING, // the net is loading
	REJECTED, // the net has been rejected
	READY, // the net is ready to run
	RUNNING, // the net is running
	CANCELLING, // the net is cancelling
	TERMINATED, // the net has completed
	SCHEDULED, // the net is scheduled for execution
	UNLOADED, // the net is no longer available
	ERROR, // an error as occured
	INVALID // there is a problem with the net
}
