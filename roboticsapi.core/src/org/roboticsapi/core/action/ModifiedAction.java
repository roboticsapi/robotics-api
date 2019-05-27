/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import org.roboticsapi.core.Action;

public abstract class ModifiedAction<A extends Action> extends Action {

	private final A innerAction;

	public ModifiedAction(A innerAction) {
		super(innerAction.getWatchdogTimeout());
		this.innerAction = innerAction;

	}

	public ModifiedAction(A innerAction, double watchdogTimeout) {
		super(watchdogTimeout);
		this.innerAction = innerAction;

	}

	public A getInnerAction() {
		return innerAction;
	}

}
