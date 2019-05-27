/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.Action;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.TrueFragment;
import org.roboticsapi.runtime.mapping.result.ActionResult;

public class GoalActionMapperResult extends BaseActionMapperResult {
	DataflowOutPort completed;

	public GoalActionMapperResult(Action action, NetFragment fragment, ActionResult result) {
		super(action, fragment, result, fragment.add(new TrueFragment()).getTrueOut());
	}

}
