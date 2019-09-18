/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.link;

import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;
import org.roboticsapi.framework.multijoint.Joint;

public class BaseLink extends Link {

	private final Joint joint;

	public BaseLink(Frame base, Joint joint, Transformation transformation) {
		super(transformation);
		if (base == null) {
			throw new IllegalArgumentException("base");
		}
		if (joint == null) {
			throw new IllegalArgumentException("joint");
		}
		this.joint = joint;
		setName("Link from " + base.getName() + " to " + joint.getName());

		// TODO: Resolve this hack: Joint creates a null-transformation to
		// simulate other base frame.
		createDependency("baseConnection", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(base, getBase());
			}
		});
		createDependency("basePosition", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(base, getBase(), Transformation.IDENTITY);
			}
		});
	}

	@Override
	public List<Frame> getLinked() {
		return Arrays.asList(joint.getFixedFrame());
	}

	public Joint getLinkedJoint() {
		return joint;
	}

}
