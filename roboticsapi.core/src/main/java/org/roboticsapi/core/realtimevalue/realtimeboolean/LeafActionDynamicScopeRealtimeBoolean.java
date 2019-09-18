/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.action.WrappedAction;

public abstract class LeafActionDynamicScopeRealtimeBoolean extends ActionDynamicScopeRealtimeBoolean {

	@Override
	protected RealtimeBoolean getScopedState(Action action, Command scope) {
		if (action instanceof WrappedAction) {
			return getScopedState(((WrappedAction) action).getWrappedAction(), scope);
		} else {
			return getState(action, scope);
		}
	}

	protected abstract RealtimeBoolean getState(Action action, Command scope);

}
