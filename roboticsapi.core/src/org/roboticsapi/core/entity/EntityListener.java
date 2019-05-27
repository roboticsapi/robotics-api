/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.entity;

// TODO: Write a documentation for this interface
public interface EntityListener {

	public abstract void onRelationAdded(ComposedEntity parent, Entity child);

	public abstract void onRelationRemoved(ComposedEntity parent, Entity child);

}
