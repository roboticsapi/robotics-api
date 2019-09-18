/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;

public class Cluster {

	private static int count = 0;

	private final Set<Frame> frames = new HashSet<Frame>();
	private final Set<PhysicalObject> entities = new HashSet<PhysicalObject>();

	private final int id;

	public Cluster() {
		id = count++;
	}

	public int getIdentifier() {
		return id;
	}

	public boolean contains(Frame frame) {
		return frames.contains(frame);
	}

	public boolean contains(PhysicalObject object) {
		return entities.contains(object);
	}

	public boolean add(Frame frame) {
		return frames.add(frame);
	}

	public boolean add(PhysicalObject object) {
		return entities.add(object);
	}

	public boolean remove(Frame frame) {
		return frames.remove(frame);
	}

	public boolean remove(PhysicalObject object) {
		return entities.remove(object);
	}

	public Set<Frame> frames() {
		return Collections.unmodifiableSet(this.frames);
	}

	public Set<PhysicalObject> entities() {
		return Collections.unmodifiableSet(this.entities);
	}

	public boolean isEmpty() {
		return frames.isEmpty() && entities.isEmpty();
	}

}
