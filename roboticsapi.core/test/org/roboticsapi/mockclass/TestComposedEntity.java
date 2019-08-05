/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;

public class TestComposedEntity extends AbstractComposedEntity {

	boolean acceptAddingChild, acceptRemovingChild;

	public TestComposedEntity(boolean acceptAddingChild, boolean acceptRemovingChild) {
		super();
		this.acceptAddingChild = acceptAddingChild;
		this.acceptRemovingChild = acceptRemovingChild;
	}

	@Override
	protected void acceptAddingChild(Entity child) throws EntityException {
		if (!acceptAddingChild) {
			throw new EntityException("Not adding any child entities.");
		}
	}

	@Override
	protected void acceptRemovingChild(Entity child) throws EntityException {
		if (!acceptRemovingChild) {
			throw new EntityException("Not removing any child entities.");
		}
	}

}
