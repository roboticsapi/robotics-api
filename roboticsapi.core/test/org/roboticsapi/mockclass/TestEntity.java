/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.entity.AbstractEntity;
import org.roboticsapi.core.entity.Property;

public class TestEntity extends AbstractEntity {

	public static class InvalidTestProperty implements Property {

	}

	public static class ValidTestProperty implements Property {

	}

	public TestEntity() {

	}

}
