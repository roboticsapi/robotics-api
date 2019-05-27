/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * A listener to be informed about the occurrence of states in command handlers.
 */
public interface StateValidator {
	/**
	 * Validates a given state.
	 * 
	 * @param event state to validate
	 * @return true if the state is valid
	 * @throws RoboticsException if the state is definitely invalid
	 */
	boolean validateState(State event) throws RoboticsException;
}
