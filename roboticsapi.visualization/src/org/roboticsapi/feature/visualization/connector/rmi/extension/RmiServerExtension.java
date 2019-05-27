/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector.rmi.extension;

import org.roboticsapi.feature.visualization.connector.RoboticsObjectVisualizationServer;
import org.roboticsapi.feature.visualization.connector.extension.VisualizationLookupServerExtension;
import org.roboticsapi.feature.visualization.connector.rmi.RmiServer;
import org.roboticsapi.world.World;

public class RmiServerExtension extends VisualizationLookupServerExtension {

	@Override
	public RoboticsObjectVisualizationServer createServer(World world) throws Exception {
		return new RmiServer(world);
	}

}
