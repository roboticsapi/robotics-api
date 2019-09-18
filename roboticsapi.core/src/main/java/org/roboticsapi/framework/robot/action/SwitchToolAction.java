/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.framework.robot.parameter.RobotTool;

/**
 * This action will switch the tool of robot arm
 */
public class SwitchToolAction extends Action {

	private final RobotTool tool;

	/**
	 * Gets the associated Robot Tool
	 *
	 * @return
	 */
	public RobotTool getRobotTool() {
		return tool;
	}

	/**
	 * Constructor
	 *
	 * @param tool Robot Tool to switch to
	 */
	public SwitchToolAction(RobotTool tool) {
		super(0, true, false);
		this.tool = tool;
	}
}
