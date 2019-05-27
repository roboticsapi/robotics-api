/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;

/**
 * Interface defining a strategy for dealing with errors during execution of
 * commands.
 */
public interface InnerExceptionStrategy {
	public void handleException(Command command, CommandRealtimeException exception) throws RoboticsException;
}
