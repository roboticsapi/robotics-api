/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.visualization.property;

import org.roboticsapi.core.entity.Property;

public class VisualizationProperty implements Property {

	private final Visualisation model;

	public VisualizationProperty(Visualisation model) {
		this.model = model;
	}

	public Visualisation getModel() {
		return model;
	}
}
