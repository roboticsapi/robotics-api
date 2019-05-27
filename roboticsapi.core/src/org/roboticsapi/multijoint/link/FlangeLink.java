/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.link;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

public class FlangeLink extends Link {

	private final Joint baseJoint;
	private final Frame linkedFrame;

	public FlangeLink(Joint baseJoint, Frame linkedFrame, Transformation transformation) {
		super(transformation);
		this.baseJoint = baseJoint;
		this.linkedFrame = linkedFrame;
	}

	@Override
	public Frame getBase() {
		return baseJoint.getMovingFrame();
	}

	@Override
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		// FIXME: Can't do anything...
	}

	@Override
	public Frame getLinked() {
		return linkedFrame;
	}

	public Joint getBaseJoint() {
		return baseJoint;
	}

}
