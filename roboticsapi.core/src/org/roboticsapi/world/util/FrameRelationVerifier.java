/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.EntitySet.Verifier;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;

public class FrameRelationVerifier implements Verifier {

	private final boolean acceptFrames;
	private final boolean acceptRelations;

	public FrameRelationVerifier(boolean acceptFrames, boolean acceptRelations) {
		this.acceptFrames = acceptFrames;
		this.acceptRelations = acceptRelations;
	}

	@Override
	public void acceptAdding(Entity child) throws EntityException {
		if (child instanceof Frame) {
			if (acceptFrames) {
				return;
			}
			throw new EntityException("Only relations can be added as child entities!");
		}

		if (child instanceof Relation) {
			if (acceptRelations) {
				return;
			}
			throw new EntityException("Only frames can be added as child entities!");
		}

		throw new EntityException("Only frames and relations can be added as child entities!");
	}

	@Override
	public void acceptRemoving(Entity child) throws EntityException {
		if (child instanceof Frame) {
			if (acceptFrames) {
				return;
			}
			throw new EntityException("Only relations can be removed as child entities!");
		}

		if (child instanceof Relation) {
			if (acceptRelations) {
				return;
			}
			throw new EntityException("Only frames can be removed as child entities!");
		}

		throw new EntityException("Only frames and relations can be removed as child entities!");
	}

}
