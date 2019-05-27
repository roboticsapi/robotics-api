/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.extension.Extension;

/**
 * The listener interface for receiving events regarding {@link Relation}s. The
 * class that is interested in processing a Relation event implements this
 * interface, and the object created with that class is registered with a
 * {@link Frame} using its <code>addRelationListener<code> method. When an event
 * regarding a Relation occurs, that object's appropriate method is invoked.
 * 
 */
public interface RelationListener extends Extension {

	/**
	 * Called when a {@link Relation} has been added to the {@link Frame} the
	 * listener has been added to.
	 * 
	 * @param addedRelation the added relation
	 * @param endpoint      the endpoint the newly added Relation points to
	 */
	public void relationAdded(Relation addedRelation, Frame endpoint);

	/**
	 * Called when a {@link Relation} was removed from the {@link Frame} the
	 * listener has been added to.
	 * 
	 * @param removedRelation the removed relation
	 * @param endpoint        the endpoint the removed Relation pointed to
	 */
	public void relationRemoved(Relation removedRelation, Frame endpoint);

}