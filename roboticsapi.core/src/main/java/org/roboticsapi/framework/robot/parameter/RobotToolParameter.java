/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.parameter;

import org.roboticsapi.core.DeviceParameters;

public class RobotToolParameter implements DeviceParameters {
	private final RobotTool tool;

	public RobotTool getRobotTool() {
		return tool;
	}

	public RobotToolParameter(RobotTool tool) {
		super();
		this.tool = tool;
	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof RobotToolParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		RobotToolParameter bounds = (RobotToolParameter) boundingObject;

		return tool.getJx() <= bounds.getRobotTool().getJx() && tool.getJy() <= bounds.getRobotTool().getJy()
				&& tool.getJz() <= bounds.getRobotTool().getJz() && tool.getMass() <= bounds.getRobotTool().getMass();
	}
}
