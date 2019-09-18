/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.result;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

/**
 * Action result combining action results for multiple joints
 */
public class MultiJointActionResult extends ActionResult {

	private final ActionResult[] jointResults;

	public MultiJointActionResult(Action action, RealtimeBoolean completion, ActionResult[] jointResults) {
		super(action, completion);
		this.jointResults = jointResults;
	}

	public MultiJointActionResult(Action action, RealtimeBoolean completion, List<ActionResult> jointResults) {
		this(action, completion, jointResults.toArray(new ActionResult[jointResults.size()]));
	}

	public ActionResult[] getJointResults() {
		return jointResults;
	}

}
