/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;

public class ConfiguredPlacement extends AbstractConfiguredKnownRelation {

	@Override
	protected Relation createRelation(Frame from, Frame to) {
		return new Placement(from, to);
	}

}
