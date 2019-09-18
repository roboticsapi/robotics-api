/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Command;

public abstract class DynamicScopeRealtimeBoolean extends RealtimeBoolean {

	protected DynamicScopeRealtimeBoolean() {
		super();
	}

	@Override
	public RealtimeBoolean getForScope(Command command) {
		RealtimeBoolean scopedState = getScopedState(command);
		if (scopedState == null || scopedState == this) {
			return scopedState;
		}
		return scopedState.getForScope(command);
	}

	public abstract RealtimeBoolean getScopedState(Command scope);

	@Override
	public boolean isAvailable() {
		return true;
	}

}
