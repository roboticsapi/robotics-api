/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.framework.robot.parameter.RobotTool;

public class RobotToolProperty implements ActivityProperty {
	private final RobotTool tool;

	public RobotTool getRobotTool() {
		return tool;
	}

	public RobotToolProperty(RobotTool tool) {
		super();
		this.tool = tool;
	}
}
