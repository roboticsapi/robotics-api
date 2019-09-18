/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.RelationshipChangeSet;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;

public class ConfiguredStaticConnection extends AbstractConfiguredKnownRelation {

	public ConfiguredStaticConnection() {
	}

	public ConfiguredStaticConnection(Frame from, Frame to, Transformation transformation) {
		super(from, to, transformation);
	}

	@Override
	protected Relation createRelation(Frame from, Frame to) {
		return new StaticConnection(from, to) {
			@Override
			public boolean canRemove(RelationshipChangeSet situation) {
				return !isInitialized();
			}
		};
	}

}
