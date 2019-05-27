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

public class BaseLink extends Link {

	private final Joint joint;
	private Frame base;

	public BaseLink(Frame base, Joint joint, Transformation transformation) {
		super(transformation);

		this.joint = joint;
		setBase(base);
	}

	@Override
	public Frame getBase() {
		return this.base;
	}

	@Override
	public Frame getLinked() {
		return joint.getFixedFrame();
	}

	public Joint getLinkedJoint() {
		return joint;
	}

	@Override
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.base = base;
	}

}
