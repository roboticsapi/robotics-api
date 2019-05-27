/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.parameter;

import org.roboticsapi.cartesianmotion.FrameProjector;
import org.roboticsapi.core.DeviceParameters;

public class FrameProjectorParameter implements DeviceParameters {

	private final FrameProjector projector;

	public FrameProjectorParameter(FrameProjector projector) {
		this.projector = projector;

	}

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		return true;
	}

	public FrameProjector getProjector() {
		return projector;
	}

}
