/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.javarcc.extension;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.framework.gnss.javarcc.primitives.JSatelliteMonitor;

public class GNSSJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint extensionPoint) {
		extensionPoint.registerInterface("gnss", device -> new RpiParameters());
		extensionPoint.registerPrimitive("GNSS::SatelliteMonitor", JSatelliteMonitor.class);
	}

}
