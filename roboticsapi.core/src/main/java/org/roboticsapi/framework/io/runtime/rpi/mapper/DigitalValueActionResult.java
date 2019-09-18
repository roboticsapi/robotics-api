/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.mapper;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;

/**
 * Action result containing a digital value (for an output)
 */
public class DigitalValueActionResult extends ActionResult {

	private final RealtimeBoolean value;

	public DigitalValueActionResult(Action action, RealtimeBoolean completion, RealtimeBoolean digitalValue) {
		super(action, completion);
		this.value = digitalValue;
	}

	public RealtimeBoolean getDigitalValue() {
		return value;
	}
}
