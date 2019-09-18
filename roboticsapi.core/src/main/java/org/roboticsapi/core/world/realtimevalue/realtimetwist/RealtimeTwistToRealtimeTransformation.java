/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;

public class RealtimeTwistToRealtimeTransformation extends RealtimeTransformation {

	private final RealtimeTransformation initialTransformation;
	private final RealtimeTwist twist;

	/**
	 * Creates a transformation by integrating a twist
	 *
	 * @param initialTransformation initial transformation
	 * @param twist                 twist to displace transformation by (using
	 *                              moving frame as pivot and orientation)
	 */
	RealtimeTwistToRealtimeTransformation(RealtimeTransformation initialTransformation, RealtimeTwist twist) {
		this.initialTransformation = initialTransformation;
		this.twist = twist;
	}

	public RealtimeTransformation getInitialTransformation() {
		return initialTransformation;
	}

	public RealtimeTwist getTwist() {
		return twist;
	}

	@Override
	public boolean isAvailable() {
		return initialTransformation.isAvailable() && twist.isAvailable();
	}

}
