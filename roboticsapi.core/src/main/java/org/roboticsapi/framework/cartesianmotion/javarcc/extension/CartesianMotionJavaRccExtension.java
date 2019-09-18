/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.javarcc.extension;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.framework.cartesianmotion.javarcc.interfaces.JCartesianPositionInterface;
import org.roboticsapi.framework.cartesianmotion.javarcc.primitives.JCartesianMonitor;
import org.roboticsapi.framework.cartesianmotion.javarcc.primitives.JCartesianPosition;

public class CartesianMotionJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {
		ep.registerPrimitive("CartesianPosition::CartesianMonitor", JCartesianMonitor.class);
		ep.registerPrimitive("CartesianPosition::CartesianPosition", JCartesianPosition.class);

		ep.registerInterface("cartesianposition",
				(device) -> device instanceof JCartesianPositionInterface ? new RpiParameters() : null);
	}

}
