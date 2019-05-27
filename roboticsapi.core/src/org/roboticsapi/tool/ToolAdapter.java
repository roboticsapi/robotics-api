/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool;

import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;

public interface ToolAdapter extends PhysicalObject {

	public Tool getTool();

	public Frame getToolFrame();

	public ToolChanger getToolChanger();

}
