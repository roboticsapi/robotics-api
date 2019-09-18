/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * A RealtimeBoolean depending on a Command.
 */
public abstract class CommandRealtimeBoolean extends RealtimeBoolean {

	public CommandRealtimeBoolean(Command scope) {
		super(scope);
	}

	public Command getCommand() {
		return getScope();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && getScope() == ((CommandRealtimeBoolean) obj).getScope();
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), getScope());
	}

	@Override
	public String toString() {
		return super.toString() + "<" + getScope() + ">";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
