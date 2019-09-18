/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.extension;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.WorldOrigin;
import org.roboticsapi.core.world.estimation.SimpleEstimator;
import org.roboticsapi.core.world.estimation.TimeAwareEstimator;
import org.roboticsapi.core.world.observation.FrameObservation;
import org.roboticsapi.core.world.relation.ConfiguredPlacement;
import org.roboticsapi.core.world.relation.ConfiguredStaticConnection;
import org.roboticsapi.core.world.relation.ConfiguredUnknownPlacement;
import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;

public class WorldExtension extends AbstractRoboticsObjectBuilder {

	public WorldExtension() {
		super(Frame.class, WorldOrigin.class, ConfiguredStaticConnection.class, ConfiguredPlacement.class,
				ConfiguredUnknownPlacement.class, FrameObservation.class, SimpleEstimator.class,
				TimeAwareEstimator.class);
	}

}
