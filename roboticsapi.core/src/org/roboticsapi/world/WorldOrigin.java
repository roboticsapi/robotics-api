/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.exception.RoboticsException;

public class WorldOrigin extends Frame {

	WorldOrigin(World owner, String name) {
		super();
		setName(name);

		try {
			setParent(owner);
		} catch (RoboticsException shouldNotHappen) {
		}
	}

	@Override
	public boolean isDeletable() {
		return false;
	}
}
