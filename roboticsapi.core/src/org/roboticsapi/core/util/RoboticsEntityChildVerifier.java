/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.EntitySet.Verifier;

public class RoboticsEntityChildVerifier implements Verifier {

	RoboticsObject roboticsObject;

	public RoboticsEntityChildVerifier(RoboticsObject roboticsObject) {
		this.roboticsObject = roboticsObject;
	}

	@Override
	public void acceptAdding(Entity child) throws EntityException {
		if (this.roboticsObject.isInitialized()) {
			throw new EntityException("Cannot add new child entity when already initialized.");
		}
	}

	@Override
	public void acceptRemoving(Entity child) throws EntityException {
		if (this.roboticsObject.isInitialized()) {
			throw new EntityException("Cannot remove child entity when already initialized.");
		}
	}

}
