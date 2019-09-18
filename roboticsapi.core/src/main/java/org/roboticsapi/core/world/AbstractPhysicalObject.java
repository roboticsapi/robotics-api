/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.AbstractRoboticsEntity;
import org.roboticsapi.core.Dependency;

/**
 * A Robotics API world model object
 */
public abstract class AbstractPhysicalObject extends AbstractRoboticsEntity implements PhysicalObject {

	private final Dependency<Frame> base;

	public AbstractPhysicalObject() {
		base = createDependency("base", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Base");
			}
		});
	}

	@Override
	public final Frame getBase() {
		return base.get();
	}

}
