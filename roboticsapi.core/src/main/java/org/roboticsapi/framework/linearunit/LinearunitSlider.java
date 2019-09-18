/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.linearunit;

import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.framework.multijoint.link.Link;

public class LinearunitSlider extends Link {

	private final Dependency<Frame> mountFrame;

	/**
	 * Creates a new LinearunitSlider with a base and a mount frame having z-up, and
	 * a specified moving direction (new z-axis after applying rotation
	 * movementDirection to base).
	 */
	public LinearunitSlider() {
		mountFrame = createDependency("mountFrame", new Frame("Slider mount frame"));
		setLinkedCount(1);
	}

	/**
	 * Creates a new LinearunitSlider with a base and a mount frame having z-up, and
	 * a specified moving direction (new z-axis after applying rotation
	 * movementDirection to base).
	 *
	 * @param baseMountFrameOffset the offset of the mount frame of the slider
	 *                             corresponding to its base
	 */
	public LinearunitSlider(Transformation baseMountFrameOffset) {
		this();
		setBaseMountFrameOffset(baseMountFrameOffset);
	}

	public final void setBaseMountFrameOffset(Transformation baseMountFrameOffset) {
		setTransformation(0, baseMountFrameOffset);
	}

	public final Transformation getBaseMountFrameOffset() {
		return getTransformation(0);
	}

	public final Frame getMountFrame() {
		return mountFrame.get();
	}

	@Override
	public final List<Frame> getLinked() {
		return Arrays.asList(getMountFrame());
	}

}
