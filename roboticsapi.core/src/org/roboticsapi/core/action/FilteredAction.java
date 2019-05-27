/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import org.roboticsapi.core.Action;

/**
 * A FilteredAction modifies the execution of a given Action by applying a
 * certain filter to values that are commanded by that Action.
 */
public abstract class FilteredAction extends WrappedAction {

	/**
	 * Instantiates a new FilteredAction.
	 * 
	 * @param filteredAction the Action whose commanded values are filtered
	 */
	public FilteredAction(Action filteredAction) {
		super(filteredAction);
	}

}
