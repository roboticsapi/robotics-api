/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public class RealtimeTransformationToRealtimeTwist extends RealtimeTwist {

	private final RealtimeTransformation transformation;

	RealtimeTransformationToRealtimeTwist(RealtimeTransformation transformation) {
		this.transformation = transformation;
	}

	public RealtimeTransformation getTransformation() {
		return transformation;
	}

	@Override
	public boolean isAvailable() {
		return transformation.isAvailable();
	}

}
