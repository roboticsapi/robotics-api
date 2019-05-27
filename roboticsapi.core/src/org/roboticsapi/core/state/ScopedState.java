/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.State;
import org.roboticsapi.core.util.HashCodeUtil;

public class ScopedState extends CommandState {
	private final State other;

	public ScopedState(Command scope, State other) {
		setCommand(scope);
		this.other = other;
	}

	public State getOther() {
		return other;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && getOther() != null && getOther().equals(((ScopedState) obj).getOther());
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), other);
	}

}
