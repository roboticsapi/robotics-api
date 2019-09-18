/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.util;

import org.roboticsapi.core.RoboticsObject;

public class ConfigurationHookAdapter implements ConfigurationHook {

	@Override
	public void onInputRead(String message) {
		// empty impl
	}

	@Override
	public void onInputReadingFailed(String message, Exception e) {
		// empty impl
	}

	@Override
	public void onBuilt(String id, String type) {
		// empty impl
	}

	@Override
	public void onBuildingFailed(String id, String type, String message) {
		// empty impl
	}

	@Override
	public void onConfigured(String id, String type) {
		// empty impl
	}

	@Override
	public void onConfiguringFailed(String id, String type, String key, String message) {
		// empty impl
	}

	@Override
	public void onInitialized(String id, RoboticsObject object) {
		// empty impl
	}

	@Override
	public void onInitializingFailed(String id, String type, String message, Exception exception) {
		// empty impl
	}

}
