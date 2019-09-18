/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision.properties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.Property;
import org.roboticsapi.core.world.PhysicalObject;

/**
 * {@link Property} for defining collision friends, i.e. {@link PhysicalObject}s
 * where collisions can be ignored.
 */
public class CollisionFriendsProperty implements Property {

	private final Set<PhysicalObject> friends;

	public CollisionFriendsProperty(Set<PhysicalObject> friends) {
		this.friends = friends;
	}

	public CollisionFriendsProperty(PhysicalObject... friends) {
		this.friends = new HashSet<PhysicalObject>();

		for (PhysicalObject f : friends) {
			this.friends.add(f);
		}
	}

	public Set<PhysicalObject> getFriends() {
		return Collections.unmodifiableSet(friends);
	}

}
