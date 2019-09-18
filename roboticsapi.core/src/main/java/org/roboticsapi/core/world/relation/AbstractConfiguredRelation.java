/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;

public abstract class AbstractConfiguredRelation extends AbstractRoboticsObject {
	private final Dependency<Frame> from, to;

	public AbstractConfiguredRelation() {
		from = createDependency("from");
		to = createDependency("to");
		createDependency("relation", new Dependency.Builder<Relation>() {
			@Override
			public Relation create() {
				return createRelation(from.get(), to.get());
			}
		});
	}

	public Frame getFrom() {
		return from.get();
	}

	@ConfigurationProperty
	public void setFrom(Frame from) {
		this.from.set(from);
	}

	public Frame getTo() {
		return to.get();
	}

	@ConfigurationProperty
	public void setTo(Frame to) {
		this.to.set(to);
	}

	protected abstract Relation createRelation(Frame from, Frame to);

}
