/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.util;

import org.roboticsapi.core.RoboticsObject;

public interface ConfigurationHook {

	public void onInputRead(String message);

	public void onInputReadingFailed(String message, Exception e);

	public void onBuilt(String id, String type);

	public void onBuildingFailed(String id, String type, String message);

	public void onConfigured(String id, String type);

	public void onConfiguringFailed(String id, String type, String key, String message);

	public void onInitialized(String id, RoboticsObject object);

	public void onInitializingFailed(String id, String type, String message, Exception innerException);

}