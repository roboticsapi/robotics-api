/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.actuator.AbstractOnlineObject;

public class TestRoboticsObject extends AbstractOnlineObject {

	Dependency<String> optionalProperty;

	public TestRoboticsObject() {
		optionalProperty = createDependency("optionalProperty");
	}

	public String getOptionalProperty() {
		return optionalProperty.get();
	}

	@ConfigurationProperty(Optional = true)
	public void setOptionalProperty(String property) {
		optionalProperty.set(property);
	}

}
