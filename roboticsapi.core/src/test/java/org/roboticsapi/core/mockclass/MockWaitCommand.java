/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;

public class MockWaitCommand extends WaitCommand {

	protected MockWaitCommand(RoboticsRuntime runtime) {
		super(runtime);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CommandHandle createHandle() throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

}
