/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.RuntimeCommand;

public abstract class ActionDynamicScopeRealtimeBoolean extends DynamicScopeRealtimeBoolean {

	@Override
	public RealtimeBoolean getScopedState(Command scope) {
		if (scope instanceof RuntimeCommand) {
			return getScopedState(((RuntimeCommand) scope).getAction(), scope);
		} else {
			return null;
		}
	}

	protected abstract RealtimeBoolean getScopedState(Action action, Command scope);

}
