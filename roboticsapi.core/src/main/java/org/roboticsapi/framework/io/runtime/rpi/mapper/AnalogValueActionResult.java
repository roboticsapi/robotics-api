/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

/**
 * Action result containing an analog value (for an output)
 */
public class AnalogValueActionResult extends ActionResult {

	private final RealtimeDouble value;

	public AnalogValueActionResult(Action action, RealtimeBoolean completion, RealtimeDouble analogValue) {
		super(action, completion);
		this.value = analogValue;
	}

	public RealtimeDouble getAnalogValue() {
		return value;
	}

}
