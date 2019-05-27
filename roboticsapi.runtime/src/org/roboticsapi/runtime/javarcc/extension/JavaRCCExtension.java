/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;

public abstract class JavaRCCExtension implements RoboticsObjectListener {

	public abstract void extend(JavaRCCExtensionPoint extensionPoint);

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof JavaRCCExtensionPoint) {
			extend((JavaRCCExtensionPoint) object);
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {

	}

}
