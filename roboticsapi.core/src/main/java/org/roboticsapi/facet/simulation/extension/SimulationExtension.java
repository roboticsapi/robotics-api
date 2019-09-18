/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.simulation.extension;

import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.facet.simulation.SPositionedEntity;
import org.roboticsapi.facet.simulation.SVisualization;
import org.roboticsapi.facet.simulation.SWorld;

public class SimulationExtension extends AbstractRoboticsObjectBuilder {
	public SimulationExtension() {
		super(SWorld.class, SPositionedEntity.class, SVisualization.class);
	}
}
