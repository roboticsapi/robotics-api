/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rcc.extension;

import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.facet.runtime.rcc.RccRuntime;

public class RccExtension extends AbstractRoboticsObjectBuilder {
	public RccExtension() {
		super(RccRuntime.class);
	}
}
