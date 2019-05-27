/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;

public class FollowFrameMapperResult extends BaseActionMapperResult {

	public FollowFrameMapperResult(Action action, NetFragment fragment, ActionResult result, DataflowOutPort cancel) {
		super(action, fragment, result, cancel); // completed when cancelled
	}

}
