/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.AlwaysActiveStatePortFactory;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;

public class DriveToMapperResult extends BaseActionMapperResult {

	public DriveToMapperResult(Action action, NetFragment fragment, ActionResult result) {
		super(action, fragment, result,
				// TODO: check if correct, cf. commented code below
				new AlwaysActiveStatePortFactory<Action.CompletedState>());
	}

	// @Override
	// public DataflowOutPort getStatePort(CompletedState event) {
	// return getStatePort(new Action.ActiveState());
	// }

}
