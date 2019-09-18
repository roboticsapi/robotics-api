/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.World;

public class FrameUtils {

	public static boolean isPathWithout(final Frame from, final Frame to, final Frame without, FrameTopology topology) {
		if (topology == null) {
			topology = World.getCommandedTopology();
		}
		return from.getRelationsTo(to, topology.without(without)) != null;
	}

	public static boolean isPathWithout(final Frame from, final Frame to, final Relation without,
			FrameTopology topology) {
		if (topology == null) {
			topology = World.getCommandedTopology();
		}
		return from.getRelationsTo(to, topology.without(without)) != null;
	}

}
