/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.entity.AbstractEntity;
import org.roboticsapi.mockclass.TestRoboticsEntity;

public class RoboticsEntityTest extends AbstractRoboticsObjectTest<AbstractEntity> {

	@Override
	protected AbstractEntity createRoboticsObject(boolean init, boolean initError, boolean uninit,
			boolean uninitError) {
		return new TestRoboticsEntity(init, initError, uninit, uninitError);
	}

}
