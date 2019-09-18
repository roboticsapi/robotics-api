/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.LogicalRelation;
import org.roboticsapi.core.world.Transformation;

/**
 * A Robotics API world model static connection (between two frames).
 *
 * StaticConnections are intended to be fixed, i.e. their {@link Transformation}
 * does not change and they are not removed during runtime of the application.
 */
public class StaticConnection extends LogicalRelation {

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public boolean isPersistent() {
		return true;
	}

	/**
	 * Creates a new static connection with the given {@link Transformation}.
	 *
	 * @param from the origin frame of this StaticConnection
	 * @param to   the target frame of this StaticConnection
	 */
	public StaticConnection(Frame from, Frame to) {
		super(from, to);
	}
}