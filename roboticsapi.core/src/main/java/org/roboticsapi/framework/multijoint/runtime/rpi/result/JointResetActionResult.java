/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.result;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

/**
 * Action result describing a joint position to jump to.
 */
public class JointResetActionResult extends ActionResult {

	private final RealtimeDouble position;

	public JointResetActionResult(Action action, RealtimeBoolean completion, RealtimeDouble position) {
		super(action, completion);
		this.position = position;
	}

	public RealtimeDouble getPosition() {
		return position;
	}

}
