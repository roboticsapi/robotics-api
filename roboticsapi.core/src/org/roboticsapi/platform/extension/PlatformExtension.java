/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.SingleDeviceInterfaceFactory;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.platform.Platform;
import org.roboticsapi.platform.activity.PlatformInterface;
import org.roboticsapi.platform.activity.PlatformInterfaceImpl;

public class PlatformExtension implements RoboticsObjectListener {

	public PlatformExtension() {
	}

	@Override
	public void onAvailable(final RoboticsObject object) {
		if (object instanceof Platform) {
			((Platform) object).addInterfaceFactory(new SingleDeviceInterfaceFactory<PlatformInterface>() {
				@Override
				protected PlatformInterface build() {
					return new PlatformInterfaceImpl(((Platform) object));
				}
			});
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {

	}

}
