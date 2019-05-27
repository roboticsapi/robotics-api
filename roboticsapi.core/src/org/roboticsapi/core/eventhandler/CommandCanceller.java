/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.TargetedEventHandler;

/**
 * An EventHandler setting the Cancel operation for the command it is attached
 * to.
 */
public class CommandCanceller extends TargetedEventHandler {

	public CommandCanceller(Command toCancel) {
		super(null, toCancel);
	}

	public CommandCanceller() {
		super();
	}

}
