/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;

public interface RuntimeSetup {

	public RoboticsRuntime createRuntime() throws InitializationException;

	public RoboticsRuntime createRuntime(String name) throws InitializationException;

	public void destroyRuntime(RoboticsRuntime rt) throws RoboticsException;

	public void registerExtensions(RoboticsContext context);

	public void unregisterExtensions(RoboticsContext context);

}
