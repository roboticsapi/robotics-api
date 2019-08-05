/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.RoboticsException;

public class MockTransactionCommand extends TransactionCommand {
	public MockTransactionCommand(RoboticsRuntime runtime) {
		super(runtime);
	}

	public MockTransactionCommand(RoboticsRuntime runtime, Command createWaitCommand) throws RoboticsException {
		super(runtime, createWaitCommand);
	}

	public MockTransactionCommand(String string, RoboticsRuntime runtime, Command createWaitCommand)
			throws RoboticsException {
		super(string, runtime, createWaitCommand);
	}
}
