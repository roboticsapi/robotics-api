/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class RealtimeTransformationIsNull extends RealtimeBoolean {

	private final RealtimeTransformation other;

	RealtimeTransformationIsNull(RealtimeTransformation other) {
		this.other = other;
	}

	public RealtimeTransformation getOther() {
		return other;
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

}
