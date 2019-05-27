/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.javarcc.extension;

import org.roboticsapi.runtime.cartesianmotion.javarcc.interfaces.JCartesianPositionInterface;
import org.roboticsapi.runtime.cartesianmotion.javarcc.primitives.JCartesianMonitor;
import org.roboticsapi.runtime.cartesianmotion.javarcc.primitives.JCartesianPosition;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtension;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;

public class CartesianMotionJavaRCCExtension extends JavaRCCExtension {

	@Override
	public void extend(JavaRCCExtensionPoint ep) {
		ep.registerPrimitive("CartesianPosition::CartesianMonitor", JCartesianMonitor.class);
		ep.registerPrimitive("CartesianPosition::CartesianPosition", JCartesianPosition.class);

		ep.registerInterface("cartesianposition", JCartesianPositionInterface.class);
	}

}
