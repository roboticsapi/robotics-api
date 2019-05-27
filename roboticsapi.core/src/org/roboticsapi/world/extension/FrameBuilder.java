/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.extension;

import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Placement;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.WorldOrigin;
import org.roboticsapi.world.measurement.FrameMatchMeasurement;

public class FrameBuilder extends AbstractRoboticsObjectBuilder {

	public FrameBuilder() {
		super(Frame.class, WorldOrigin.class, StaticConnection.class, Placement.class, FrameMatchMeasurement.class);
	}

}
