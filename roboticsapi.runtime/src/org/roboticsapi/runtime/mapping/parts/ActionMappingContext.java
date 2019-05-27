/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import java.util.Map;

import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public class ActionMappingContext {
	public final DataflowOutPort cancelPort;
	public final DataflowOutPort overridePort;
	public final Map<PlannedAction<?>, Plan> actionPlans;

	public ActionMappingContext(DataflowOutPort cancelPort, DataflowOutPort overridePort,
			Map<PlannedAction<?>, Plan> actionPlans) {
		this.cancelPort = cancelPort;
		this.overridePort = overridePort;
		this.actionPlans = actionPlans;
	}
}