/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.MultiDependency.Builder;
import org.roboticsapi.core.world.AbstractPhysicalObject;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.relation.ConfiguredStaticConnection;

public abstract class Link extends AbstractPhysicalObject implements PhysicalObject {

	private final MultiDependency<Transformation> transformations;
	private final MultiDependency<ConfiguredStaticConnection> connections;

	public Link() {
		transformations = createMultiDependency("transformations");
		connections = createMultiDependency("connections", new Dependency.Builder<Integer>() {
			@Override
			public Integer create() {
				return transformations.get();
			}
		}, new Builder<ConfiguredStaticConnection>() {
			@Override
			public ConfiguredStaticConnection create(int n) {
				return new ConfiguredStaticConnection(getBase(), getLinked().get(n), transformations.get(n));
			}
		});
	}

	public Link(Transformation trans) {
		this(Arrays.asList(trans));
	}

	public Link(List<Transformation> transformations) {
		this();
		setLinkedCount(transformations.size());
		for (int i = 0; i < transformations.size(); i++) {
			setTransformation(i, transformations.get(i));
		}
	}

	public final void setLinkedCount(int size) {
		transformations.set(size);
	}

	public final int getLinkedCount() {
		return transformations.get();
	}

	public final void setTransformation(int index, Transformation transformation) {
		transformations.set(index, transformation);
	}

	public final Transformation getTransformation(int index) {
		return transformations.get(index);
	}

	/**
	 * Returns the {@link Link}'s linked {@link Frame}s.
	 *
	 * @return the {@link Link}'s linked {@link Frame}s
	 */
	public abstract List<Frame> getLinked();

	/**
	 * Returns the link's connections between base and linked frame.
	 *
	 * @return the link's connections between base and linked frame.
	 */
	public final List<Relation> getConnections() {
		List<Relation> ret = new ArrayList<>();
		for (ConfiguredStaticConnection conn : connections.getAll()) {
			ret.addAll(conn.getRelationships(Relation.class));
		}
		return ret;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getName() + "'";
	}

}
