/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.world.AbstractPhysicalObject;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

/**
 * Abstract implementation for {@link GrippingFinger}s.
 */
public abstract class AbstractGrippingFinger extends AbstractPhysicalObject implements GrippingFinger {

	private final Dependency<Frame> tipFrame;

	public AbstractGrippingFinger() {
		tipFrame = createDependency("tipFrame", new Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Tip");
			}
		});
		createDependency("connection", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(getBase(), tipFrame.get());
			}
		});
		createDependency("position", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(getBase(), tipFrame.get(),
						new Transformation(getOffset(), 0, getLength(), 0, 0, 0));
			}
		});
	}

	@Override
	public final Frame getTipFrame() {
		return tipFrame.get();
	}

}
