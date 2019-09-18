/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;

public abstract class JavaRccExtension implements RoboticsObjectListener {

	public abstract void extend(JavaRccExtensionPoint extensionPoint);

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof JavaRccExtensionPoint) {
			extend((JavaRccExtensionPoint) object);
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {

	}

}
