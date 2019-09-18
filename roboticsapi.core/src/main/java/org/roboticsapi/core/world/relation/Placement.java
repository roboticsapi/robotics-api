/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.LogicalRelation;

/**
 * A Robotics API world model placement (between frames)
 */
public class Placement extends LogicalRelation {

	public Placement(Frame from, Frame to) {
		super(from, to);
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public boolean isPersistent() {
		return false;
	}

}
