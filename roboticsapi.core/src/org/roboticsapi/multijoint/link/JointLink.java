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

public class JointLink extends Link {

	private final Joint baseJoint, linkedJoint;

	public JointLink(Joint baseJoint, Joint linkedJoint, Transformation transformation) {
		super(transformation);

		this.baseJoint = baseJoint;
		this.linkedJoint = linkedJoint;
	}

	@Override
	public Frame getBase() {
		return baseJoint.getMovingFrame();
	}

	@Override
	public Frame getLinked() {
		return linkedJoint.getFixedFrame();
	}

	public Joint getBaseJoint() {
		return baseJoint;
	}

	public Joint getLinkedJoint() {
		return linkedJoint;
	}

	@Override
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		// FIXME: Can't do anything...
	}

}
