/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.mockclass.TestDevice;

public class DeviceEntityTest extends EntityTest {

	@Override
	protected Entity createEntity() {
		return new TestDevice();
	}

	@Override
	protected Property createValidProperty() {
		return new TestDevice.ValidTestProperty();
	}

	@Override
	protected Property createInvalidProperty() {
		return new TestDevice.InvalidTestProperty();
	}
}
